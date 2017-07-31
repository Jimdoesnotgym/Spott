package com.example.chiangj.spott.models;


public class Place {
    private String mName;
    private String[] mTypes;
    private Object[] mPlacePhotos;
    private Object[] mAddressComponents;

    public Place(String name, String[] types, Object[] placePhotos, Object[] addressComponents){
        mName = name;
        mTypes = types;
        mPlacePhotos = placePhotos;
        mAddressComponents = addressComponents;
    }
}
