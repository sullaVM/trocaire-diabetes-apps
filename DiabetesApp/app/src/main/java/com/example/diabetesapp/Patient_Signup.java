package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.net.Uri;
import java.util.LinkedList;

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
    Button reset;

    EditText enterName;

    ImageView imageView;

    ImageView pass1;
    ImageView pass2;
    ImageView pass3;
    ImageView pass4;
    ImageView pass5;
    ImageView pass6;
    ImageView pass7;
    ImageView pass8;
    ImageView pass9;

    Bitmap photo;
    int currentNumber;

    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__signup);

        back = findViewById(R.id.back);
        enter = findViewById(R.id.enter);
        reset = findViewById(R.id.reset);

        enterName = findViewById(R.id.editText);

        imageView = findViewById(R.id.imageView);
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        pass3 = findViewById(R.id.pass3);
        pass4 = findViewById(R.id.pass4);
        pass5 = findViewById(R.id.pass5);
        pass6 = findViewById(R.id.pass6);
        pass7 = findViewById(R.id.pass7);
        pass8 = findViewById(R.id.pass8);
        pass9 = findViewById(R.id.pass9);

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

        currentNumber = findNumber();
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
        String name = enterName.getText().toString();
        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exist, it is created.
            File textFile = new File(this.getFilesDir(), "TextFile.txt");
            if (!textFile.exists())
                textFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true /*append*/));

            writer.write(currentNumber + " " + name + " " + password + "\n");
            writer.close();

            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(this,
                    new String[]{textFile.toString()},
                    null,
                    null);

            File photoFile = new File(this.getFilesDir(), "Image" + currentNumber + ".jpg");
            if (!textFile.exists())
                textFile.createNewFile();
            try {
                FileOutputStream out = new FileOutputStream(photoFile);
                Bitmap bitmap = photo.copy(photo.getConfig(), true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write data.");
        }
        currentNumber++;
    }

    public void back(){
        Intent intent = new Intent(this, MainActivity.class);
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