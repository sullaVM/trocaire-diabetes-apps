package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button pat_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pat_login = findViewById(R.id.button4);
        pat_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(3);
            }
        });
    }
    private void changeScreen(int num){
        Intent i = new Intent(this, Patient_Login.class);
        startActivity(i);
    }
}
