package com.theshootapp.world.Services;

/**
 * Created by hamza on 24-Jun-18.
 */
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class InstanceIdGenerator extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        UploadToServer(FirebaseInstanceId.getInstance().getToken());
    }

    void UploadToServer(String key)
    {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dR = firebaseDatabase.getReference("FCM_InstanceID").child(firebaseUser.getUid());
            dR.setValue(key);
        }
    }


}
