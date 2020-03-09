package com.example.diabetesapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.text.InputType;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.diabetesapp.data.requests.StoreBSLRequest;
import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.requests.StoreWeightRequest;
import com.example.diabetesapp.data.responses.StoreBSLResponse;
import com.example.diabetesapp.data.responses.StoreRBPResponse;
import com.example.diabetesapp.data.responses.StoreWeightResponse;

import java.sql.Timestamp;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Manual extends AppCompatActivity {

    static final int STORE_HEIGHT = 0;
    static final int STORE_WEIGHT = 1;

    ImageView enter, back;
    EditText data;

    String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        enter = findViewById(R.id.enter);
        data = findViewById(R.id.enterData);
        data.setInputType(InputType.TYPE_CLASS_NUMBER);
        back = findViewById(R.id.back);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                int type = intent.getIntExtra("tag", 0);
                if(type==2) saveData(STORE_HEIGHT, intent);
                if(type==3) saveData(STORE_WEIGHT, intent);
                //else enterData("input");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    public void back() {
        this.finish();
    }

    private void saveData(int tag, Intent i){

        if(tag==STORE_WEIGHT) {
            int mPatientID = i.getIntExtra("patientId", 0);
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            try {
                StoreWeightRequest storeWeightRequest = new StoreWeightRequest(mPatientID, timestamp, Float.parseFloat(data.getText().toString()));
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
        }
    }

    private void enterData(String type) {
        input = data.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(type, input);
        setResult(InputPressureSugar.RESULT_OK, resultIntent);
        finish();
    }
}
