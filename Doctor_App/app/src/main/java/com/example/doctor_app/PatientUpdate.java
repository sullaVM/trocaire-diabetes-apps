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
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.doctor_app.data.requests.GetPatientProfileRequest;
import com.example.doctor_app.data.requests.UpdatePatientRequest;
import com.example.doctor_app.data.responses.GetPatientProfileResponse;
import com.example.doctor_app.data.responses.UpdatePatientResponse;

public class PatientUpdate extends AppCompatActivity {

    // Input fields
    private EditText firstName;
    private EditText lastName;
    private EditText mobileNumber;
    private EditText height;
    private EditText weight;
    private CheckBox pregnant;
    private EditText username;
    private EditText doctorID;

    private String photoDataUrl;
    private String password;
    private String bslUnit;

    private int patientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_patient);

        // Get inputs
        firstName = findViewById(R.id.editText);
        lastName = findViewById(R.id.editText2);
        mobileNumber = findViewById(R.id.editText3);
        height = findViewById(R.id.editText4);
        weight = findViewById(R.id.editText5);
        pregnant = findViewById(R.id.checkBox);
        username = findViewById(R.id.editText6);
        doctorID = findViewById(R.id.editText7);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        patientID = getIntent().getIntExtra("id", 0);
        getProfile(patientID);

    }

    private void getProfile(int patientID) {
        GetPatientProfileRequest profileRequest = new GetPatientProfileRequest(patientID);
        profileRequest.makeRequest(getBaseContext(), new Consumer<GetPatientProfileResponse>() {
            @Override
            public void accept(GetPatientProfileResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "GetPatientProfile", "Request succeeded");
                    fillForm(response);
                } else {
                    Log.println(Log.INFO, "GetPatientProfile", "Request failed");
                }
            }
        });
    }

    private void fillForm(GetPatientProfileResponse response) {
        firstName.setText(response.firstName);
        lastName.setText(response.lastName);
        mobileNumber.setText(response.mobileNumber.toString());
        height.setText(response.height.toString());
        weight.setText("--");

        if(response.pregnant == 0) {
            pregnant.setChecked(false);
        } else {
            pregnant.setChecked(true);
        }

        username.setText(response.userName);
        doctorID.setText(response.doctorID.toString());

        // Not visible on form
        photoDataUrl = response.photoDataUrl;
        password = response.password;
        bslUnit = response.bslUnit;
    }

    // Called by submit button
    public void updateProfile(View view) {
        Integer doc = Integer.parseInt(doctorID.getText().toString());
        String fir = firstName.getText().toString();
        String las = lastName.getText().toString();
        String use = username.getText().toString();
        String hei = height.getText().toString();
        String wei = weight.getText().toString();
        Integer pre = pregnant.isChecked() ? 1 : 0;
        Integer mob = Integer.parseInt(mobileNumber.getText().toString());

        Log.println(Log.INFO,"UpdatePatient", doc + " " + fir + " " + las + " " +
                use + " " + hei + " " + pre + " " + mob + " " + photoDataUrl + " " +
                bslUnit + " " + patientID);

       UpdatePatientRequest updateRequest = new UpdatePatientRequest(doc, fir, las, use, hei,
               pre, mob, photoDataUrl, bslUnit, patientID);

       updateRequest.makeRequest(getBaseContext(), new Consumer<UpdatePatientResponse>() {
            @Override
            public void accept(UpdatePatientResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "UpdatePatient", "Request succeeded");
                } else {
                    Log.println(Log.INFO, "UpdatePatient", "Request failed");
                }
            }
        });

       // Return to the patient profile

        Patient patient = new Patient(doc, fir, las,
                hei, mob, photoDataUrl, password,
                pre, bslUnit, patientID, false);

        Intent intent = new Intent(getApplicationContext(), Info.class);
        intent.putExtra("info", patient);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void back(View view) {
        finish();
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
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}
