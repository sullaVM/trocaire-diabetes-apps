<<<<<<< HEAD
package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Patient_Data_Enter extends AppCompatActivity {

    Button back;
    TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__data__enter);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        data = findViewById(R.id.textView);

        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", 1);
        showData(tag);
    }

    private void showData(int tag){
        String printData = "";
        File testFile = new File(this.getExternalFilesDir(null), "TextFile.txt");
        if (testFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                for(int i = 0; i < tag; i++) {
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
=======
package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Patient_Data_Enter extends AppCompatActivity {

    Button back;
    TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__data__enter);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        data = findViewById(R.id.textView);

        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", 1);
        showData(tag);
    }

    private void showData(int tag){
        String printData = "";
        File testFile = new File(this.getExternalFilesDir(null), "TextFile.txt");
        if (testFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                for(int i = 0; i < tag; i++) {
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
>>>>>>> 5324be2447ea23c6216b310d2e8fe8e279ab4945
