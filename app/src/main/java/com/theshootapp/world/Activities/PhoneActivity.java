package com.theshootapp.world.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theshootapp.world.Adapters.SimpleFragmentPagerAdapter;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.ModelClasses.User;
import com.theshootapp.world.ModelClasses.UserProfile;
import com.theshootapp.world.R;

import java.util.ArrayList;

public class PhoneActivity extends AppCompatActivity implements OnListFragmentInteractionListener {


    String currentUserName;
    SharedPreferences sharedPreferences;
    AlertDialog.Builder alert;
    AlertDialog.Builder alertError;
    DatabaseReference userReference;
    DatabaseReference friends;
    ArrayList<User> userMap;
    String currentUId;
    String cuid;// temporary to be remove


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        alert = new AlertDialog.Builder(this);
        alertError = new AlertDialog.Builder(this);
        userMap=new ArrayList<>();
        userReference= FirebaseDatabase.getInstance().getReference().child("User");
        currentUId= FirebaseAuth.getInstance().getUid();
        friends=FirebaseDatabase.getInstance().getReference().child("UserFriends").child(currentUId);

        setTitle("Phone");
        sharedPreferences=getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        currentUserName=sharedPreferences.getString("Name","Name not Found");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.create_contact_icon:
                onAddClick();
                return true;
            case R.id.download_icon:
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onListFragmentInteraction(Bundle details, String action, boolean isFabClicked) {
        Intent intent = new Intent(this, PlaceCallActivity.class);
        intent.putExtra("receiverId", details.getString("receiverUid"));
        intent.putExtra("receiverName", details.getString("receiverName"));
        intent.putExtra("callerName", currentUserName);
        startActivity(intent);
        finish();
    }

    public void onAddClick()
    {
        final EditText edittext = new EditText(this);
        alert.setMessage("Enter mobile number");
        alert.setTitle("Add Contact");

        alert.setView(edittext);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
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

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
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
                    DisplayAlert("The Number you Entered is not a Shoot User");
                }
                else if(uID.equals(currentUId))
                {
                    DisplayAlert("This is Your Own Number");
                }
                else if(userMap.contains(new User(uID,"","")))
                {
                    DisplayAlert("The User already exists");
                }
                else if(dS!=null)
                {
                    UserProfile userProfile=dS.getValue(UserProfile.class);
                    friends.child(uID).setValue(true);
                    userMap.add(new User(uID,userProfile.getPhoneNumber(),userProfile.getName()));
                    //updateUi(uID);//TODO remove this function through proper UI

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
}
