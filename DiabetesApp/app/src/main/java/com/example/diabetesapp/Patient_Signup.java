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
    Button load;
    Button cameraB;
    EditText enterName;
    TextView loadedText;
    ImageView imageView;
    Bitmap photo;
    int currentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__signup);

        back = findViewById(R.id.back);
        enter = findViewById(R.id.enter);
        load = findViewById(R.id.load);
        cameraB = findViewById(R.id.cameraB);
        enterName = findViewById(R.id.editText);
        loadedText = findViewById(R.id.textView);
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

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        cameraB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        currentNumber = findNumber();
    }

    private int findNumber(){
        int val = 1;
        String[] text;
        File testFile = new File(this.getExternalFilesDir(null), "TextFile.txt");
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
        return val;
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void loadData(){
        String textFromFile = "";
        // Gets the file from the primary external storage space of the
        // current application.
        File testFile = new File(this.getExternalFilesDir(null), "TextFile.txt");
        if (testFile != null) {
            // Reads the data from the file
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile += line.toString();
                    textFromFile += "\n";
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TestFile.txt file.");
            }
        }
        loadedText.setText(textFromFile);

        String photoPath = this.getExternalFilesDir(null) + "/TestImage.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
    }

    public void enterData(){
        String name = enterName.getText().toString();
        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exist, it is created.
            File textFile = new File(this.getExternalFilesDir(null), "TextFile.txt");
            if (!textFile.exists())
                textFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true /*append*/));
            writer.write(currentNumber + " " + name + "\n");
            writer.close();

            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(this,
                    new String[]{textFile.toString()},
                    null,
                    null);

            File photoFile = new File(this.getExternalFilesDir(null), "Image" + currentNumber + ".jpg");
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
