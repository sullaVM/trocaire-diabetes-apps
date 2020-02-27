package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class InputPassword extends AppCompatActivity {

    Button enter;
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
            checkPassword(mPatientID, password);
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

    void checkPassword(int tag, String password) {
        /*
        File testFile = new File(this.getFilesDir(), "TextFile.txt");
        if (testFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                for (int i = 1; i < tag; i++) {
                    line = reader.readLine();
                }
                line = reader.readLine();
                String[] info = line.split(" ");
                String realPassword = info[3];

         */
        //if (realPassword.equals(password)) {
        Intent intent = new Intent(this, DataEnter.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
                /*} else {
                    //Intent intent = new Intent(this, MainActivity.class);
                    //startActivity(intent);
                }
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TextFile.txt file.");
            }
        }
        */
    }

}