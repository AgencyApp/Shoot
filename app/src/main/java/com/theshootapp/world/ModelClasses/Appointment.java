package com.theshootapp.world.ModelClasses;

public class Appointment {
    private String eventName;
    private String locationName;
    private long dayTimestamp;
    private String schedularUid;
    private String otherUid;
    private String schedularName;
    private String otherName;
    private String time;
    private Double lat;
    private Double lng;


    public Appointment() {
    }

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

    public long getDayTimestamp() {
        return dayTimestamp;
    }

    public void setDayTimestamp(long dayTimestamp) {
        this.dayTimestamp = dayTimestamp;
    }

    public String getSchedularUid() {
        return schedularUid;
    }

    public void setSchedularUid(String schedularUid) {
        this.schedularUid = schedularUid;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getSchedularName() {
        return schedularName;
    }

    public void setSchedularName(String schedularName) {
        this.schedularName = schedularName;
    }

    public String getOtherName() {
        return otherName;
    }

    public String getOtherUid() {
        return otherUid;
    }

    public void setOtherUid(String otherUid) {
        this.otherUid = otherUid;
    }

    public Appointment(String eventName, String locationName, long dayTimestamp, String schedularUid, String otherUid, String schedularName, String otherName, String time, Double lat, Double lng) {
        this.eventName = eventName;
        this.locationName = locationName;
        this.dayTimestamp = dayTimestamp;
        this.schedularUid = schedularUid;
        this.otherUid = otherUid;
        this.schedularName = schedularName;
        this.otherName = otherName;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
