package com.theshootapp.world.Activities;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theshootapp.world.Adapters.ImageAdapter;
import com.theshootapp.world.R;

import java.util.ArrayList;

public class MomentActivity extends AppCompatActivity {

    ArrayList<String>momentIds;
    ArrayList<String>momentIdsTobeDeleted;
    long currentTime;
    DatabaseReference databaseReference;
     ImageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        currentTime=System.currentTimeMillis() / 1000;
        databaseReference=FirebaseDatabase.getInstance().getReference().child("UserMoments").child(FirebaseAuth.getInstance().getUid());
        setTitle("Moments");
        ArrayList<String> url = new ArrayList<>();
        url.add("http://via.placeholder.com/350x350");
        url.add("http://via.placeholder.com/350x350");
        url.add("http://via.placeholder.com/350x350");
        url.add("http://via.placeholder.com/350x350");
        url.add("http://via.placeholder.com/350x350");
        GridView gridview = (GridView) findViewById(R.id.gridview);
        adapter = new ImageAdapter(this,url);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int selectedIndex = adapter.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    adapter.selectedPositions.remove(selectedIndex);
                    ((ImageView)v).setColorFilter(null);

                } else {
                    adapter.selectedPositions.add(position);
                    ((ImageView)v).setColorFilter(ContextCompat.getColor(MomentActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_moment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download_icon:
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    void fetchMoments()
    {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                long momentTimeStamp=dataSnapshot.getValue(Long.class);
                if((currentTime-momentTimeStamp)<86400)
                {
                    momentIds.add(dataSnapshot.getKey());
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    deleteMovement(dataSnapshot.getKey());
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

    void deleteMovement(String key)
    {
        DatabaseReference dR=databaseReference.child(key);
        dR.removeValue();
    }
}
