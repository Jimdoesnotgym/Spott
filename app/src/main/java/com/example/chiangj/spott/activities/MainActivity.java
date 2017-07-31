package com.example.chiangj.spott.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.chiangj.spott.BuildConfig;
import com.example.chiangj.spott.R;
import com.example.chiangj.spott.apis.GooglePlacesApi;
import com.example.chiangj.spott.fragments.SongListFragment;
import com.example.chiangj.spott.models.Place;
import com.example.chiangj.spott.models.PlaceList;
import com.example.chiangj.spott.models.Song;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private final float MAP_ZOOM_LEVEL = 18.0f;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LatLng mCurrentLatLng;
    private boolean isRequestingLocationUpdates = false;
    private GoogleMap mGoogleMap;
    private Marker mPreviousMarker;
    private Marker mCurrentMarker;
    private CameraPosition mCurrentCameraPosition;
    private boolean isLocationChanged;
    private FloatingActionButton mButtonCenterMap;
    private boolean mIsFirstLaunch = true;
    private View mBottomSheetSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!hasPermissions()){
            appRequestPermissions();
        }else {
            initialize();
            queryPlaces();
        }

        mButtonCenterMap = (FloatingActionButton)findViewById(R.id.btn_center_map);
        mButtonCenterMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mCurrentMarker != null){
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, MAP_ZOOM_LEVEL));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if(mFusedLocationClient != null && mLocationCallback != null){
            stopLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && isResultsAllGranted(grantResults)){
                    Log.d(TAG, "asking for permissions for fine location");
                    initialize();
                }
                else {
                    appRequestPermissions();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CHECK_SETTINGS:
                if(resultCode == RESULT_OK){
                    Log.d(TAG, "REQUEST_CHECK_SETTINGS result ok");
                    startLocationUpdates();
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("cameraposition", mGoogleMap.getCameraPosition());
        outState.putBoolean("isFirstLaunch", false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onrestoreinstancestate");
        mCurrentCameraPosition = savedInstanceState.getParcelable("cameraposition");
        mIsFirstLaunch = savedInstanceState.getBoolean("isFirstLaunch");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady, setting map object");
        mGoogleMap = googleMap;

        if(mGoogleMap != null){
            mGoogleMap.setMaxZoomPreference(MAP_ZOOM_LEVEL);
        }
    }

    private void appRequestPermissions(){
        ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
    }

    private void initialize(){
        addSongListFragment();
        setUpBottomSheetSongList();
        setUpLocationCallback();
        setUpMap();
        retrieveLocation();
    }

    private void addSongListFragment() {
        final SongListFragment songListFragment = new SongListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.song_list_container, songListFragment, "SongListFragmentTag").commit();
    }

    private void setUpBottomSheetSongList() {
        mBottomSheetSongList = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetSongList);
        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //if(BuildConfig.DEBUG) Log.d(TAG, String.valueOf(newState));

                //***************************For Testing Only***************************//
                isLocationChanged = true;
                //**********************************************************************//

                switch (newState){
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if(isLocationChanged){
                            isLocationChanged = false;
                            //TODO query songs with rxJava and retrofit, parse, then update UI through adapter

                            List<Song> list = new ArrayList<Song>();
                            list.add(new Song("A", "1"));

                            //create your own listener and call method in songlistfragment here and pass
                            SongListFragment fragment = (SongListFragment) getSupportFragmentManager().findFragmentByTag("SongListFragmentTag");
                            fragment.updateAdapterList(list);
                        }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mButtonCenterMap.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
            }
        });
    }

    private void setUpLocationCallback() {
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    Log.d(TAG, String.valueOf(location.getLatitude()));
                    Log.d(TAG, String.valueOf(location.getLongitude()));
                    if((location != mCurrentLocation && mGoogleMap != null) || (mCurrentLocation == null && mGoogleMap != null)){

                        //TODO
                        /*************************************************************
                         Should use the following when can actually test while moving
                         if(mCurrentLocation != location){
                         mCurrentLocation = location;
                         Log.d(TAG, "update map");
                         updateMap();
                         }
                         *************************************************************/

                        if(mCurrentLocation != location) {
                            isLocationChanged = true;
                        }

                        mCurrentLocation = location;
                        if(BuildConfig.DEBUG) Log.d(TAG, "location changed, update map");
                        updateMap();
                    }
                }
            }
        };
    }

    private void setUpMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "getMapAsync");
        mapFragment.getMapAsync(this);
    }

    public void retrieveLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mCurrentLocation = location;
                        Log.d(TAG, "I have a location");
                        Toast.makeText(MainActivity.this,
                                "Lat: " + String.valueOf(location.getLatitude()) + "Lon: " + String.valueOf(location.getLongitude()),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, String.valueOf(location.getLatitude()));
                        Log.d(TAG, String.valueOf(location.getLongitude()));
                        createLocationRequest();
                    }
                }
            });
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private boolean hasPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(String permission: PERMISSIONS){
                if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isResultsAllGranted(int[] grantResults){
        for(int i: grantResults){
            if(i != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void updateMap() {
        mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        if(mIsFirstLaunch){
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, MAP_ZOOM_LEVEL));
            mIsFirstLaunch = false;
        }
        if(mCurrentMarker == null){
            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 17.0f));
            mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions().position(mCurrentLatLng).title("Current Location"));
        }else {
            mPreviousMarker = mCurrentMarker;
            mPreviousMarker.remove();
            mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions().position(mCurrentLatLng).title("Current Location"));
        }

    }

    private void createLocationRequest() {
        Log.d(TAG, "inside createLocationRequest");
        if(mLocationRequest == null){
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(2500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //initialize location requests here
                Log.d(TAG, "LocationSettingsResponse onSuccess, starting location updates");
                isRequestingLocationUpdates = true;
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //check e.getStatusCOde
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode){
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        //Location settings are not satisfied, show user dialog to fix this
                        try {
                            //Show the dialog by calling startResolutionForResult()
                            //and check the result in onActiityResult()
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        }catch (IntentSender.SendIntentException sendEx){

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Location settings are not satisfied but we have not way to fix the settings
                        //so we do not show the dialog
                        break;
                }
            }
        });
    }

    private void startLocationUpdates() {
        if(isRequestingLocationUpdates){
            Log.d(TAG, "start location updates");
            try{
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }catch (SecurityException e){
                e.printStackTrace();
            }
        }
    }

    private void stopLocationUpdates() {
       if(mFusedLocationClient != null){
           mFusedLocationClient.removeLocationUpdates(mLocationCallback);
       }
    }

    private void queryPlaces(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        GooglePlacesApi service = retrofit.create(GooglePlacesApi.class);
        service.getPlaceList("-33.8670522,151.1957362", "300", String.valueOf(R.string.google_maps_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlaceList>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull PlaceList placeList) {
                        if(placeList.results.size() == 0){
                            Log.d(TAG, "Jim2 " + placeList.results);
                            Log.d(TAG, "Jim4 " + placeList.status);
                        }
                        for(Object place : placeList.results){
                            Log.d(TAG, "Jim 3");
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG, e.getMessage() + " Jim", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
