package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class BloodPressureManual extends AppCompatActivity {

    Button enter;
    Button back;
    EditText data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__blood__pressure__manual);

        enter = findViewById(R.id.enter);
        data = findViewById(R.id.enterData);
        back = findViewById(R.id.back);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterData();
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

    private void enterData() {
        String value = data.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("pressure", value);
        setResult(DataEnter.RESULT_OK, resultIntent);
        finish();
    }
}
