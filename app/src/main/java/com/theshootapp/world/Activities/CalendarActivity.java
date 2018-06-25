package com.theshootapp.world.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.theshootapp.world.Adapters.UsersAppointmentRecyclerViewAdapter;
import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    ArrayList<Appointment>appointments;
    FirebaseDatabase firebaseDatabase;
    String currentUid;
    User currentUser;
    private RecyclerView recyclerView;
    private UsersAppointmentRecyclerViewAdapter adapter;
    OnListFragmentInteractionListener mListener;
    MaterialCalendarView simpleCalendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        appointments=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        currentUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.appointmentsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter = new UsersAppointmentRecyclerViewAdapter(appointments,this));

       simpleCalendarView = (MaterialCalendarView) findViewById(R.id.simpleCalendarView);
                simpleCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget,@NonNull CalendarDay date, boolean selected) {
                        long timestamp = date.getDate().getTime();
                        appointments.clear();
                        adapter.notifyDataSetChanged();
                        fetchAppointmentsOnDate(timestamp);
                    }
                });

        setTitle("Calendar");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_appointment_icon:
                CalendarDay date = simpleCalendarView.getSelectedDate();
                if(date==null)
                {
                    Toast.makeText(this, "Select a date first", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(this,AddAppointment.class);
                intent.putExtra("date",date.getDate().getTime());
                startActivity(intent);
                
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    void fetchAppointmentsOnDate(long timestamp)
    {

        DatabaseReference databaseReference=firebaseDatabase.getReference("UserAppointment/" + currentUid+"/"+timestamp);
        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                fetchAppointmentData(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchAppointments(long timestamp) {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


        DatabaseReference appointments = firebaseDatabase.getReference("UserAppointment/" + currentUid+"/"+timestamp);
        appointments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue(boolean.class) ) {
                        fetchAppointmentData(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void fetchAppointmentData(final String appointmentId) {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference appointment = firebaseDatabase.getReference("Appointment/" + appointmentId);
        appointment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Appointment appointment = dataSnapshot.getValue(Appointment.class);
                appointments.add(appointment);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    @Override
    public void onListFragmentInteraction(Bundle details, String action, boolean isFabClicked) {
        /*Intent intent = new Intent(this, SendMessage.class);
        intent.putExtra("receiverUid", details.getString("receiverUid"));
        intent.putExtra("receiverName", details.getString("receiverName"));

        startActivity(intent);*/
    }
}
