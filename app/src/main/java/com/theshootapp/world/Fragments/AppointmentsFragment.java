package com.theshootapp.world.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsFragment extends Fragment {


    ArrayList<Appointment> appointments;
    FirebaseDatabase firebaseDatabase;
    String currentUid;
    User currentUser;
    private RecyclerView recyclerView;
    private UsersAppointmentRecyclerViewAdapter adapter;
    OnListFragmentInteractionListener mListener;
    MaterialCalendarView simpleCalendarView;

    public AppointmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_appointments, container, false);
        appointments=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        currentUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.appointmentsListToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter = new UsersAppointmentRecyclerViewAdapter(appointments, getActivity()));
        Calendar cal = Calendar.getInstance();
        cal. set (Calendar .HOUR_OF_DAY, 0);
        cal. set (Calendar .MINUTE, 0);
        cal. set (Calendar .SECOND, 0);
        cal. set (Calendar .MILLISECOND, 0);


        fetchAppointmentsOnDate(cal.getTimeInMillis());


        return view;
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

}
