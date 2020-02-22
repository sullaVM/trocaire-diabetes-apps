package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.util.Log;
import android.provider.MediaStore;
import android.graphics.Bitmap;

import java.io.FileOutputStream;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;

public class PatientSignUpDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Button enter;

    EditText enterName;

    ImageView imageView;

    Bitmap photo;
    int currentNumber;

    String clinic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        enter = findViewById(R.id.next);

        enterName = findViewById(R.id.editText);

        imageView = findViewById(R.id.imageView);

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

        // Spinner element
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        clinic = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        Intent intent = new Intent(this, PatientSignUpPass.class);
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