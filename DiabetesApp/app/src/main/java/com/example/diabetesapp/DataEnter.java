package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.PatientLogoutRequest;
import com.example.diabetesapp.data.responses.PatientLogoutResponse;
import com.example.diabetesapp.login.User;

public class DataEnter extends AppCompatActivity {

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;
    static final int REQUEST_WEIGHT = 2;

    ImageButton logOut, blood_sugar, blood_pressure, kg;

    int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data__enter);

        mPatientID = getIntent().getIntExtra("tag", -1);

        logOut = findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        blood_sugar = findViewById(R.id.blood_sugar);
        blood_sugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(REQUEST_SUGAR);
            }
        });

        blood_pressure = findViewById(R.id.blood_pressure);
        blood_pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(REQUEST_PRESSURE);
            }
        });

        kg = findViewById(R.id.kg);
        kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(REQUEST_WEIGHT);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void goTo(int id) {
        if (id == REQUEST_SUGAR) {
            Intent i = new Intent(this, InputPressureSugar.class);
            i.putExtra("tag", id);
            i.putExtra("patientId", mPatientID);
            startActivity(i);
        } else if (id == REQUEST_PRESSURE) {
            Intent i = new Intent(this, InputPressureSugar.class);
            i.putExtra("tag", id);
            i.putExtra("patientId", mPatientID);
            startActivity(i);
        } else if (id == REQUEST_WEIGHT) {
            Intent i = new Intent(this, Manual.class);
            i.putExtra("tag", id);
            i.putExtra("patientId", mPatientID);
            startActivity(i);
        }
    }

    private void logOut() {
        PatientLogoutRequest patientLogoutRequest = new PatientLogoutRequest(mPatientID);
        patientLogoutRequest.makeRequest(this, new Consumer<PatientLogoutResponse>() {
            @Override
            public void accept(PatientLogoutResponse patientLogoutResponse) {
                User.logOut(getBaseContext());
                finish();
            }
        });
    }
}