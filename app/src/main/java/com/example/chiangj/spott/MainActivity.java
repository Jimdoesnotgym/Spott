package com.example.chiangj.spott;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }else {
                retrieveLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "asking for permissions for fine location");
                    retrieveLocation();
                }
                else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                }
            }
        }
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

    private void createLocationRequest() {
        if(mLocationRequest == null){
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
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

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
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

    private void startLocationUpdates() {
        Log.d(TAG, "start location updates");
    }
}
