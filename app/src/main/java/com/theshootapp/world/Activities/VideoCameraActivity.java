package com.theshootapp.world.Activities;

import android.content.Intent;
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
import com.otaliastudios.cameraview.VideoQuality;
import com.theshootapp.world.R;

import java.io.File;
import java.io.IOException;

public class VideoCameraActivity extends AppCompatActivity {

    CameraView cameraView;
    String videoFileName;
    private boolean isFlashOn;
    private boolean isFrontFacing;
    private boolean isVideoRecording;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_camera);
        getSupportActionBar().hide();

        cameraView = (CameraView)findViewById(R.id.Videocamera);
        cameraView.setSessionType(SessionType.VIDEO);
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER); // Tap to focus!
        cameraView.setFacing(Facing.BACK);
        isFrontFacing=false;
        cameraView.setFlash(Flash.OFF);
        isFlashOn=false;
        isVideoRecording=false;
        cameraView.setPlaySounds(false);//Check
        cameraView.setVideoQuality(VideoQuality.MAX_480P);
        cameraView.setVideoMaxDuration(10000);
        videoFileName=getFilesDir() + File.separator + "tempVideo";
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                // The File is the same you passed before.
                // Now it holds a MP4 video.
                ((ImageButton)findViewById(R.id.videoButton)).setImageResource(R.drawable.ic_fiber_manual_record_white_24dp);
                Intent i= new Intent(VideoCameraActivity.this,VideoPreviewActivity.class);
                i.putExtra("videoPath",videoFileName);
                startActivity(i);
            }
        });
    }

    public void onVideoClick(View view)
    {
        if(!isVideoRecording) {
            isVideoRecording=true;
            ((ImageButton)findViewById(R.id.videoButton)).setImageResource(R.drawable.ic_fiber_manual_record_red_24dp);
            File file = new File(videoFileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cameraView.startCapturingVideo(file);
        }
        else
        {((ImageButton)findViewById(R.id.videoButton)).setImageResource(R.drawable.ic_fiber_manual_record_white_24dp);
            cameraView.setFlash(Flash.OFF);
            isFlashOn = false;
            isVideoRecording=false;
            cameraView.stopCapturingVideo();
        }


    }
    public void onVideoFlashClick(View view)
    {
        if(isVideoRecording) {
            if (isFlashOn) {
                ((ImageButton)findViewById(R.id.flashButtonVideo)).setImageResource(R.drawable.torch_copy);
                cameraView.setFlash(Flash.OFF);
                isFlashOn = false;
            } else {
                ((ImageButton)findViewById(R.id.flashButtonVideo)).setImageResource(R.drawable.flash_on);
                cameraView.setFlash(Flash.TORCH);
                isFlashOn = true;
            }
        }
    }
    public void onVideoCameraSwap(View view)
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

    protected void onPause() {
        cameraView.stop();
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }

    @Override
    protected void onDestroy() {
        cameraView.destroy();
        super.onDestroy();
    }
}
