package com.example.diabetesapp;

import android.content.Intent;
import android.graphics.Bitmap;
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
    static final int REQUEST_IMAGE_CAPTURE = 2;

    Bitmap bitmap;

    Button back, enter;

    TextView sugar, pressure;
    EditText height, weight;

    String sugar_data, pressure_data;

    int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data__enter);

        sugar = findViewById(R.id.sugar);
        pressure = findViewById(R.id.pressure);

        height = findViewById(R.id.addHeight);
        weight = findViewById(R.id.addWeight);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        ImageView sugar_picture = findViewById(R.id.camera);
        ImageView sugar_numbers = findViewById(R.id.numbers);
        ImageView pressure_picture = findViewById(R.id.camera2);
        ImageView pressure_numbers = findViewById(R.id.numbers2);

        mPatientID = getIntent().getIntExtra("tag", -1);

        sugar_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto("sugar", 0);
            }
        });
        sugar_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manual("sugar", 0);
            }
        });
        pressure_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto("pressure", 1);
            }
        });
        pressure_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manual("pressure", 1);
            }
        });

        enter = findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterData();
            }
        });
    }

    private void manual(String type, int code) {
        Intent intent = new Intent(this, Manual.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, code);
    }

    private void takePhoto(String type, int code) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("type", type);
        takePictureIntent.putExtra("code", code);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DataEnter.RESULT_OK) {
            switch (requestCode) {
                case (0): {
                    sugar_data = data.getStringExtra("sugar");
                    if (sugar_data != null) sugar.setText(sugar_data);
                }
                case (1): {
                    pressure_data = data.getStringExtra("pressure");
                    if (pressure_data != null) pressure.setText(pressure_data);
                }
                case (REQUEST_IMAGE_CAPTURE): {
                    String type = data.getStringExtra("type");
                    int code = data.getIntExtra("code", 1);
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    bitmap = imageBitmap;
                    File photoFile = new File(this.getFilesDir(), "Image.jpg");
                    try {
                        FileOutputStream out = new FileOutputStream(photoFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();

                        Intent intent = new Intent(this, Camera.class);
                        intent.putExtra("type", type);
                        startActivityForResult(intent, code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

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

    private void back() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}