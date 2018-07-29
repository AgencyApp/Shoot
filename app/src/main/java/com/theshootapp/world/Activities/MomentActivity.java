package com.theshootapp.world.Activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.otaliastudios.cameraview.CameraUtils;
import com.theshootapp.world.Adapters.ImageAdapter;
import com.theshootapp.world.Adapters.CustomView;
import com.theshootapp.world.Database.FileDataBase;
import com.theshootapp.world.Database.MyFile;
import com.theshootapp.world.ModelClasses.Appointment;
import com.theshootapp.world.ModelClasses.Moment;
import com.theshootapp.world.R;
import com.theshootapp.world.Utility.GlideApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MomentActivity extends AppCompatActivity {

    ArrayList<String> momentIds;
    ArrayList<String> momentIdsTobeDeleted;
    long currentTime;
    DatabaseReference databaseReference;
    ImageAdapter adapter;
    ArrayList<MyFile> storagePictures;
    FileDataBase fileDataBase;
    ArrayList<Bitmap> storageImages;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivity(new Intent(this,LoginActivity.class));
        }
        currentTime = System.currentTimeMillis() / 1000;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserMoment").child(FirebaseAuth.getInstance().getUid());
        setTitle("Moments");
        momentIds = new ArrayList<>();
        storagePictures = new ArrayList<>();
        GridView gridview = (GridView) findViewById(R.id.gridview);
        fileDataBase = FileDataBase.getAppDatabase(this);
        storageImages = new ArrayList<>();
        adapter = new ImageAdapter(this, momentIds);
        gridview.setAdapter(adapter);
        storage = FirebaseStorage.getInstance();

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int selectedIndex = adapter.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    adapter.selectedPositions.remove(selectedIndex);
                    ((CustomView)v).display(false);

                } else {
                    adapter.selectedPositions.add(position);
                     ((CustomView)v).display(true);
                }
            }
        });
        fetchMoments();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_moment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.local_icon:
                startActivity(new Intent(this,LocalImages.class));
                return true;
            case R.id.download_icon:
                downloadPictures();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void downloadPictures()
    {
        if(adapter.selectedPositions.size()==0)
        {
            Toast.makeText(this, "Select some pictures to save to gallery", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Saving Pictures", Toast.LENGTH_SHORT).show();
        for (int i = 0; i<adapter.selectedPositions.size();i++)
        {

            final String id = momentIds.get(adapter.selectedPositions.get(i));
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference moment = firebaseDatabase.getReference("Moments/" + id);
            moment.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Moment moment1 = dataSnapshot.getValue(Moment.class);
                    if(moment1.isVideo())
                    {
                        StorageReference ref = storage.getReference().child("Moments/" + id + ".mp4");
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                request.setDescription("Downloading shoot video");
                                request.setTitle("Downloading Video");
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, id+".mp4");

                                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                manager.enqueue(request);
                                deleteMovement(id);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
                    else{

                        StorageReference ref = storage.getReference().child("Moments/" + id + ".jpeg");
                        Glide.with(getApplicationContext()).asBitmap().load(ref)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition)  {
                                        saveImage(resource,id);
                                        deleteMovement(id);
                                    }
                                });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        finish();
    }



    void fetchMoments() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                long momentTimeStamp = dataSnapshot.getValue(Long.class);
                if ((currentTime - momentTimeStamp) < 86400) {
                    momentIds.add(dataSnapshot.getKey());
                    adapter.notifyDataSetChanged();
                } else {
                    deleteMovement(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                momentIds.remove(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    void deleteMovement(String key) {
        DatabaseReference dR = databaseReference.child(key);
        dR.removeValue();
    }



    private String saveImage(Bitmap image,String id) {
        String savedImagePath = null;

        String imageFileName = "JPEG_" + id + ".jpg";
        File storageDir = new File(            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/Shoot");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
            Toast.makeText(this, "Image Saved", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }



}