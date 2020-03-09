package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.diabetesapp.data.requests.PatientLoginRequest;
import com.example.diabetesapp.data.responses.PatientLoginResponse;

import java.util.List;


public class InputPassword extends AppCompatActivity {

    PatternLockView mPatternLockView;
    int mPatientID;


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
            String password = PatternLockUtils.patternToString(mPatternLockView, pattern);
            checkPassword(password);
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input__password);

        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);

        mPatientID = getIntent().getIntExtra("tag", -1);
    }

    void checkPassword(final String password) {

        final PatientLoginRequest patientLoginRequest = new PatientLoginRequest(mPatientID, password);
        patientLoginRequest.makeRequest(getBaseContext(), new Consumer<PatientLoginResponse>() {
            @Override
            public void accept(PatientLoginResponse patientLoginResponse) {
                if (patientLoginResponse.success != null && patientLoginResponse.success) {
                    Intent intent = new Intent(getBaseContext(), DataEnter.class);
                    intent.putExtra("tag", mPatientID);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "User not found", Toast.LENGTH_SHORT);
                }
            }
        });
    }

}
