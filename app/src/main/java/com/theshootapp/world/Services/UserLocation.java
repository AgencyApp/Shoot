package com.theshootapp.world.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.Nullable;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theshootapp.world.ModelClasses.LocationModel;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

/**
 * Created by hamza on 22-Jun-18.
 */

public class UserLocation extends IntentService {
    DatabaseReference locationReference;
    SharedPreferences sharedPreferences;


    public UserLocation(String name) {
        super(name);
    }

    public UserLocation() {
        super("Location Service");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        locationReference= FirebaseDatabase.getInstance().getReference().child("UserLocation");
        final GeoFire geoFire = new GeoFire(locationReference);
        long mLocTrackingInterval = 1000 * 60; // 5 sec
        float trackingDistance = 10f;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(getApplicationContext()).location().continuous().config(builder.build()).start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                LocationModel locationModel=new LocationModel(location.getLongitude(),location.getLatitude());
                sharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("longitude",String.valueOf(location.getLongitude()) );
                editor.putString("latitude",String.valueOf(location.getLatitude()) );
                editor.commit();
                //locationReference.setValue(locationModel);
                geoFire.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });

            }
        });
    }
}
