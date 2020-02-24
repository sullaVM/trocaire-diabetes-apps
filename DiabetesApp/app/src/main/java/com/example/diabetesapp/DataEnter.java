package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import androidx.appcompat.app.AppCompatActivity;

public class DataEnter extends AppCompatActivity {

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
                takePhoto("sugar");
            }
        });
        sugar_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manual("sugar");
            }
        });
        pressure_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto("pressure");
            }
        });
        pressure_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manual("pressure");
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

    private void manual(String type) {
        Intent intent = new Intent(this, Manual.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, 0);
    }

    private void takePhoto(String type) {
        Intent intent = new Intent(this, Camera.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, 0);
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