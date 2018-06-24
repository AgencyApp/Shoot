package com.theshootapp.world.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theshootapp.world.Activities.MomentActivity;
import com.theshootapp.world.R;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mThumbIds;
    public ArrayList<Integer> selectedPositions = new ArrayList<>();
    FirebaseStorage storage;

    public ImageAdapter(Context c, ArrayList<String> mThumbIds) {
        mContext = c;
        this.mThumbIds = mThumbIds;

        storage = FirebaseStorage.getInstance();
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
            imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
        else
        {
            imageView.setColorFilter(null);

        }
        StorageReference ref = storage.getReference().child("Moments/" + mThumbIds.get(position) + ".jpeg");
        Glide.with(mContext).load(ref).into(imageView);
        return imageView;
    }


}