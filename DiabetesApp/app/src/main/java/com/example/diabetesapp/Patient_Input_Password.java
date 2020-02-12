package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Patient_Input_Password extends AppCompatActivity {

    ImageView pass1;
    ImageView pass2;
    ImageView pass3;
    ImageView pass4;
    ImageView pass5;
    ImageView pass6;
    ImageView pass7;
    ImageView pass8;
    ImageView pass9;

    Button reset;
    Button enter;

    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__input__password);

        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        pass3 = findViewById(R.id.pass3);
        pass4 = findViewById(R.id.pass4);
        pass5 = findViewById(R.id.pass5);
        pass6 = findViewById(R.id.pass6);
        pass7 = findViewById(R.id.pass7);
        pass8 = findViewById(R.id.pass8);
        pass9 = findViewById(R.id.pass9);

        reset = findViewById(R.id.reset);

        enter = findViewById(R.id.enter);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                int tag = intent.getIntExtra("tag", 1);
                checkPassword(tag);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass1.setImageResource(R.drawable.deselect);
                pass2.setImageResource(R.drawable.deselect);
                pass3.setImageResource(R.drawable.deselect);
                pass4.setImageResource(R.drawable.deselect);
                pass5.setImageResource(R.drawable.deselect);
                pass6.setImageResource(R.drawable.deselect);
                pass7.setImageResource(R.drawable.deselect);
                pass8.setImageResource(R.drawable.deselect);
                pass9.setImageResource(R.drawable.deselect);
                password = "";
            }
        });

        pass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass1.setImageResource(R.drawable.select);
                password += "1";
            }
        });

        pass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass2.setImageResource(R.drawable.select);
                password += "2";
            }
        });

        pass3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass3.setImageResource(R.drawable.select);
                password += "3";
            }
        });

        pass4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass4.setImageResource(R.drawable.select);
                password += "4";
            }
        });

        pass5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass5.setImageResource(R.drawable.select);
                password += "5";
            }
        });

        pass6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass6.setImageResource(R.drawable.select);
                password += "6";
            }
        });

        pass7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass7.setImageResource(R.drawable.select);
                password += "7";
            }
        });

        pass8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass8.setImageResource(R.drawable.select);
                password += "8";
            }
        });

        pass9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass9.setImageResource(R.drawable.select);
                password += "9";
            }
        });
    }

    void checkPassword(int tag){
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
                String realPassword = info[3];

                if(realPassword.equals(password)){
                    Intent intent = new Intent(this, Patient_Data_Enter.class);
                    intent.putExtra("tag", tag);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(this, Patient_Login.class);
                    startActivity(intent);
                }

            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TestFile.txt file.");
            }
        }
    }

}
