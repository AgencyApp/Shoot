package com.theshootapp.world.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theshootapp.world.ModelClasses.LocationModel;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationManagerProvider;

/**
 * Created by hamza on 24-Jun-18.
 */

public class UserLocation24Hrs extends Service {
   // Context context;
    DatabaseReference locationReference;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public UserLocation24Hrs() {
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationReference= FirebaseDatabase.getInstance().getReference().child("UserLocation");
        final GeoFire geoFire = new GeoFire(locationReference);
        long mLocTrackingInterval = 1000 * 60; // 5 sec
        float trackingDistance = 10f;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(getApplicationContext()).location(new LocationManagerProvider()).continuous().config(builder.build()).start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                LocationModel locationModel = new LocationModel(location.getLongitude(), location.getLatitude());
                sharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("longitude", String.valueOf(location.getLongitude()));
                editor.putString("latitude", String.valueOf(location.getLatitude()));
                editor.commit();
                //  locationReference.setValue(locationModel);
                geoFire.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
            }
        });
        return START_STICKY;
    }
}
