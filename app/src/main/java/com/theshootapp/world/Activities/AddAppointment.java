package com.theshootapp.world.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.ModelClasses.LocationModel;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;
import com.theshootapp.world.Services.AppointmentNotificationService;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.HOUR_OF_DAY;


public class AddAppointment extends AppCompatActivity {

    EditText eventName;
    EditText time;
    TextView location;
    LatLng loc;
    Long dateTimestamp;
    FirebaseDatabase firebaseDatabase;
    String currentUid;
    AlertDialog.Builder alert;
    DatabaseReference userReference;
    DatabaseReference friends;
    AlertDialog.Builder alertError;
    ArrayAdapter<com.theshootapp.world.ModelClasses.User> userSpinnerAdapter;
    ArrayList <com.theshootapp.world.ModelClasses.User>userFriends;
    int userSelected = 0;
    static int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        eventName = findViewById(R.id.eventName);
        time = findViewById(R.id.eventTime);
        location = findViewById(R.id.location);

        dateTimestamp = getIntent().getLongExtra("date",0);

        Spinner userSpinner = findViewById(R.id.userSpinner);

        firebaseDatabase=FirebaseDatabase.getInstance();
        currentUid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        alert = new AlertDialog.Builder(this);
        alertError = new AlertDialog.Builder(this);
        userReference= FirebaseDatabase.getInstance().getReference().child("User");
        friends=FirebaseDatabase.getInstance().getReference().child("UserFriends").child(currentUid);

        UpdateShootFriend();

        userFriends = new ArrayList<>();
        userFriends.add(new com.theshootapp.world.ModelClasses.User("0","0","Select User"));

        userSpinnerAdapter = new ArrayAdapter<com.theshootapp.world.ModelClasses.User>(this, android.R.layout.simple_spinner_dropdown_item, userFriends);
        userSpinner.setAdapter(userSpinnerAdapter);



        setTitle("Add Appointment");

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddAppointment.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setText(hourOfDay+":"+minute);
                    }
                },hour,minute,false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userSelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void selectLocation(View v)
    {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                location.setText(place.getName());
                loc = place.getLatLng();
            }
        }
    }

    public void addAppointment(View v)
    {
        //final String mUsername = username.getText().toString();
        final String mUsername = userFriends.get(userSelected).getUserId();
        final String mEventName = eventName.getText().toString();
        final String mTime = time.getText().toString();
        final String mLocation = location.getText().toString();
        final LatLng mLoc = loc;
        final long dayTimestamp = dateTimestamp;

        if(userSelected==0 || TextUtils.isEmpty(mEventName) || TextUtils.isEmpty(mTime) || mLoc==null)
        {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.add_appointment), R.string.fill_fields, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        DatabaseReference emp = firebaseDatabase.getReference("User/" + mUsername);
        emp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile user = dataSnapshot.getValue(UserProfile.class);
                final String otherName = user.getName();

                DatabaseReference emp1 = firebaseDatabase.getReference("User/" + currentUid);
                emp1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserProfile user = dataSnapshot.getValue(UserProfile.class);
                        String currentName = user.getName();

                        Appointment appointment = new Appointment(mEventName,mLocation,dayTimestamp,currentUid,mUsername,currentName,otherName,mTime,mLoc.latitude,mLoc.longitude);
                        DatabaseReference appointmentRef = firebaseDatabase.getReference("Appointment").push();
                        appointmentRef.setValue(appointment);

                        DatabaseReference userAppointmentRef = firebaseDatabase.getReference("UserAppointment/"+currentUid +"/"+ dayTimestamp + "/" + appointmentRef.getKey());
                        userAppointmentRef.setValue(true);

                        DatabaseReference userAppointmentRef2 = firebaseDatabase.getReference("UserAppointment/"+mUsername+"/" + dayTimestamp + "/" + appointmentRef.getKey());
                        userAppointmentRef2.setValue(true);

                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("userId",mUsername);
                        dataBundle.putDouble("appointmentLatitude",mLoc.latitude);
                        dataBundle.putDouble("appointmentLongitude",mLoc.longitude);
                        dataBundle.putString("userName",otherName);
                        dataBundle.putInt("count",0);


                        Intent intent1 = new Intent(AddAppointment.this, AppointmentNotificationService.class);
                        intent1.putExtra("data",dataBundle);

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(dayTimestamp);

                        SimpleDateFormat df = new SimpleDateFormat("kk:mm");
                        Date d1 = null;
                        try {
                            d1 = df.parse(mTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(d1);

                        int hours = calendar.get(HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);
                        int seconds = calendar.get(Calendar.SECOND);

                        c.set(HOUR_OF_DAY,hours);
                        c.set(Calendar.MINUTE,minutes);
                        c.set(Calendar.SECOND,seconds);

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddAppointment.this);
                        int notification_id = preferences.getInt("notification_id", 0);

                        PendingIntent pintent = PendingIntent.getService(AddAppointment.this, notification_id, intent1, 0);
                        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pintent);

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("notification_id",notification_id+1);
                        editor.apply();

                        Toast.makeText(AddAppointment.this, "Appointment Created", Toast.LENGTH_SHORT).show();
                        
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(AddAppointment.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    void UpdateShootFriend()
    {
        friends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean flag=dataSnapshot.getValue(Boolean.class);
                if(flag==true) {
                    FetchUser(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void FetchUser(String key)
    {
        userReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                com.theshootapp.world.ModelClasses.User user= new com.theshootapp.world.ModelClasses.User(dataSnapshot.getKey(),userProfile.getPhoneNumber(),userProfile.getName());
                userFriends.add(user);
                userSpinnerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
