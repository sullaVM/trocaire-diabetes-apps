package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;
import android.net.Uri;
import android.widget.MediaController;

public class VideoActivity extends AppCompatActivity {

    VideoView video;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        video = findViewById(R.id.video);

        Intent i = getIntent();
        String str = i.getStringExtra("video");
        String videoPath = "";
        if(str.equals("login")) videoPath = "android.resource://" + getPackageName() + "/" + R.raw.login;
        if(str.equals("weight")) videoPath = "android.resource://" + getPackageName() + "/" + R.raw.weight;
        if(str.equals("pressure")) videoPath = "android.resource://" + getPackageName() + "/" + R.raw.pressure;
        if(str.equals("sugar")) videoPath = "android.resource://" + getPackageName() + "/" + R.raw.sugar;
        if(str.equals("pressure_entry")) videoPath = "android.resource://" + getPackageName() + "/" + R.raw.pressure_entry;
        if(str.equals("sugar_entry")) videoPath = "android.resource://" + getPackageName() + "/" + R.raw.sugar_entry;
            Uri uri = Uri.parse(videoPath);
            video.setVideoURI(uri);

            MediaController mediaController = new MediaController(this);
            video.setMediaController(mediaController);
            mediaController.setAnchorView(video);
    }

    private void back(){
        finish();
    }
}
