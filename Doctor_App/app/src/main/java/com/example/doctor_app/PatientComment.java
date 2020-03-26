package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.doctor_app.data.requests.StorePatientLogRequest;
import com.example.doctor_app.data.responses.StorePatientLogResponse;

import java.sql.Timestamp;

public class PatientComment extends AppCompatActivity {

    private String patientName;
    private int patientID;

    private EditText commentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_comment);


        patientName = getIntent().getStringExtra("name");
        patientID = getIntent().getIntExtra("id", 0);

        // UI Components
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("Patient " + patientName);
        setSupportActionBar(toolbar);
        commentInput = findViewById(R.id.editText);
    }

    public void submit(View view) {
        String comment = commentInput.getText().toString();

        Timestamp timetamp = new Timestamp(System.currentTimeMillis());
        String time = timetamp.toString();

        StorePatientLogRequest log = new StorePatientLogRequest(patientID,time,comment);

        log.makeRequest(getBaseContext(), new Consumer<StorePatientLogResponse>() {
            @Override
            public void accept(StorePatientLogResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "StorePatientLog", "Request succeeded");
                } else {
                    Log.println(Log.INFO, "StorePatientLog", "Request failed");
                }
            }
        });

        this.finish();
    }

    // Top bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);
        return true;
    }

    // Top bar menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                back();
                return true;
            default:
                return false;
        }
    }

    // Return to login activity (i.e. sign out)
    private void back() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void cancel(View view) {
        finish();
    }
}
