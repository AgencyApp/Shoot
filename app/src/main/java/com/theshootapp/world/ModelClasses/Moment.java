package com.theshootapp.world.ModelClasses;

/**
 * Created by hamza on 22-Jun-18.
 */

public class Moment {
    String ownerId;
    double longitude;
    double latitude;
    long timeStamp;

    public Moment(String ownerId, double longitude, double latitude, long timeStamp) {
        this.ownerId = ownerId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timeStamp = timeStamp;
    }

    public Moment() {
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
