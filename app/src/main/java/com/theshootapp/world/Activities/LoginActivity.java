package com.theshootapp.world.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mukesh.permissions.AppPermissions;
import com.theshootapp.world.Manifest;
import com.theshootapp.world.R;

import java.util.ArrayList;
import java.util.Arrays;

import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.Utility.PermissionResultCallback;
import com.theshootapp.world.Utility.PermissionUtils;

public class LoginActivity extends AppCompatActivity  {
    FirebaseAuth auth;
    PermissionUtils permissionUtils;
    ArrayList<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        permissions=new ArrayList<>();
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(android.Manifest.permission.CAMERA);
        permissions.add(android.Manifest.permission.RECORD_AUDIO);
        permissions.add(android.Manifest.permission.READ_CONTACTS);
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        AppPermissions runtimePermission = new AppPermissions(this);
       if (runtimePermission.hasPermission(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.READ_CONTACTS,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION}))
       {
           if (auth.getCurrentUser() != null) {
               // already signed in
               checkProfile();
           } else {
               startActivityForResult(
                       AuthUI.getInstance()
                               .createSignInIntentBuilder()
                               .setAvailableProviders(Arrays.asList(
                                       new AuthUI.IdpConfig.PhoneBuilder().build()
                               ))
                               .build(),
                       123);
           }

       }
       else
           {

               runtimePermission.requestPermission(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 256);
           }


      /* if (auth.getCurrentUser() != null) {
            // already signed in
            checkProfile();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.PhoneBuilder().build()
                            ))
                            .build(),
                    123);
        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==256)
        {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    finish();
                    return;
                }
            }
            if (auth.getCurrentUser() != null) {
                // already signed in
                checkProfile();
            } else {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.PhoneBuilder().build()
                                ))
                                .build(),
                        123);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123&&resultCode==RESULT_OK)
        {
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("FCM_InstanceID").child(auth.getUid());
            dR.setValue(FirebaseInstanceId.getInstance().getToken());
            checkProfile();
        }
    }

    void checkProfile()
    {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("User").child(uid);
        dR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                if(userProfile==null)
                {
                    startActivity(new Intent(LoginActivity.this,UserProfileActivity.class));

                }
                else
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("UserId",dataSnapshot.getKey());
                    editor.putString("Phone",userProfile.getPhoneNumber() );
                    editor.putString("Name",userProfile.getName());
                    editor.commit();
                    startActivity(new Intent(LoginActivity.this,MainCameraActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
