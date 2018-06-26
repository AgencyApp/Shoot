package com.theshootapp.world.Activities;

import android.graphics.Bitmap;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.otaliastudios.cameraview.CameraUtils;
import com.theshootapp.world.Adapters.ImageAdapter;
import com.theshootapp.world.Adapters.LocalImageAdapter;
import com.theshootapp.world.Database.FileDataBase;
import com.theshootapp.world.Database.MyFile;
import com.theshootapp.world.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LocalImages extends AppCompatActivity {

    ArrayList<String> momentIdsTobeDeleted;
    long currentTime;
    DatabaseReference databaseReference;
    LocalImageAdapter adapter;
    ArrayList<MyFile> storagePictures;
    FileDataBase fileDataBase;
    ArrayList<Bitmap> storageImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_images);
        currentTime = System.currentTimeMillis() / 1000;
        setTitle("Saved Media");;
        storagePictures = new ArrayList<>();
        GridView gridview = (GridView) findViewById(R.id.gridview);
        fileDataBase = FileDataBase.getAppDatabase(this);
        storageImages = new ArrayList<>();
        adapter = new LocalImageAdapter(this, storageImages);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int selectedIndex = adapter.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    adapter.selectedPositions.remove(selectedIndex);
                    ((ImageView) v).setColorFilter(null);

                } else {
                    adapter.selectedPositions.add(position);
                    ((ImageView) v).setColorFilter(ContextCompat.getColor(LocalImages.this, R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY);

                }
            }
        });
        fetchImagesfromStorage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shoot_icon:
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    void fetchImagesfromStorage() {
        storagePictures = (ArrayList<MyFile>) fileDataBase.fileDao().getAll();
        for (int i = 0; i < storagePictures.size(); i++) {
            getFileBitMap(storagePictures.get(i));
        }

    }

    void getFileBitMap(MyFile myFile) {
        File imgFile = new File(myFile.getFileName());

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
                    //or populate a image view;
                }
            });
        }
    }

}
