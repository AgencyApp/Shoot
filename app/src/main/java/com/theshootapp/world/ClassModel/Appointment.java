package com.theshootapp.world.ClassModel;

public class Appointment {
    private String eventName;
    private String locationName;
    private String lat;
    private String lng;
    private int timestamp;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Appointment(String eventName, String locationName, String lat, String lng, int timestamp, String personName) {
        this.eventName = eventName;
        this.locationName = locationName;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
        this.personName = personName;
    }

    public int gettimestamp() {
        return timestamp;
    }

    public void settimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    String personName;
}
