package com.example.diabetesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.StoreBSLRequest;
import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.responses.StoreBSLResponse;
import com.example.diabetesapp.data.responses.StoreRBPResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class InputPressureSugar extends AppCompatActivity {

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;
    private static final String filename = "/StoredData.txt";

    ImageButton camera, camera2, back, done;
    EditText dataBox1, dataBox2;
    String input;
    String input2;
    int mPatientID;
    int intentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_sugar);

        input = "";
        input2 = "";
        intentTag = getIntent().getIntExtra("tag", 0);

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
                if (getIntent().getIntExtra("tag", 0) == REQUEST_SUGAR) {
                    input = dataBox1.getText().toString();
                    if (input.equals(""))
                        dataBox1.setBackgroundColor(getResources().getColor(R.color.light_red));
                    else saveData(REQUEST_SUGAR);
                } else {
                    input = dataBox1.getText().toString();
                    input2 = dataBox2.getText().toString();
                    boolean check = true;
                    if (input.equals("")) {
                        dataBox1.setBackgroundColor(getResources().getColor(R.color.light_red));
                        check = false;
                    } else dataBox1.setBackgroundColor(getResources().getColor(R.color.appWhite));
                    if (input2.equals("")) {
                        dataBox2.setBackgroundColor(getResources().getColor(R.color.light_red));
                        check = false;
                    } else dataBox2.setBackgroundColor(getResources().getColor(R.color.appWhite));
                    if (check) saveData(REQUEST_PRESSURE);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.setBackground(getDrawable(R.drawable.button_background_default_96dp));
        if (intentTag == REQUEST_PRESSURE)
            camera2.setBackground(getDrawable(R.drawable.button_background_default_96dp));
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
                if (data.getBooleanExtra("diastole", true) == true) {
                    input = data.getStringExtra("input");
                    if (input != null) dataBox1.setText(input);
                } else {
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
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            if (request == REQUEST_SUGAR) {
                try {
                    StoreBSLRequest storeBSLRequest = new StoreBSLRequest(mPatientID, timestamp, Float.parseFloat(input), null);
                    storeBSLRequest.makeRequest(this, new Consumer<StoreBSLResponse>() {
                        @Override
                        public void accept(StoreBSLResponse storeBSLResponse) {
                            Log.d("Upload", "BSL request submitted successfully");
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    StoreRBPRequest storeRBPRequest = new StoreRBPRequest(mPatientID, timestamp, Float.parseFloat(input), Float.parseFloat(input2));
                    storeRBPRequest.makeRequest(this, new Consumer<StoreRBPResponse>() {
                        @Override
                        public void accept(StoreRBPResponse storeRBPResponse) {
                            Log.d("Upload", "RBP request submitted successfully");
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (request == REQUEST_SUGAR) {
                if (input == null) input = dataBox1.getText().toString();
                try {
                    File textFile = new File(this.getFilesDir(), filename);
                    if (!textFile.exists())
                        textFile.createNewFile();

                    BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true));

                    writer.write("BSL " + mPatientID + " " + Float.parseFloat(input) + "\n");
                    writer.close();
                } catch (IOException e) {
                    Log.e("ReadWriteFile", "Unable to write data.");
                }
            } else if (request == REQUEST_PRESSURE) {
                if (input == null) input = dataBox1.getText().toString();
                if (input2 == null) input2 = dataBox2.getText().toString();
                try {
                    File textFile = new File(this.getFilesDir(), filename);
                    if (!textFile.exists())
                        textFile.createNewFile();

                    BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true));

                    writer.write("RBP " + mPatientID + " " + Float.parseFloat(input) + " " + Float.parseFloat(input2) + "\n");
                    writer.close();
                } catch (IOException e) {
                    Log.e("ReadWriteFile", "Unable to write data.");
                }
            }
        }
        finish();
    }
}
