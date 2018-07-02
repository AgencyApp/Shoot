package com.theshootapp.world.Adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.R;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UsersAppointmentRecyclerViewAdapter extends RecyclerView.Adapter<UsersAppointmentRecyclerViewAdapter.ViewHolder> {

    private final List<Appointment> mValues;
    Context context;
    FirebaseStorage storage;

    public UsersAppointmentRecyclerViewAdapter(List<Appointment> items, Context c) {
        mValues = items;
        context = c;
        storage=FirebaseStorage.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        if(mValues.get(position).getSchedularUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            holder.mName.setText(mValues.get(position).getOtherName());
            StorageReference ref = storage.getReference().child("UserDP/" + mValues.get(position).getOtherUid() + ".jpg");
            Glide.with(context.getApplicationContext()).load(ref).into(holder.mPicture);
        }
        else {
            holder.mName.setText(mValues.get(position).getSchedularName());
            StorageReference ref = storage.getReference().child("UserDP/" + mValues.get(position).getSchedularUid() + ".jpg");
            Glide.with(context.getApplicationContext()).load(ref).into(holder.mPicture);
        }
        //TODO: Set Profile Picture using Glide

        holder.mEventName.setText(mValues.get(position).getEventName());
        holder.mTimeStamp.setText(mValues.get(position).getTime());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setTitle("Location").setMessage(holder.mItem.getLocationName());
                builderSingle.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.show();
            }
        });


        //ProfilePicture.setProfilePicture(mIds.get(position), holder.mPicture);


    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder

    {
        public final View mView;
        public final ImageView mPicture;
        public final TextView mName;
        public final TextView mEventName;
        public final TextView mTimeStamp;
        public Appointment mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPicture = view.findViewById(R.id.user_dp);
            mName = view.findViewById(R.id.user_name);
            mEventName = view.findViewById(R.id.last_message);
            mTimeStamp = view.findViewById(R.id.timestamp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }


    }
}
