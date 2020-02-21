package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void forgotPassword(View view) {
        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(this);
        dialogDelete.setTitle("Forgot password?");
        dialogDelete.setMessage("An email wil be sent to reset.");
        dialogDelete.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        dialogDelete.setNegativeButton("CANCEL",null);
        dialogDelete.show();
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }
}
