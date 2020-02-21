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

    ImageView check;
    ImageView cross;

    boolean pregnant;
    String sugar_data;
    String pressure_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__data__enter);

        pregnant = false;

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

        sugar_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sugar_takePhoto();
            }
        });
        sugar_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sugar_enterData();
            }
        });

        pressure_picture = findViewById(R.id.camera2);
        pressure_numbers = findViewById(R.id.numbers2);

        pressure_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressure_takePhoto();
            }
        });
        pressure_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressure_enterData();
            }
        });

        check = findViewById(R.id.check);
        cross = findViewById(R.id.cross);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pregnant = true;
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pregnant = false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        enter = findViewById(R.id.enter);
    }

    private void sugar_enterData() {
        Intent intent = new Intent(this, DataEnterManual.class);
        startActivityForResult(intent, 0);
    }

    private void sugar_takePhoto() {
        Intent intent = new Intent(this, DataEnterCamera.class);
        startActivityForResult(intent, 0);
    }

    private void pressure_enterData() {
        Intent intent = new Intent(this, BloodPressureManual.class);
        startActivityForResult(intent, 0);
    }

    private void pressure_takePhoto() {
        Intent intent = new Intent(this, BloodPressureCamera.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0): {
                if (resultCode == DataEnter.RESULT_OK) {
                    pressure_data = data.getStringExtra("pressure");
                    sugar_data = data.getStringExtra("sugar");
                    if (sugar_data != null) sugar.setText(sugar_data);
                    if (pressure_data != null) pressure.setText(pressure_data);
                }
            }
            break;
        }
    }

    private void enterData() {
        String h = height.getText().toString();
        String w = weight.getText().toString();
        String sugar = sugar_data;
        String pressure = pressure_data;
        boolean p = pregnant;
    }

    private void showData(int tag) {
        String printData = "";
        File testFile = new File(this.getFilesDir(), "TextFile.txt");
        if (testFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                for (int i = 1; i < tag; i++) {
                    line = reader.readLine();
                }
                line = reader.readLine();
                String[] info = line.split(" ");
                printData = "First Name: " + info[1] + "\nLast Name: " + info[2];
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TestFile.txt file.");
            }
        }
        //data.setText(printData);
    }

    private void back() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}