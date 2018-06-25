package com.theshootapp.world.Activities;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;

import java.util.ArrayList;
import java.util.Calendar;


public class AddAppointment extends AppCompatActivity {

    EditText eventName;
    EditText time;
    EditText location;
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
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
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

    public void addAppointment(View v)
    {
        //final String mUsername = username.getText().toString();
        final String mUsername = userFriends.get(userSelected).getUserId();
        final String mEventName = eventName.getText().toString();
        final String mTime = time.getText().toString();
        final String mLocation = location.getText().toString();
        final long dayTimestamp = dateTimestamp;

        if(userSelected==0 || TextUtils.isEmpty(mEventName) || TextUtils.isEmpty(mTime) || TextUtils.isEmpty(mLocation))
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

                        Appointment appointment = new Appointment(mEventName,mLocation,dayTimestamp,currentUid,mUsername,currentName,otherName,mTime);
                        DatabaseReference appointmentRef = firebaseDatabase.getReference("Appointment").push();
                        appointmentRef.setValue(appointment);

                        DatabaseReference userAppointmentRef = firebaseDatabase.getReference("UserAppointment/"+currentUid +"/"+ dayTimestamp + "/" + appointmentRef.getKey());
                        userAppointmentRef.setValue(true);

                        DatabaseReference userAppointmentRef2 = firebaseDatabase.getReference("UserAppointment/"+mUsername+"/" + dayTimestamp + "/" + appointmentRef.getKey());
                        userAppointmentRef2.setValue(true);

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
