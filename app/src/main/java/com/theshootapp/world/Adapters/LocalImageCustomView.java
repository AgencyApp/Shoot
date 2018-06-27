package com.theshootapp.world.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theshootapp.world.R;

public class LocalImageCustomView extends FrameLayout {

    ImageView imageView;
    ImageView checkMark;
    Context c;

    public LocalImageCustomView(Context context) {
        super(context);
        c=context;
        LayoutInflater.from(context).inflate(R.layout.galleryitem, this);
        imageView = (ImageView) getRootView().findViewById(R.id.thumbImage);
        checkMark = findViewById(R.id.checkMark);
    }

    public void display(Bitmap image, boolean isSelected) {
        imageView.setImageBitmap(image);
        display(isSelected);

        imageView.setLayoutParams(new RelativeLayout.LayoutParams(230, 230));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(0, 0, 0, 0);
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