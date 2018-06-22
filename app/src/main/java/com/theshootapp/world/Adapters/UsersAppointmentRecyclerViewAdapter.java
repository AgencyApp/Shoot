package com.theshootapp.world.Adapters;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.Interfaces.OnListFragmentInteractionListener;
import com.theshootapp.world.R;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UsersAppointmentRecyclerViewAdapter extends RecyclerView.Adapter<UsersAppointmentRecyclerViewAdapter.ViewHolder> {

    private final List<Appointment> mValues;
    OnListFragmentInteractionListener mListener;


    public UsersAppointmentRecyclerViewAdapter(List<Appointment> items,  OnListFragmentInteractionListener mListener) {
        mValues = items;
        this.mListener = mListener;
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
        holder.mName.setText(mValues.get(position).getPersonName());
        holder.mEventName.setText(mValues.get(position).getEventName());
        Date date = new Date(mValues.get(position).gettimestamp() * 1000);

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        holder.mTimeStamp.setText(sdf.format(date));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
               /* bundle.putString("receiverUid", mIds.get(position));
                bundle.putString("receiverName", holder.mItem.getReciverName());
                mListener.onListFragmentInteraction(bundle, "chatMessage", true);*/
            }
        });

        //TODO: Set Profile Picture using Glide
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
