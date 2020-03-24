package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.os.Bundle;
import android.util.Log;
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
        String pre= pregnant.isChecked() ? "1" : "0";

        Integer mob = Integer.parseInt(mobileNumber.getText().toString());

        Log.println(Log.INFO,"updatePatient", doc + " " + fir + " " + las + " " +
                use + " " + hei + " " + pre + " " + mob + " " + photoDataUrl + " " +
                bslUnit + " " + patientID);

       UpdatePatientRequest updateRequest = new UpdatePatientRequest(doc, fir, las, use, hei,
               wei, mob, photoDataUrl, bslUnit, patientID);

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
    }

    public void back(View view) {
        finish();
    }
}
