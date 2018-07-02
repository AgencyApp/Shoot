package com.theshootapp.world.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.ModelClasses.Moment;
import com.theshootapp.world.R;

import java.util.HashMap;

public class CustomView extends FrameLayout {

    ImageView imageView;
    ImageView checkMark;
    Context c;
    FirebaseStorage storage;
    public CustomView(Context context) {
        super(context);
        c=context;
        LayoutInflater.from(context).inflate(R.layout.galleryitem, this);
        imageView = (ImageView) getRootView().findViewById(R.id.thumbImage);
        checkMark = findViewById(R.id.checkMark);
        storage = FirebaseStorage.getInstance();
    }

    public void display(final String url,final boolean isSelected) {

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference moment = firebaseDatabase.getReference("Moments/" + url);
        moment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Moment moment1 = dataSnapshot.getValue(Moment.class);
                if(moment1.isVideo())
                {
                    /*StorageReference ref = storage.getReference().child("Moments/" + url + ".mp4");
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            new retriveVideoFrameFromVideo(uri.toString(),imageView).execute();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });*/
                    StorageReference ref = storage.getReference().child("Thumbnails/" + url + ".jpeg");
                    Glide.with(c).load(ref).into(imageView);

                }
                else{
                    StorageReference ref = storage.getReference().child("Moments/" + url + ".jpeg");
                    Glide.with(c).load(ref).into(imageView);

                }
                display(isSelected);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ;

        imageView.setLayoutParams(new RelativeLayout.LayoutParams(230, 230));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(0, 0, 0, 0);
    }

    public class retriveVideoFrameFromVideo extends AsyncTask<Object,Void,Bitmap>
    {
        String videoPath;
        ImageView img;
        public retriveVideoFrameFromVideo(String videoPath,ImageView img)
        {
            this.videoPath = videoPath;
            this.img = img;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            img.setImageBitmap(b);
        }

        @Override
        protected Bitmap doInBackground(Object[] objects) {
            Bitmap bitmap = null;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try
            {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
            finally
            {

                if (mediaMetadataRetriever != null)
                {
                    mediaMetadataRetriever.release();
                }
            }
            return bitmap;
        }
    }


    public void display(boolean isSelected) {
        if(isSelected) {
            checkMark.setVisibility(View.VISIBLE);
            imageView.setColorFilter(ContextCompat.getColor(c, R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
        else {
            checkMark.setVisibility(View.INVISIBLE);
            imageView.setColorFilter(null);
        }
    }
}