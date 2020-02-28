package com.example.diabetesapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;

public class Manual extends AppCompatActivity {

    ImageView enter, back;
    EditText data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        enter = findViewById(R.id.enter);
        data = findViewById(R.id.enterData);
        data.setInputType(InputType.TYPE_CLASS_NUMBER);
        back = findViewById(R.id.back);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String type = intent.getStringExtra("type");
                enterData(type);
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

    private void enterData(String type) {
        String value = data.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(type, value);
        setResult(DataEnter.RESULT_OK, resultIntent);
        finish();
    }
}
