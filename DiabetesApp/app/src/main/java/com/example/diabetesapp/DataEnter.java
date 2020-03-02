package com.example.diabetesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.diabetesapp.data.requests.StoreBSLRequest;
import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.requests.StoreWeightRequest;
import com.example.diabetesapp.data.responses.StoreBSLResponse;
import com.example.diabetesapp.data.responses.StoreRBPResponse;
import com.example.diabetesapp.data.responses.StoreWeightResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;

import androidx.appcompat.app.AppCompatActivity;

public class DataEnter extends AppCompatActivity {

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;
    static final int REQUEST_HEIGHT = 2;
    static final int REQUEST_WEIGHT = 3;

    ImageView back;
    ImageView blood_sugar, blood_pressure, cm, kg;

    int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data__enter);

        mPatientID = getIntent().getIntExtra("tag", -1);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
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

        cm = findViewById(R.id.cm);
        cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(REQUEST_HEIGHT);
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
        } else if (id == REQUEST_HEIGHT) {
            Intent i = new Intent(this, Manual.class);
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

    /*
    private void enterData() {
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        try {
            StoreWeightRequest storeWeightRequest = new StoreWeightRequest(mPatientID, timestamp, Float.parseFloat(weight.getText().toString()));
            storeWeightRequest.makeRequest(this, new Response.Listener<StoreWeightResponse>() {
                @Override
                public void onResponse(StoreWeightResponse response) {
                    Log.println(Log.INFO, "StoreWeightRequest", response.success.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.println(Log.ERROR, "StoreWeightRequest", error.getMessage());
                }
            });
        } catch (Exception e) {

        }
        try {
            StoreBSLRequest storeBSLRequest = new StoreBSLRequest(mPatientID, timestamp, Float.parseFloat(sugar_data), null);
            storeBSLRequest.makeRequest(this, new Response.Listener<StoreBSLResponse>() {
                @Override
                public void onResponse(StoreBSLResponse response) {
                    Log.println(Log.INFO, "StoreBSLRequest", response.success.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.println(Log.ERROR, "StoreBSLRequest", error.getMessage());
                }
            });
        } catch (Exception e) {

        }
        try {
            StoreRBPRequest storeRBPRequest = new StoreRBPRequest(mPatientID, timestamp, Float.parseFloat(pressure_data), Float.parseFloat(pressure_data));
            storeRBPRequest.makeRequest(this, new Response.Listener<StoreRBPResponse>() {
                @Override
                public void onResponse(StoreRBPResponse response) {
                    Log.println(Log.INFO, "StoreRBPRequest", response.success.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.println(Log.ERROR, "StoreRBPRequest", error.getMessage());

                }
            });
        } catch (Exception e) {

        }
    }

     */

    private void back() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}