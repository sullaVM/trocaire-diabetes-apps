package com.example.diabetesapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.requests.StoreWeightRequest;
import com.example.diabetesapp.data.responses.StoreRBPResponse;
import com.example.diabetesapp.data.responses.StoreWeightResponse;

import java.sql.Timestamp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import android.util.Log;

public class Manual extends AppCompatActivity {

    EditText data;
    ImageButton enter, back;
    int mPatientID;

    private static final String filename = "StoredData.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        enter = findViewById(R.id.enter);
        back = findViewById(R.id.back);

        data = findViewById(R.id.enterData);
        data.setInputType(InputType.TYPE_CLASS_NUMBER);

        mPatientID = getIntent().getIntExtra("patientId", -1);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.getText().toString().equals("")) data.setBackgroundColor(getResources().getColor(R.color.light_red));
                else saveData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.setBackground(getDrawable(R.drawable.button_background_pressed_48dp));
                back();
            }
        });
    }

    public void back() {
        this.finish();
    }

    private void saveData() {
        enter.setBackground(getDrawable(R.drawable.button_background_pressed_48dp));

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            try {
                StoreWeightRequest storeWeightRequest = new StoreWeightRequest(mPatientID, timestamp, Float.parseFloat(data.getText().toString()));
                storeWeightRequest.makeRequest(this, new Consumer<StoreWeightResponse>() {
                    @Override
                    public void accept(StoreWeightResponse storeWeightResponse) {
                        Log.d("Upload", "Weight request submitted successfully");
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            try {
                File textFile = new File(this.getFilesDir(), filename);
                if (!textFile.exists())
                    textFile.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true /*append*/));

                writer.write("W " + mPatientID + " " + Float.parseFloat(data.getText().toString()) + "\n");
                writer.close();
            } catch (IOException e) {
                Log.e("ReadWriteFile", "Unable to write data.");
            }
        }
        finish();
    }
}
