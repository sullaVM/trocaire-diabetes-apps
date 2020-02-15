package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import android.media.MediaScannerConnection;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class Patient_SignUp_Password extends AppCompatActivity {

    PatternLockView mPatternLockView;

    Button enter;

    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__sign_up__password);

        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);


        enter = findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                enterData(intent);
            }
        });
    }

    private void enterData(Intent intent) {
        String name = intent.getStringExtra("name");
        String clinic = intent.getStringExtra("clinic");
        int number = intent.getIntExtra("number", 1);

        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exist, it is created.
            File textFile = new File(this.getFilesDir(), "TextFile.txt");
            if (!textFile.exists())
                textFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true /*append*/));

            writer.write(number + " " + name + " " + password + " " + clinic + "\n");
            writer.close();

            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(this,
                    new String[]{textFile.toString()},
                    null,
                    null);
        }
        catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write data.");
        }

        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            password = PatternLockUtils.patternToString(mPatternLockView, pattern);
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };
}
