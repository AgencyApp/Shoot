package com.theshootapp.world.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

        /*ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.galleryitem, null);

            holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.imageview.setLayoutParams(new RelativeLayout.LayoutParams(230, 230));
            holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageview.setPadding(0, 0, 0, 0);
            holder.checkbox = convertView.findViewById(R.id.itemCheckBox);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkbox.setId(position);
        holder.imageview.setId(position);

        holder.imageview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                int id = v.getId();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + selectedPositions.indexOf(id)), "image/*");
                mContext.startActivity(intent);
            }
        });

        holder.checkbox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                if(selectedPositions.contains(id))
                {
                    int selectedIndex = selectedPositions.indexOf(id);
                    selectedPositions.remove(selectedIndex);
                }
                else
                {
                    selectedPositions.add(id);
                }
            }
        });

        holder.checkbox.setSelected(selectedPositions.contains(position));
        StorageReference ref = storage.getReference().child("Moments/" + mThumbIds.get(position) + ".jpeg");
        Glide.with(mContext).load(ref).into(holder.imageview);
        return convertView;*/

        CustomView customView = (convertView == null) ?
                new CustomView(mContext) : (CustomView) convertView;
        customView.display(mThumbIds.get(position), selectedPositions.contains(position));
        return customView;
    }




}