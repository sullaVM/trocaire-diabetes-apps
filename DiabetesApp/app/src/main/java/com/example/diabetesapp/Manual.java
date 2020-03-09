package com.example.diabetesapp;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.StoreWeightRequest;
import com.example.diabetesapp.data.responses.StoreWeightResponse;

import java.sql.Timestamp;

public class Manual extends AppCompatActivity {
    EditText data;

    int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ImageView enter = findViewById(R.id.enter);
        ImageView back = findViewById(R.id.back);

        data = findViewById(R.id.enterData);
        data.setInputType(InputType.TYPE_CLASS_NUMBER);

        mPatientID = getIntent().getIntExtra("patientId", -1);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
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

    private void saveData() {

        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        try {
            StoreWeightRequest storeWeightRequest = new StoreWeightRequest(mPatientID, timestamp, Float.parseFloat(data.getText().toString()));
            storeWeightRequest.makeRequest(this, new Consumer<StoreWeightResponse>() {
                @Override
                public void accept(StoreWeightResponse storeWeightResponse) {
                    finish();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT);
        }
    }
}
