package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Patient_Data_Enter extends AppCompatActivity {

    Button back;
    TextView data;

    ImageView picture;
    ImageView numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__data__enter);

        picture = findViewById(R.id.camera);
        numbers = findViewById(R.id.numbers);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                int tag = intent.getIntExtra("tag", 1);
                takePhoto(tag);
            }
        });

        numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                int tag = intent.getIntExtra("tag", 1);
                enterData(tag);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", 1);
        //showData(tag);
    }

    private void enterData(int tag){
        Intent intent = new Intent(this, Patient_Data_Enter_Manual.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
    }

    private void takePhoto(int tag){
        Intent intent = new Intent(this, Patient_Data_Enter_Camera.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
    }

    private void showData(int tag){
        String printData = "";
        File testFile = new File(this.getFilesDir(), "TextFile.txt");
        if (testFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                for(int i = 1; i < tag; i++) {
                    line = reader.readLine();
                }
                line = reader.readLine();
                String[] info = line.split(" ");
                printData = "First Name: " + info[1] + "\nLast Name: " + info[2];
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TestFile.txt file.");
            }
        }
        data.setText(printData);
    }

    private void back(){
        Intent intent = new Intent(this, Patient_Login.class);
        startActivity(intent);
    }
}