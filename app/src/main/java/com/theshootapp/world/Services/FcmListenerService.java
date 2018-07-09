package com.theshootapp.world.Services;

/**
 * Created by hamza on 08-Jul-18.
 */

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.messaging.FirebaseMessagingService;
        import com.google.firebase.messaging.RemoteMessage;
        import com.google.firebase.storage.FirebaseStorage;
        import com.sinch.android.rtc.NotificationResult;
        import com.sinch.android.rtc.SinchHelpers;
        import com.sinch.android.rtc.calling.CallNotificationResult;
        import com.theshootapp.world.Activities.LoginActivity;
        import com.theshootapp.world.ModelClasses.UserProfile;
        import com.theshootapp.world.R;

        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.ServiceConnection;
        import android.os.IBinder;
        import android.support.annotation.NonNull;
        import android.support.v4.app.NotificationCompat;
        import android.util.Log;

        import java.util.Map;

public class FcmListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Map data = remoteMessage.getData();
        if (SinchHelpers.isSinchPushPayload(data)) {
            new ServiceConnection() {
                private Map payload;

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (payload != null) {
                        SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) service;
                        if (sinchService != null) {
                            NotificationResult result = sinchService.relayRemotePushNotificationPayload(payload);
                            // handle result, e.g. show a notification or similar
                            // here is example for notifying user about missed/canceled call:
                            if (result.isValid() && result.isCall()) {
                                CallNotificationResult callResult = result.getCallResult();
                                if (callResult.isCallCanceled()||callResult.isTimedOut()) {
                                    createNotification(callResult.getRemoteUserId());
                                }
                            }
                        }
                    }
                    payload = null;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {}

                public void relayMessageData(Map<String, String> data) {
                    payload = data;
                    getApplicationContext().bindService(new Intent(getApplicationContext(), SinchService.class), this, BIND_AUTO_CREATE);
                }
            }.relayMessageData(data);
        }
    }

    private void createNotification(String userId) {
        FirebaseDatabase.getInstance().getReference().child("User").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), LoginActivity.class), 0);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle("Missed call from:")
                                .setContentText(userProfile.getName()+"\n"+userProfile.getPhoneNumber());
                mBuilder.setContentIntent(contentIntent);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mBuilder.setAutoCancel(true);
                NotificationManager mNotificationManager =
                        (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
