package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.net.Uri;
import java.util.LinkedList;
import java.util.List;

import android.media.MediaScannerConnection;
import android.util.Log;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.content.Context;
import android.content.ContextWrapper;
import java.io.FileOutputStream;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.graphics.BitmapFactory;

public class Patient_Signup extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Button back;
    Button enter;

    EditText enterName;

    ImageView imageView;

    Bitmap photo;
    int currentNumber;

    String clinic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__signup);

        back = findViewById(R.id.back);
        enter = findViewById(R.id.next);

        enterName = findViewById(R.id.editText);

        imageView = findViewById(R.id.imageView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        currentNumber = findNumber();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.clinic1:
                if (checked)
                    clinic = "Clinic1";
                    break;
            case R.id.clinic2:
                if (checked)
                    clinic = "Clinic2";
                    break;
        }
    }

    private int findNumber(){
        int val = 0;
        String[] text;
        File testFile = new File(this.getFilesDir(), "TextFile.txt");
        if (testFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    text = line.split(" ");
                    val = Integer.parseInt(text[0]);
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TextFile.txt file.");
            }
        }
        return val + 1;
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void enterData(){
        Intent intent = new Intent(this, Patient_SignUp_Password.class);
        String name = enterName.getText().toString();
        intent.putExtra("name", name);
        intent.putExtra("clinic", clinic);
        intent.putExtra("number", currentNumber);

            File photoFile = new File(this.getFilesDir(), "Image" + currentNumber + ".jpg");
            try {
                FileOutputStream out = new FileOutputStream(photoFile);
                Bitmap bitmap = photo.copy(photo.getConfig(), true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        startActivity(intent);
    }

    public void back(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                photo = imageBitmap;
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }
}