package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.StoreBSLRequest;
import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.responses.StoreBSLResponse;
import com.example.diabetesapp.data.responses.StoreRBPResponse;

import java.sql.Timestamp;

public class InputPressureSugar extends AppCompatActivity {

    static final int REQUEST_SUGAR = 0;
    static final int REQUEST_PRESSURE = 1;

    EditText dataBox1, dataBox2;
    ImageView back, done, camera;
    String input;
    String input2;
    int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pressure_sugar);

        mPatientID = getIntent().getIntExtra("patientId", 0);

        dataBox1 = findViewById(R.id.data);
        dataBox2 = findViewById(R.id.data2);
        dataBox1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dataBox2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getIntExtra("tag", 0) == REQUEST_SUGAR) saveData(REQUEST_SUGAR);
                else saveData(REQUEST_PRESSURE);
            }
        });

        camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                int id = i.getIntExtra("tag", 0);
                if (id == 0) OCRNormal(id);
                else OCRSevenDigit(id);
            }
        });
    }

    private void OCRNormal(int code) {
        Intent intent = new Intent(this, CameraSugar.class);
        startActivityForResult(intent, code);
    }

    private void OCRSevenDigit(int code) {
        Intent intent = new Intent(this, Camera.class);
        startActivityForResult(intent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == InputPressureSugar.RESULT_OK) {
            input = data.getStringExtra("input1");
            input2 = data.getStringExtra("input2");
            if (input != null) dataBox1.setText(input);
            if (input2 != null) dataBox2.setText(input2);
        }
    }

    private void back() {
        finish();
    }

    private void saveData(int request) {
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        if (request == REQUEST_SUGAR) {

            try {
                StoreBSLRequest storeBSLRequest = new StoreBSLRequest(mPatientID, timestamp, Float.parseFloat(input), null);
                storeBSLRequest.makeRequest(this, new Consumer<StoreBSLResponse>() {
                    @Override
                    public void accept(StoreBSLResponse storeBSLResponse) {
                        finish();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT);
            }
        } else if (request == REQUEST_PRESSURE) {
            try {
                StoreRBPRequest storeRBPRequest = new StoreRBPRequest(mPatientID, timestamp, Float.parseFloat(input), Float.parseFloat(input));
                storeRBPRequest.makeRequest(this, new Consumer<StoreRBPResponse>() {
                    @Override
                    public void accept(StoreRBPResponse storeRBPResponse) {
                        finish();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT);
            }
        }
    }
}
