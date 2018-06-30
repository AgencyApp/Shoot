package com.theshootapp.world.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theshootapp.world.Activities.PhoneActivity;
import com.theshootapp.world.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AppointmentNotificationService extends Service {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    DatabaseReference locationReference;
    Bundle dataBundle1;
    Context c;

    public AppointmentNotificationService() {


    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    class getData extends AsyncTask{

        GeoLocation location;
        public getData(GeoLocation location)
        {
            this.location= location;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            final Bundle dataBundle = dataBundle1;
            final double latitude=dataBundle.getDouble("appointmentLatitude");
            final double longitude=dataBundle.getDouble("appointmentLongitude");
            try {

                StringBuilder response = new StringBuilder();

                String url1 = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + location.latitude + "," + location.longitude + "&destinations=" + latitude + "," + longitude + "&key=AIzaSyBknB7x4WpceoNpF1ykv0cJUeRJE7vqO-w";
                Log.d("URL",url1);
                URL url = new URL(url1);

                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()),8192);
                    String strLine = null;
                    while ((strLine = input.readLine()) != null)
                    {
                        response.append(strLine);
                    }
                    input.close();
                }

                JSONObject json = new JSONObject(response.toString());
                JSONArray routeArray = json.getJSONArray("rows");
                JSONObject routes = routeArray.getJSONObject(0);

                JSONArray newTempARr = routes.getJSONArray("elements");
                JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                JSONObject timeOb = newDisTimeOb.getJSONObject("duration");


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
                int notification_id = preferences.getInt("notification_id", 0);


                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(c, PhoneActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(c, notification_id, intent, 0);

                if(Double.parseDouble(distOb.getString("value"))<=50)
                {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c, "my_channel_01")
                            .setSmallIcon(R.drawable.ic_person_pin_circle_black_24dp)
                            .setContentTitle("Appointment Update")
                            .setContentText(dataBundle.getString("userName")+" has arrived ")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    return null ;
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c, "my_channel_01")
                        .setSmallIcon(R.drawable.ic_person_pin_circle_black_24dp)
                        .setContentTitle("Appointment Update")
                        .setContentText(dataBundle.getString("userName")+" is "+timeOb.getString("text")+" away ("+distOb.getString("text")+")")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
                notificationManager.notify(1, mBuilder.build());

                int count = dataBundle.getInt("count")+1;
                if(count>7)
                    return null;

                dataBundle.putInt("count",count);

                Intent intent1 = new Intent(AppointmentNotificationService.this, AppointmentNotificationService.class);
                intent1.putExtra("data",dataBundle);



                PendingIntent pintent = PendingIntent.getService(AppointmentNotificationService.this, notification_id+1, intent1, 0);
                AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 300*1000, pintent);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("notification_id",notification_id+2);
                editor.apply();



            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationReference= FirebaseDatabase.getInstance().getReference().child("UserLocation");
        final GeoFire geoFire = new GeoFire(locationReference);

        c=this;

        dataBundle1 = intent.getExtras().getBundle("data");





        ApplicationInfo app = null;
        try {
            /*app = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            final Bundle bundle = app.metaData;*/



            geoFire.getLocation(dataBundle1.getString("userId"), new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {
                    if (location != null) {
                        new getData(location).execute();

                    } else {
                        System.out.println(String.format("There is no location for key %s in GeoFire", key));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("There was an error getting the GeoFire location: " + databaseError);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;

    }
}
