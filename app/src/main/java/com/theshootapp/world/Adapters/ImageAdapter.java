package com.theshootapp.world.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.theshootapp.world.Activities.MomentActivity;
import com.theshootapp.world.R;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mThumbIds;
    public ArrayList<Integer> selectedPositions = new ArrayList<>();

    public ImageAdapter(Context c, ArrayList<String> mThumbIds) {
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
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(230, 230));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        if(selectedPositions.contains(position))
        {
            imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
        else
        {
            imageView.setColorFilter(null);

        }
        Picasso.get().load(mThumbIds.get(position)).into(imageView);
        return imageView;
    }


}