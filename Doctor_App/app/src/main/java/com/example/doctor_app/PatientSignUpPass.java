package com.example.doctor_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.doctor_app.data.requests.CreatePatientRequest;
import com.example.doctor_app.data.responses.CreatePatientResponse;

import java.util.List;

public class PatientSignUpPass extends AppCompatActivity {

    // Input fields
    private PatternLockView mPatternLockView;
    private EditText mEditText;

    // Submit button
    private Button next;

    // Pattern lock view result variable
    private String password;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_pass);

        // Get inputs
        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mEditText = findViewById(R.id.editText1);

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

    private void enterData(final Intent intent) {

        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        int mobileNumber = intent.getIntExtra("mobileNumber", 0);
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");
        int pregnant = intent.getIntExtra("pregnant", CreatePatientRequest.NOT_PREGNANT);
        final String photoDataUrl = intent.getStringExtra("photoDataUrl");
        final int doctorID = intent.getIntExtra("doctorID", 0);

        // Test info
        Log.println(Log.INFO, "doctorID", Integer.toString(doctorID));
        Log.println(Log.INFO, "firstName", firstName);
        Log.println(Log.INFO, "lastName", lastName);
        Log.println(Log.INFO, "height", height);
        Log.println(Log.INFO, "mobileNumber", Integer.toString(mobileNumber));
        Log.println(Log.INFO, "password", password);
        Log.println(Log.INFO, "pregnant", Integer.toString(pregnant));

        if (photoDataUrl != null) {
            Log.println(Log.INFO, "photoDataUrl", photoDataUrl);
        }

        // Create the patient using the API
        CreatePatientRequest patientRequest = new CreatePatientRequest(doctorID, mEditText.getText().toString(), password, firstName,
                lastName, height, mobileNumber, pregnant, "mmolL", photoDataUrl);
        patientRequest.makeRequest(getBaseContext(), new Consumer<CreatePatientResponse>() {
            @Override
            public void accept(CreatePatientResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "CreatePatientRequest", "Request succeeded");
                    success(doctorID, response.patientID, photoDataUrl, intent);
                } else if (response != null && response.success == false) {
                    if (response.message != null) {
                        Toast.makeText(getBaseContext(), response.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.println(Log.INFO, "CreatePatientRequest", "Request failed");
                    fail(doctorID, intent);
                }
            }
        });
    }

    private void success(int doctorID, int patientID, String photoDataUrl, Intent intent) {
        // Finish
        intent = new Intent(getApplicationContext(), Dashboard.class);
        intent.putExtra("tag", doctorID);
        intent.putExtra("patientID", patientID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void fail(int doctorID, Intent intent) {
        // Finish
        intent = new Intent(getApplicationContext(), Dashboard.class);
        intent.putExtra("tag", doctorID);
        intent.putExtra("patientID", -1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
