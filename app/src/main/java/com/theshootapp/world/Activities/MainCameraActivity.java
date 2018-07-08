package com.theshootapp.world.Activities;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.otaliastudios.cameraview.SessionType;
import com.sinch.android.rtc.SinchError;
import com.theshootapp.world.Database.MyFile;
import com.theshootapp.world.ModelClasses.LocationModel;
import com.theshootapp.world.R;
import com.theshootapp.world.Services.SinchService;
import com.theshootapp.world.Services.UserLocation;
import com.theshootapp.world.Services.UserLocation24Hrs;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import static java.security.AccessController.getContext;

public class MainCameraActivity extends BaseActivity implements SinchService.StartFailedListener {
    CameraView cameraView;
    boolean  callClicked;
    private boolean isFlashOn;
    private boolean isFrontFacing;
    UserLocation userLocation;
    SharedPreferences sharedPreferences;
    long fileId;
    String fileName;
    ArrayList<String>files;
    boolean isLongPress;
    boolean burstDone;
    boolean simpleClick;
    ImageButton button;
    private Timer mTimer1;
    int count=0;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().hide();
        isLongPress=false;
        burstDone=false;
        simpleClick=false;
        button=findViewById(R.id.imageButton2);
       // Intent serviceIntent=new Intent(this,UserLocation.class);
        //startService(serviceIntent);
        Intent serviceIntent=new Intent(this,UserLocation24Hrs.class);
        startService(serviceIntent);
        //Intent suggestionIntent=new Intent(this, SuggestionFetcher.class);
        //startService(suggestionIntent);
        cameraView = (CameraView)findViewById(R.id.camera);
        cameraView.setSessionType(SessionType.PICTURE);
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER); // Tap to focus!
        cameraView.setFacing(Facing.BACK);
        isFrontFacing=false;
        cameraView.setFlash(Flash.OFF);
        isFlashOn=false;
        callClicked=false;
        cameraView.setPlaySounds(false);
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                imageCaptured(jpeg);
                super.onPictureTaken(jpeg);
            }
        });

        createNotificationChannel();
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isLongPress=true;
                files=new ArrayList<>();
                count=0;
                startTimer();
                return false;
            }
        });
      /* button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(isLongPress)
                    {
                        System.out.println("Stop" );
                        stopTimer();
                        burstDone=true;

                    }

                }
                return false;
            }
        });*/

    }


    private void startTimer(){
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        updateFileName();
                        count++;
                        System.out.println(count);
                        cameraView.captureSnapshot();
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 400);
    }

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
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
        if(!isLongPress) {
            System.out.println("Stop555" );
             simpleClick=true;
             updateFileName();
            cameraView.capturePicture();
        }
        else {
            isLongPress = false;
            System.out.println("Stop" );
            stopTimer();
            burstDone=true;
            updateFileName();
            cameraView.captureSnapshot();
        }
    }
    public void imageCaptured(byte[] jpeg) {
        if(simpleClick) {
            simpleClick=false;
            String filename = writeToFile(jpeg);
            Intent intent = new Intent(this, PictureDisplay.class);
            intent.putExtra("image", filename);
            startActivity(intent);
        }
        else
        {

            files.add(writeToFile(jpeg));
            if(burstDone)
            {
                    burstDone = false;
                    Intent i = new Intent(this,BurstActivity.class);
                    i.putStringArrayListExtra("files",files);
                    /*for (int i = 0; i < files.size(); i++) {
                        System.out.println(files.get(i));
                    }*/
                    startActivity(i);

            }

        }
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

    public void onCallClick(View view)
    {
        startActivity(new Intent(this,PhoneActivity.class));
    }

    public void onShootClick(View view)
    {
        startActivity(new Intent(this,LocalImages.class));
    }


    public String writeToFile(byte[] array)
    {
        try
        {
            String path = fileName;

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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_channel_01", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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


    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, "ServiceFalied", Toast.LENGTH_LONG);
    }


    @Override
    public void onStarted() {
        if (callClicked) {
            callClicked = false;
            openPlaceCallActivity();
        }
    }
    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(MainCameraActivity.this);
    }
    private void callClicked() {
        String userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        callClicked = true;

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!userName.equals(getSinchServiceInterface().getUserName())) {
            getSinchServiceInterface().stopClient();
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            // showSpinner();
        } else {
            openPlaceCallActivity();
        }
    }



    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, mainCallActivity.class);
        startActivity(mainActivity);
    }

    void updateFileName()
    {
        sharedPreferences=getSharedPreferences("FileId", Context.MODE_PRIVATE);
        fileId=sharedPreferences.getLong("fileId",0l);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong("fileId",fileId+1);
        editor.commit();
        fileName="file"+String.valueOf(fileId)+".jpeg";
    }



}

