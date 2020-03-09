package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.InputType;

import android.util.Log;

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

public class InputPressureSugar extends AppCompatActivity {

    EditText dataBox1, dataBox2;
    ImageView back, done, camera;

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;

    String input;
    String input2;
    int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pressure_sugar);

        mPatientID = getIntent().getIntExtra("patientId", 0);

        dataBox1 = findViewById(R.id.data);
        dataBox2 = findViewById(R.id.data2);
        dataBox1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dataBox2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getIntExtra("tag", 0)==REQUEST_SUGAR) saveData(REQUEST_SUGAR);
                else saveData(REQUEST_PRESSURE);
            }
        });

        camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                int id = i.getIntExtra("tag", 0);
                if(id==0) OCRNormal(id);
                else OCRSevenDigit(id);
            }
        });
    }

    private void enterManual(int code, int boxNo) {
        Intent intent = new Intent(this, Manual.class);
        intent.getIntExtra("tag", boxNo);
        startActivityForResult(intent, code);
    }

    private void OCRNormal(int code) {
        Intent intent = new Intent(this, CameraSugar.class);
        startActivityForResult(intent, code);
    }

    private void OCRSevenDigit(int code) {
        Intent intent = new Intent(this, Camera.class);
        startActivityForResult(intent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == InputPressureSugar.RESULT_OK) {
            input = data.getStringExtra("input1");
            input2 = data.getStringExtra("input2");
            if (input != null) dataBox1.setText(input);
            if (input2 != null) dataBox2.setText(input2);
        }
    }

    private void back(){
        finish();
    }

    private void saveData(int request){
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        if(request==REQUEST_SUGAR) {

            try {
                StoreBSLRequest storeBSLRequest = new StoreBSLRequest(mPatientID, timestamp, Float.parseFloat(input), null);
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
            } catch (Exception e) {}
        }
        if(request==REQUEST_PRESSURE){
            try {
                StoreRBPRequest storeRBPRequest = new StoreRBPRequest(mPatientID, timestamp, Float.parseFloat(input), Float.parseFloat(input));
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
            } catch (Exception e) {}
        }
    }
}
