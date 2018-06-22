package com.theshootapp.world.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.theshootapp.world.Adapters.UsersAppointmentRecyclerViewAdapter;
import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        appointments=new ArrayList<>();
        appointments.add(new Appointment("Party","Johar Town","122","133",1529668208,"Lily" ));
        firebaseDatabase=FirebaseDatabase.getInstance();
        currentUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.appointmentsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter = new UsersAppointmentRecyclerViewAdapter(appointments, mListener = this));

       MaterialCalendarView simpleCalendarView = (MaterialCalendarView) findViewById(R.id.simpleCalendarView);
                simpleCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget,@NonNull CalendarDay date, boolean selected) {
                        long timestamp = date.getDate().getTime();
                        Toast.makeText(CalendarActivity.this, Long.toString(timestamp), Toast.LENGTH_SHORT).show();
                    }
                });

        setTitle("Calendar");

    }

    void updateUI()
    {
        /*DatabaseReference databaseReference=firebaseDatabase.getReference("CurrentChat/"+currentUid);
        databaseReference.orderByChild("timeStamp").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                LastMessage lastMessage = dataSnapshot.getValue(LastMessage.class);
                appointments.add(0, lastMessage);
                appointmentsReceiverIds.add(0, dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
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
        });*/
    }


    @Override
    public void onListFragmentInteraction(Bundle details, String action, boolean isFabClicked) {
        /*Intent intent = new Intent(this, SendMessage.class);
        intent.putExtra("receiverUid", details.getString("receiverUid"));
        intent.putExtra("receiverName", details.getString("receiverName"));

        startActivity(intent);*/
    }
}
