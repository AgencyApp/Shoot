package com.theshootapp.world.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theshootapp.world.ModelClasses.User;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mainCallActivity extends AppCompatActivity {
    AlertDialog.Builder alert;
    AlertDialog.Builder alertError;
    DatabaseReference userReference;
    DatabaseReference friends;
    HashMap<String,User> userMap;
    String currentUId;
    String currentUserName;
    String cuid;// temporary to be remove
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_call);
        alert = new AlertDialog.Builder(this);
        alertError = new AlertDialog.Builder(this);
        userMap=new HashMap<>();
        userReference= FirebaseDatabase.getInstance().getReference().child("User");
        currentUId=FirebaseAuth.getInstance().getUid();
        friends=FirebaseDatabase.getInstance().getReference().child("UserFriends").child(currentUId);
        UpdateShootFriend();
        sharedPreferences=getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        currentUserName=sharedPreferences.getString("Name","Name not Found");
    }


    public void onAddClick(View view)
    {
        final EditText edittext = new EditText(this);
        alert.setMessage("Enter Your Message");
        alert.setTitle("Enter Your Title");

        alert.setView(edittext);

        alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //OR
                String Number = edittext.getText().toString();
                if(Number.startsWith("03"))
                {
                   Number= Number.replaceFirst("0","+92");
                }
                else if(Number.startsWith("00"))
                {
                   Number= Number.replaceFirst("00","+");
                }

                    isShootUser(Number);


            }
        });

        alert.setNegativeButton("No Option", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    void UpdateShootFriend()
    {
        friends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean flag=dataSnapshot.getValue(Boolean.class);
                if(flag==true) {
                    if (!userMap.containsKey(dataSnapshot.getKey())) {
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
                    DisplayAlert("The Number you Enter is not a Shoot User");
                }
                else if(uID.equals(currentUId))
                {
                    DisplayAlert("This is Your Own Number");
                }
                else if(userMap.containsKey(uID))
                {
                    DisplayAlert("The User already exists");
                }
                else if(dS!=null)
                {
                    UserProfile userProfile=dS.getValue(UserProfile.class);
                    friends.child(uID).setValue(true);
                    userMap.put(uID,new User(uID,userProfile.getPhoneNumber(),userProfile.getName()));
                    updateUi(uID);//TODO remove this function through proper UI

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void DisplayAlert(String Message)
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("User Not Found")
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    void FetchUser(String key)
    {
        userReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                User user= new User(dataSnapshot.getKey(),userProfile.getPhoneNumber(),userProfile.getName());
                userMap.put(user.getUserId(),user);
                updateUi(user.getUserId());//TODO remove this function through proper UI

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void updateUi(String uid)
    {
        TextView textView=findViewById(R.id.number);
        textView.setText(userMap.get(uid).getName());
        cuid=uid;
    }

    public void onCallClick(View view)
    {

        makeCall(userMap.get(cuid).getName(),userMap.get(cuid).getUserId());
    }

    void makeCall(String receiverName, String receiverUid)
    {
        Intent intent = new Intent(this, PlaceCallActivity.class);
        intent.putExtra("receiverId", receiverUid);
        intent.putExtra("receiverName", receiverName);
        intent.putExtra("callerName", currentUserName);
        startActivity(intent);
    }
}
