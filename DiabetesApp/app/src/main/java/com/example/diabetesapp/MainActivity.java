package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button doc_signup;
    Button doc_login;
    Button pat_signup;
    Button pat_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doc_signup = findViewById(R.id.button);
        doc_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(0);
            }
        });
        doc_login = findViewById(R.id.button2);
        doc_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(1);
            }
        });
        pat_signup = findViewById(R.id.button3);
        pat_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(2);
            }
        });
        pat_login = findViewById(R.id.button4);
        pat_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(3);
            }
        });
    }
    private void changeScreen(int num){
        Intent i = new Intent();
        if(num==0) i = new Intent(this, Doctor_Signup.class);
        if(num==1) i = new Intent(this, Doctor_Login.class);
        if(num==2) i = new Intent(this, Patient_Signup.class);
        if(num==3) i = new Intent(this, Patient_Login.class);

        startActivity(i);
    }
}
