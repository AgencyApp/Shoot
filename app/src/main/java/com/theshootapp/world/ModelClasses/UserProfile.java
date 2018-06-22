package com.theshootapp.world.ModelClasses;

/**
 * Created by hamza on 21-Jun-18.
 */

public class UserProfile {
    String name;
    String phoneNumber;

    public UserProfile() {
    }

    public UserProfile(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
