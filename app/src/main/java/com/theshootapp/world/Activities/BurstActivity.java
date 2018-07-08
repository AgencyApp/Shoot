package com.theshootapp.world.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.theshootapp.world.Adapters.LocalImageAdapter;
import com.theshootapp.world.Adapters.LocalImageCustomView;
import com.theshootapp.world.Database.FileDataBase;
import com.theshootapp.world.Database.MyFile;
import com.theshootapp.world.ModelClasses.Moment;
import com.theshootapp.world.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class BurstActivity extends AppCompatActivity {


    LocalImageAdapter adapter;
    ArrayList<String> storagePictures;
    ArrayList<Bitmap> storageImages;
    long currentTime;

    SharedPreferences sharedPreferences;
    StorageReference storageReference;
    FileDataBase fileDataBase;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_images);

        currentTime = System.currentTimeMillis() / 1000;
        setTitle("Select Pictures");;
        storagePictures = getIntent().getStringArrayListExtra("files");
        GridView gridview = (GridView) findViewById(R.id.gridview);
        storageImages = new ArrayList<>();
        adapter = new LocalImageAdapter(this, storageImages);
        gridview.setAdapter(adapter);

        fileDataBase=FileDataBase.getAppDatabase(this);

        sharedPreferences=getSharedPreferences("location", Context.MODE_PRIVATE);
        String temp=sharedPreferences.getString("longitude","0");
        longitude=Double.parseDouble(temp);
        temp=sharedPreferences.getString("latitude","0");
        latitude=Double.parseDouble(temp);
        storageReference= FirebaseStorage.getInstance().getReference();

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int selectedIndex = adapter.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    adapter.selectedPositions.remove(selectedIndex);
                    ((LocalImageCustomView)v).display(false);

                } else {
                    adapter.selectedPositions.add(position);
                    ((LocalImageCustomView)v).display(true);
                }
            }
        });

        getImages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_burst_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shoot_icon:
                shootPictures();
                return true;

            case R.id.download_icon:
                savePictures();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    public void shootPictures()
    {
        if(adapter.selectedPositions.size()==0)
        {
            Toast.makeText(this, "Select some pictures to shoot", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Shooting Pictures!", Toast.LENGTH_SHORT).show();
        for (int i = 0; i<adapter.selectedPositions.size();i++)
        {

            String path = storagePictures.get(adapter.selectedPositions.get(i));
            shootPicture(path);
        }

        for (int i = 0; i < storagePictures.size(); i++) {
            if(adapter.selectedPositions.contains(i))
                continue;

            File imgFile = new File(storagePictures.get(i));
            imgFile.delete();
        }
        finish();

    }

    public void shootPicture(final String path)
    {
        Long ts = System.currentTimeMillis() / 1000;
        final Moment moment=new Moment(FirebaseAuth.getInstance().getUid(),longitude,false,latitude,ts);
        final DatabaseReference momentRef= FirebaseDatabase.getInstance().getReference().child("Moments").push();
        String key=momentRef.getKey();
        StorageReference momentStorageRef = storageReference.child("Moments/"+key+".jpeg");

        Uri file = Uri.fromFile(new File(path));
        UploadTask uploadTask = momentStorageRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                momentRef.setValue(moment);
                File imgFile = new File(path);
                boolean delete = imgFile.delete();
            }
        });

    }


    public void getImages()
    {
        for (int i = 0; i < storagePictures.size(); i++) {

            File imgFile = new File(storagePictures.get(i));

            if (imgFile.exists()) {
                byte[] b = new byte[(int) imgFile.length()];
                try {
                    FileInputStream fileInputStream = new FileInputStream(imgFile);
                    fileInputStream.read(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CameraUtils.decodeBitmap(b, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        storageImages.add(bitmap);
                        adapter.notifyDataSetChanged();
                        //or populate a image view;
                    }
                });

            }
        }
    }

    public void savePictures() {
        if (adapter.selectedPositions.size() == 0) {
            Toast.makeText(this, "Select some pictures to save to local moment", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Saving Pictures to Moment", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < adapter.selectedPositions.size(); i++) {
            addPicToStorage(storagePictures.get(adapter.selectedPositions.get(i)));

        }
        for(int i = 0; i<storagePictures.size();i++)
        {
            if(adapter.selectedPositions.contains(i))
                continue;
            File imgFile = new File(storagePictures.get(i));
            imgFile.delete();
        }
        finish();
    }

    //TODO delete images on activity destroy

    void addPicToStorage(String path)
    {
        new DatabaseAsyncTask(this,path).execute();
    }

    public class DatabaseAsyncTask extends AsyncTask {
        Context c;
        String path;

        DatabaseAsyncTask(Context con, String path)
        {
            c = con;
            this.path = path;
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
