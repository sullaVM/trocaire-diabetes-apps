package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.diabetesapp.data.requests.StoreBSLRequest;
import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.responses.StoreBSLResponse;
import com.example.diabetesapp.data.responses.StoreRBPResponse;

import java.sql.Timestamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import android.util.Log;

public class InputPressureSugar extends AppCompatActivity {

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;
    private static final String filename = "StoredData.txt";

    ImageButton camera, camera2, back, done;
    EditText dataBox1, dataBox2;
    String input;
    String input2;
    int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_sugar);

        if (getIntent().getIntExtra("tag", 0) == REQUEST_SUGAR) {
            camera = findViewById(R.id.camera);
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getIntent();
                    int id = i.getIntExtra("tag", 0);
                    OCRNormal(id);
                }
            });

            mPatientID = getIntent().getIntExtra("patientId", 0);

            dataBox1 = findViewById(R.id.data1);
            dataBox1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            setContentView(R.layout.activity_input_pressure_sugar);

            camera = findViewById(R.id.camera);
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getIntent();
                    int id = i.getIntExtra("tag", 0);
                    OCRSevenDigit(id, true);
                }
            });

            camera2 = findViewById(R.id.camera2);
            camera2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getIntent();
                    int id = i.getIntExtra("tag", 0);
                    OCRSevenDigit(id, false);
                }
            });

            mPatientID = getIntent().getIntExtra("patientId", 0);

            dataBox1 = findViewById(R.id.data);
            dataBox2 = findViewById(R.id.data2);
            dataBox1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            dataBox2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

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
                if (getIntent().getIntExtra("tag", 0) == REQUEST_SUGAR) saveData(REQUEST_SUGAR);
                else saveData(REQUEST_PRESSURE);
            }
        });

    }

    private void OCRNormal(int code) {
        Intent intent = new Intent(this, CameraSugar.class);
        startActivityForResult(intent, code);
    }

    private void OCRSevenDigit(int code, boolean diastole) {
        Intent intent = new Intent(this, Camera.class);
        intent.putExtra("diastole", diastole);
        startActivityForResult(intent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == InputPressureSugar.RESULT_OK) {
            if (getIntent().getIntExtra("tag", 0) == REQUEST_PRESSURE) {
                if(data.getBooleanExtra("diastole", true) == true){
                    input = data.getStringExtra("input");
                    if (input != null) dataBox1.setText(input);
                }
                else {
                    input2 = data.getStringExtra("input");
                    if (input2 != null) dataBox2.setText(input2);
                }
            } else {
                input = data.getStringExtra("input1");
                if (input != null) dataBox1.setText(input);
            }
        }
    }

    private void back() {
        finish();
    }

    private void saveData(int request) {
        if (request == REQUEST_SUGAR) {
            if(input==null) input = dataBox1.getText().toString();
            try {
                File textFile = new File(this.getFilesDir(), filename);
                if (!textFile.exists())
                    textFile.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true));

                writer.write("BSL " + mPatientID + " " +  Float.parseFloat(input) + "\n");
                writer.close();
            } catch (IOException e) {
                Log.e("ReadWriteFile", "Unable to write data.");
            }
        } else if (request == REQUEST_PRESSURE) {
            if(input==null) input = dataBox1.getText().toString();
            if(input2==null) input2 = dataBox2.getText().toString();
                try {
                    File textFile = new File(this.getFilesDir(), filename);
                    if (!textFile.exists())
                        textFile.createNewFile();

                    BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true));

                    writer.write("RBP " + mPatientID + " " +  Float.parseFloat(input) + " " +  Float.parseFloat(input2) + "\n");
                    writer.close();
                } catch (IOException e) {
                    Log.e("ReadWriteFile", "Unable to write data.");
                }
        }
        finish();
    }
}
