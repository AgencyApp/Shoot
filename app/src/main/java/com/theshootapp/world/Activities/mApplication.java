package com.theshootapp.world.Activities;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by abdul on 4/26/2018.
 */

public class mApplication extends Application {
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
