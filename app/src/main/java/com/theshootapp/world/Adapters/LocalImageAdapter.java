package com.theshootapp.world.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.theshootapp.world.R;

import java.util.ArrayList;

public class LocalImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Bitmap> mThumbIds;
    public ArrayList<Integer> selectedPositions = new ArrayList<>();


    public LocalImageAdapter(Context c, ArrayList<Bitmap> mThumbIds) {
        mContext = c;
        this.mThumbIds = mThumbIds;
    }



    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LocalImageCustomView customView = (convertView == null) ?
                new LocalImageCustomView(mContext) : (LocalImageCustomView) convertView;
        customView.display(mThumbIds.get(position), selectedPositions.contains(position));
        return customView;
    }
}
