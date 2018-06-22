package com.theshootapp.world.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.MediaController;
import android.widget.VideoView;

import com.theshootapp.world.R;

public class VideoPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        getSupportActionBar().hide();
        Intent i=getIntent();
        String path=i.getStringExtra("videoPath");
        VideoView videoView = (VideoView)findViewById(R.id.videoPreviewView);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);

        videoView.setVideoURI(Uri.parse(path));
        videoView.requestFocus();
        videoView.start();
    }
}
