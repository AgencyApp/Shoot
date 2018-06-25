package com.theshootapp.world.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.theshootapp.world.Fragments.AppointmentsFragment;
import com.theshootapp.world.Fragments.CallFragment;
import com.theshootapp.world.Fragments.SuggestionsFragment;
import com.theshootapp.world.R;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new CallFragment();
        } else if (position == 1){
            return new SuggestionsFragment();
        }  else {
            return new AppointmentsFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 3;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.category_call);
            case 1:
                return mContext.getString(R.string.category_suggestions);
            case 2:
                return mContext.getString(R.string.category_appointments);
            default:
                return null;
        }
    }

}