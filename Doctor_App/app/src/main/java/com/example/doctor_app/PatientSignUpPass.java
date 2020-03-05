package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

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
import com.example.doctor_app.data.requests.CreatePatientRequest;
import com.example.doctor_app.data.responses.CreatePatientResponse;

import java.util.List;

public class PatientSignUpPass extends AppCompatActivity {

    // Input fields
    private PatternLockView mPatternLockView;

    // Submit button
    private Button next;

    // Pattern lock view result variable
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_pass);

        // Get inputs
        mPatternLockView = findViewById(R.id.pattern_lock_view);

        // Get next button
        next = findViewById(R.id.enter);

        // Set Listeners
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                enterData(intent);
            }
        });
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

    private void enterData(Intent intent) {

        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        int mobileNumber = intent.getIntExtra("mobileNumber",0);
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");
        String pregnant = intent.getStringExtra("pregnant");
        String photoDataUrl = intent.getStringExtra("photoDataUrl");
        int doctorID = intent.getIntExtra("doctorID", 0);

        // Test info
        Log.println(Log.INFO, "doctorID", Integer.toString(doctorID));
        Log.println(Log.INFO, "firstName", firstName);
        Log.println(Log.INFO, "lastName", lastName);
        Log.println(Log.INFO, "height", height);
        Log.println(Log.INFO, "mobileNumber", Integer.toString(mobileNumber));
        Log.println(Log.INFO, "photoDataUrl", photoDataUrl);
        Log.println(Log.INFO, "password", password);

        // Create the patient using the API
        CreatePatientRequest patientRequest = new CreatePatientRequest(doctorID, firstName,
                lastName, height, mobileNumber, photoDataUrl, password, "test");
        patientRequest.makeRequest(getBaseContext(), new Consumer<CreatePatientResponse>() {
            @Override
            public void accept(CreatePatientResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "CreatePatientRequest", "Request succeeded");
                } else {
                    Log.println(Log.INFO, "CreatePatientRequest", "Request failed");
                }
            }
        });

        // Save the profile photo URL locally
        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exist, it is created.
            File textFile = new File(this.getFilesDir(), "TextFile.txt");
            if (!textFile.exists())
                textFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true /*append*/));

            writer.write(photoDataUrl + " " + ".\n");
            writer.close();

            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(this, new String[]{textFile.toString()},
                    null, null);
        }
        catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write data.");
        }

        // Finish the patient sign up
        intent = new Intent(getApplicationContext(), Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
