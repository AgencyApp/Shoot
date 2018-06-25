package com.theshootapp.world.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.theshootapp.world.Adapters.SimpleFragmentPagerAdapter;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.R;

public class PhoneActivity extends AppCompatActivity implements OnListFragmentInteractionListener {


    String currentUserName;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
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
    public void onListFragmentInteraction(Bundle details, String action, boolean isFabClicked) {
        Intent intent = new Intent(this, PlaceCallActivity.class);
        intent.putExtra("receiverId", details.getString("receiverUid"));
        intent.putExtra("receiverName", details.getString("receiverName"));
        intent.putExtra("callerName", currentUserName);
        startActivity(intent);
        finish();
    }
}
