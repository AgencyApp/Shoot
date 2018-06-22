package com.theshootapp.world.ModelClasses;

/**
 * Created by hamza on 22-Jun-18.
 */

public class LocationModel {
    double longitude;
    double latitude;

    public LocationModel(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public LocationModel() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
