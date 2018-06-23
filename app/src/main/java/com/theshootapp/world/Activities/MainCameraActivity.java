package com.theshootapp.world.Activities;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.otaliastudios.cameraview.SessionType;
import com.theshootapp.world.ModelClasses.LocationModel;
import com.theshootapp.world.R;
import com.theshootapp.world.Services.UserLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import static java.security.AccessController.getContext;

public class MainCameraActivity extends AppCompatActivity {
    CameraView cameraView;
    private boolean isFlashOn;
    private boolean isFrontFacing;
    UserLocation userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().hide();
        Intent serviceIntent=new Intent(this,UserLocation.class);
        startService(serviceIntent);
        cameraView = (CameraView)findViewById(R.id.camera);
        cameraView.setSessionType(SessionType.PICTURE);
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER); // Tap to focus!
        cameraView.setFacing(Facing.BACK);
        isFrontFacing=false;
        cameraView.setFlash(Flash.OFF);
        isFlashOn=false;
        cameraView.setPlaySounds(false);
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                imageCaptured(jpeg);
                super.onPictureTaken(jpeg);
            }
        });


    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }
    public void onPicClick(View view)
    {
        cameraView.capturePicture();
    }
    public void imageCaptured(byte[] jpeg) {
        String filename=writeToFile(jpeg);
        Intent intent = new Intent(this, PictureDisplay.class);
        intent.putExtra("image",filename);
        startActivity(intent);
    }

    public void onMomentClick(View v)
    {
        startActivity(new Intent(this, MomentActivity.class));
    }

    public void onVideoClick(View v)
    {
        startActivity(new Intent(this,VideoCameraActivity.class));
    }

    public void onCalendarClick(View v)
    {
        startActivity(new Intent(this,CalendarActivity.class));
    }

    public String writeToFile(byte[] array)
    {
        try
        {
            String path = "picture.jpeg";

            File file = new File(getFilesDir(), path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(getFilesDir() + File.separator + path);
            stream.write(array);
            return (getFilesDir() + File.separator + path);
        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void onFlashClick(View view)
    {
        if(isFlashOn)
        {
            ((ImageButton)findViewById(R.id.flashButton)).setImageResource(R.drawable.torch_copy);
            cameraView.setFlash(Flash.OFF);
            isFlashOn=false;
        }
        else
        {
            ((ImageButton)findViewById(R.id.flashButton)).setImageResource(R.drawable.flash_on);
            cameraView.setFlash(Flash.ON);
            isFlashOn=true;
        }
    }
    public void onCameraSwap(View view)
    {
        if (isFrontFacing) {
            cameraView.setFacing(Facing.BACK);
            isFrontFacing=false;
        }
        else {
            cameraView.setFacing(Facing.FRONT);
            isFrontFacing=true;
        }

    }
    @Override
    protected void onDestroy() {
        cameraView.destroy();
        super.onDestroy();
    }

    public void onCamera2Video(View view)
    {
        startActivity(new Intent(this,VideoCameraActivity.class));
    }

    public void onCallClick(View view)
    {
        startActivity(new Intent(this,mainCallActivity.class));
    }

}

