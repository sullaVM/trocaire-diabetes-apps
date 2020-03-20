package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class DataEnter extends AppCompatActivity {

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;
    static final int REQUEST_WEIGHT = 2;

    ImageButton back, blood_sugar, blood_pressure, kg;

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
                back.setBackground(getDrawable(R.drawable.button_background_pressed_48dp));
                back();
            }
        });

        blood_sugar = findViewById(R.id.blood_sugar);
        blood_sugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blood_sugar.setBackground(getDrawable(R.drawable.button_background_pressed_48dp));
                goTo(REQUEST_SUGAR);
            }
        });

        blood_pressure = findViewById(R.id.blood_pressure);
        blood_pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blood_pressure.setBackground(getDrawable(R.drawable.button_background_pressed_48dp));
                goTo(REQUEST_PRESSURE);
            }
        });

        kg = findViewById(R.id.kg);
        kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kg.setBackground(getDrawable(R.drawable.button_background_pressed_48dp));
                goTo(REQUEST_WEIGHT);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        blood_sugar.setBackground(getDrawable(R.drawable.button_background_48dp));
        blood_pressure.setBackground(getDrawable(R.drawable.button_background_48dp));
        kg.setBackground(getDrawable(R.drawable.button_background_48dp));
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
        } else if (id == REQUEST_WEIGHT) {
            Intent i = new Intent(this, Manual.class);
            i.putExtra("tag", id);
            i.putExtra("patientId", mPatientID);
            startActivity(i);
        }
    }

    private void back() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        back();
    }
}