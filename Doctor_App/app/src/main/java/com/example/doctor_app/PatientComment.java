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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.doctor_app.data.requests.StorePatientLogRequest;
import com.example.doctor_app.data.responses.StorePatientLogResponse;

import java.sql.Timestamp;

public class PatientComment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String patientName;
    private int patientID;

    private EditText commentInput;

    private int weeks = 1;

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

        String[] numbers = new String[]{"1 week", "2 weeks", "3 weeks", "4 weeks"};
        Spinner spinner = findViewById(R.id.weeks_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbers);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void submit(View view) {
        String comment = commentInput.getText().toString();

        Timestamp timetamp = new Timestamp(System.currentTimeMillis());
        String time = timetamp.toString();

        Log.println(Log.INFO, "StorePatientLog", "patientID: " + patientID +
                " time: " + time + " note: " + comment);

        StorePatientLogRequest log = new StorePatientLogRequest(patientID, time, comment, weeks);

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

    // Spinner
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        weeks = position + 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing. Default is 1 week.
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
