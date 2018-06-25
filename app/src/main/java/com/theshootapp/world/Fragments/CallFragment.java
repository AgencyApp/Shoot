package com.theshootapp.world.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theshootapp.world.Adapters.AllUsersCallRecyclerViewAdapter;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.ModelClasses.User;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallFragment extends Fragment {

    AlertDialog.Builder alert;
    AlertDialog.Builder alertError;
    DatabaseReference userReference;
    DatabaseReference friends;
    ArrayList<User> userMap;
    String currentUId;
    String currentUserName;
    String cuid;// temporary to be remove
    SharedPreferences sharedPreferences;
    private AllUsersCallRecyclerViewAdapter adapter;
    OnListFragmentInteractionListener mListener;


    public CallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        alert = new AlertDialog.Builder(getActivity());
        alertError = new AlertDialog.Builder(getActivity());
        userMap=new ArrayList<>();
        userReference= FirebaseDatabase.getInstance().getReference().child("User");
        currentUId= FirebaseAuth.getInstance().getUid();
        friends=FirebaseDatabase.getInstance().getReference().child("UserFriends").child(currentUId);
        UpdateShootFriend();


        RecyclerView recyclerView = view.findViewById(R.id.call_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AllUsersCallRecyclerViewAdapter(userMap, getActivity(), mListener);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void UpdateShootFriend()
    {
        friends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean flag=dataSnapshot.getValue(Boolean.class);
                if(flag==true) {
                    if (!userMap.contains(dataSnapshot.getKey())) {
                        FetchUser(dataSnapshot.getKey());
                    }
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
                User user= new User(dataSnapshot.getKey(),userProfile.getPhoneNumber(),userProfile.getName());
                userMap.add(user);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
