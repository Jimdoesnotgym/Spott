package com.example.chiangj.spott.apis;

import com.example.chiangj.spott.models.Place;
import com.example.chiangj.spott.models.PlaceList;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GooglePlacesApi {
    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlaceList>getPlaceList(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String key
    );
}
