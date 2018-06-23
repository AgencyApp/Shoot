package com.theshootapp.world.Activities;

import android.app.TimePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;

import java.util.Calendar;


public class AddAppointment extends AppCompatActivity {

    EditText username;
    EditText eventName;
    EditText time;
    EditText location;
    Long dateTimestamp;
    FirebaseDatabase firebaseDatabase;
    String currentUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        username = findViewById(R.id.username);
        eventName = findViewById(R.id.eventName);
        time = findViewById(R.id.eventTime);
        location = findViewById(R.id.location);

        dateTimestamp = getIntent().getLongExtra("date",0);

        firebaseDatabase=FirebaseDatabase.getInstance();
        currentUid= FirebaseAuth.getInstance().getCurrentUser().getUid();

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

    }

    public void addAppointment(View v)
    {
        //final String mUsername = username.getText().toString();
        final String mUsername = "ZvIgGj61IgQ60jE1tSRnGgr5H1B3";
        final String mEventName = eventName.getText().toString();
        final String mTime = time.getText().toString();
        final String mLocation = location.getText().toString();
        final long dayTimestamp = dateTimestamp;

        if(TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mEventName) || TextUtils.isEmpty(mTime) || TextUtils.isEmpty(mLocation))
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
}
