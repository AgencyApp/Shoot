package com.theshootapp.world.Fragments;


import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theshootapp.world.Adapters.AllUsersCallRecyclerViewAdapter;
import com.theshootapp.world.Adapters.SuggestionsRecyclerViewAdapter;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.ModelClasses.User;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestionsFragment extends Fragment implements OnListFragmentInteractionListener {
    SharedPreferences sharedPreferences;
    DatabaseReference userReference;
    DatabaseReference suggestions;
    String currentUId;
    HashMap<String,Boolean> userMap;
    HashMap<String,Boolean> userSuggestionMap;
    DatabaseReference friends;
    ArrayList<String> userProfiles;
    ArrayList<User>userSuggestions;
    private SuggestionsRecyclerViewAdapter adapter;
    OnListFragmentInteractionListener mListener;
    ProgressBar progressBar;

    public SuggestionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_suggestions, container, false);
        mListener = this;



        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        ImageButton button = view.findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Async().execute();
            }
        });
        sharedPreferences =this.getActivity().getSharedPreferences("Suggestions",Context.MODE_PRIVATE);
        userReference= FirebaseDatabase.getInstance().getReference().child("User");
        currentUId=FirebaseAuth.getInstance().getUid();
        userMap=new HashMap<>();
        userSuggestionMap=new HashMap<>();
        userSuggestions = new ArrayList<>();
        userProfiles=new ArrayList<>();
        friends=FirebaseDatabase.getInstance().getReference().child("UserFriends").child(currentUId);
        suggestions=FirebaseDatabase.getInstance().getReference().child("Suggestions").child(currentUId);
        boolean fetchContact=sharedPreferences.getBoolean("fetchContact",false);
        RecyclerView recyclerView = view.findViewById(R.id.suggestions_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SuggestionsRecyclerViewAdapter(userSuggestions, getActivity(), mListener);
        recyclerView.setAdapter(adapter);


        FetchSuggestions();


        if(!fetchContact)
        {
            new Async().execute();
        }


        return view;

    }



    void getSuggestion()
    {
        ContentResolver cr = this.getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(phoneNo.startsWith("03"))
                        {
                            phoneNo= phoneNo.replaceFirst("0","+92");
                        }
                        else if(phoneNo.startsWith("00"))
                        {
                           phoneNo= phoneNo.replaceFirst("00","+");
                        }
                        isShootUser(phoneNo);

                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        getShootFriends();

    }

    void isShootUser(String number)
    {
        userReference.orderByChild("phoneNumber").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uID=null;
                DataSnapshot dS=null;
                if(dataSnapshot.getChildrenCount()>0) {
                    dS = dataSnapshot.getChildren().iterator().next();
                    uID = dS.getKey();
                }
                if(uID==null||uID.equals(""))
                {

                }
                else if(uID.equals(currentUId))
                {

                }
                else if(userMap.containsKey(uID))
                {

                }
                else if(dS!=null)
                {
                   // UserProfile userProfile=dS.getValue(UserProfile.class);
                    //suggestions.child(uID).child("true");
                    userProfiles.add(dS.getKey());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void getShootFriends()
    {
        friends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()
                     ) {

                    if(snapshot.getValue(Boolean.class)==true)
                    {
                        userMap.put(snapshot.getKey(),true);

                    }
                }
                uploadSuggestions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onListFragmentInteraction(Bundle details, String action, boolean isFabClicked) {
        String uid = details.getString("personUid");
        int position = details.getInt("personPosition");
        friends.child(uid).setValue(true);
        suggestions.child(uid).removeValue();
        userSuggestions.remove(position);
    }


    public class Async extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            getSuggestion();
            return null;
        }
    }

    void uploadSuggestions()
    {
        suggestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()) {

                    if(snapshot.getValue(Boolean.class)==true)
                    {
                        userSuggestionMap.put(snapshot.getKey(),true);

                    }
                }
                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putBoolean("fetchContact",true);
                ed.apply();


                for(int i=0;i<userProfiles.size();i++)
                {
                    if(!userSuggestionMap.containsKey(userProfiles.get(i))&&!userMap.containsKey(userProfiles.get(i)))
                    {
                        suggestions.child(userProfiles.get(i)).setValue(true);
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                FetchSuggestions();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void FetchSuggestions()
    {
        userSuggestions.clear();
        adapter.notifyDataSetChanged();
        suggestions.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(Boolean.class))
                {
                    FetchUser(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
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
                if(!userSuggestions.contains(user))
                {
                userSuggestions.add(user);
                adapter.notifyDataSetChanged();}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
