package com.theshootapp.world.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.otaliastudios.cameraview.CameraUtils;
import com.theshootapp.world.Database.FileDataBase;
import com.theshootapp.world.Database.MyFile;
import com.theshootapp.world.ModelClasses.Moment;
import com.theshootapp.world.R;
import com.theshootapp.world.Utility.OnSwipeTouchListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PictureDisplay extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    FileDataBase fileDataBase;
    double longitude;
    double latitude;
    String path;
    Bitmap img;
    StorageReference storageReference;
    File imgFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_frame);
        getSupportActionBar().hide();
        sharedPreferences=getSharedPreferences("location", Context.MODE_PRIVATE);
        String temp=sharedPreferences.getString("longitude","0");
        longitude=Double.parseDouble(temp);
        temp=sharedPreferences.getString("latitude","0");
        latitude=Double.parseDouble(temp);
        storageReference= FirebaseStorage.getInstance().getReference();
        fileDataBase=FileDataBase.getAppDatabase(this);
        Intent i=getIntent();
        path=i.getStringExtra("image");
        imgFile = new  File(path);


        if(imgFile.exists()) {
            byte[] b = new byte[(int) imgFile.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(imgFile);
                fileInputStream.read(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
             Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            final ImageView myImage = (ImageView) findViewById(R.id.imageView);

            // myImage.setImageBitmap(myBitmap);
            CameraUtils.decodeBitmap(b, new CameraUtils.BitmapCallback() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    myImage.setImageBitmap(bitmap);
                    img=bitmap;
                }
            });

            myImage.setOnTouchListener(new OnSwipeTouchListener(this) {
                public void onSwipeTop() {
                    onShootClick();
                }
                public void onSwipeRight() {

                }
                public void onSwipeLeft() {
                    addPicToStorage();
                }
                public void onSwipeBottom() {

                }

            });


        }


    }
    public void onShootClick()
    {
        Long ts = System.currentTimeMillis() / 1000;
        final Moment moment=new Moment(FirebaseAuth.getInstance().getUid(),longitude,latitude,ts);
        final DatabaseReference momentRef=FirebaseDatabase.getInstance().getReference().child("Moments").push();
        String key=momentRef.getKey();
        StorageReference momentStorageRef = storageReference.child("Moments/"+key+".jpeg");
       /* Bitmap bitmap = Bitmap.createScaledBitmap(img,1280,720,true );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = momentStorageRef.putBytes(data);*/
        Uri file = Uri.fromFile(new File(path));
       UploadTask uploadTask = momentStorageRef.putFile(file);

        //TODO progress Bar for uploading
// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                momentRef.setValue(moment);
                imgFile.delete();
                Toast.makeText(PictureDisplay.this, "Shooting Picture!", Toast.LENGTH_SHORT).show();

            }
        });

        finish();
    }

    void addPicToStorage()
    {
        Toast.makeText(PictureDisplay.this, "Saving Picture", Toast.LENGTH_SHORT).show();
        new DatabaseAsyncTask(this).execute();
        finish();
    }

    public class DatabaseAsyncTask extends AsyncTask {
        Context c;

        DatabaseAsyncTask(Context con)
        {
            c = con;
        }
        @Override
        protected Object doInBackground(Object[] objects) {

            MyFile myFile=new MyFile();
            myFile.setFileName(path);
            fileDataBase.fileDao().insertAll(myFile);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(c, "Picture Saved!", Toast.LENGTH_SHORT).show();
        }
    }

}
