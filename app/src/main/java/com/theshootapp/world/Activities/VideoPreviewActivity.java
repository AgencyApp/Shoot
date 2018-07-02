package com.theshootapp.world.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theshootapp.world.ModelClasses.Moment;
import com.theshootapp.world.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class VideoPreviewActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private double longitude;
    private double latitude;
    StorageReference storageReference;
    StorageReference thumbnailReference;
    String path;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        getSupportActionBar().hide();
        Intent i=getIntent();
         path=i.getStringExtra("videoPath");
        sharedPreferences=getSharedPreferences("location", Context.MODE_PRIVATE);
        String temp=sharedPreferences.getString("longitude","0");
        longitude=Double.parseDouble(temp);
        temp=sharedPreferences.getString("latitude","0");
        latitude=Double.parseDouble(temp);
        progressDialog = new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();
        thumbnailReference=storageReference.child("Thumbnails");
        VideoView videoView = (VideoView)findViewById(R.id.videoPreviewView);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);

        videoView.setVideoURI(Uri.parse(path));
        videoView.requestFocus();
        videoView.start();
    }

    public void onShootClick(View view) {
        Long ts = System.currentTimeMillis() / 1000;
        final DatabaseReference momentRef = FirebaseDatabase.getInstance().getReference().child("Moments").push();
        final String key = momentRef.getKey();
        final Moment moment = new Moment(FirebaseAuth.getInstance().getUid(), longitude, true, latitude, ts);
        StorageReference momentStorageRef = storageReference.child("Moments/" + key + ".mp4");
        Uri file = Uri.fromFile(new File(path));
        UploadTask uploadTask = momentStorageRef.putFile(file);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                progressDialog.dismiss();
                Toast.makeText(VideoPreviewActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                momentRef.setValue(moment);
               Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
               uploadThumbnail(bitmap,key);

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });

    }

    void uploadThumbnail(Bitmap bitmap,String key)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = thumbnailReference.child(key+".jpeg").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                progressDialog.dismiss();
                Toast.makeText(VideoPreviewActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                finish();
                // ...
            }
        });

    }
}
