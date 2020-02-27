package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import java.io.FileOutputStream;

import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import androidx.appcompat.app.AppCompatActivity;

public class DataEnter extends AppCompatActivity {

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    Button back;
    Button enter;

    TextView sugar;
    TextView pressure;

    ImageView sugar_picture;
    ImageView sugar_numbers;

    ImageView pressure_picture;
    ImageView pressure_numbers;

    EditText height;
    EditText weight;

    String sugar_data;
    String pressure_data;

    public static Bitmap bitmap;

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

        sugar_picture = findViewById(R.id.camera);
        sugar_numbers = findViewById(R.id.numbers);
        pressure_picture = findViewById(R.id.camera2);
        pressure_numbers = findViewById(R.id.numbers2);

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
            public void onClick(View view) { takePhoto("pressure", 1); }
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
        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takePictureIntent = new Intent(this, Camera.class);
        takePictureIntent.putExtra("type", type);
        takePictureIntent.putExtra("code", code);
        startActivity(takePictureIntent);
        //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
        String h = height.getText().toString();
        String w = weight.getText().toString();
        String sugar = sugar_data;
        String pressure = pressure_data;
    }

    private void back() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}