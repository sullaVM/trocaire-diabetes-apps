package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Patient_Data_Enter_Manual extends AppCompatActivity {

    Button enter;
    EditText data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__data__enter__manual);

        enter = findViewById(R.id.enter);
        data = findViewById(R.id.enterData);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterData();
            }
        });
    }

    private void enterData(){
        String value = data.getText().toString();
    }
}
