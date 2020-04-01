package com.example.doctor_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import static android.media.ThumbnailUtils.extractThumbnail;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doctor_app.data.requests.CreatePatientRequest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;

public class PatientSignUpDetails extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Input fields
    private EditText firstName;
    private EditText lastName;
    private EditText mobileNumber;
    private EditText height;
    private EditText weight;
    private CheckBox pregnant;
    private ImageView profilePhoto;

    // Submit button
    private Button next;

    // Photo activity result variable
    private Bitmap photo;
    // Name of file to save photo to
    private String photoDataUrl;

    private Integer doctorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        // Get the ID of the currently signed in doctor
        Intent intent = getIntent();
        doctorID = intent.getIntExtra("doctorID", 0);

        // Get inputs
        firstName = findViewById(R.id.editText);
        lastName = findViewById(R.id.editText2);
        mobileNumber = findViewById(R.id.editText3);
        height = findViewById(R.id.editText4);
        weight = findViewById(R.id.editText5);
        pregnant = findViewById(R.id.checkBox);
        profilePhoto = findViewById(R.id.imageView);

        // Get next button
        next = findViewById(R.id.next);

        // Get file name to save photo to
        photoDataUrl = Integer.toString(findNumber());

        // Set listeners
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterData();
            }
        });
    }

    private int findNumber() {
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
                    Log.println(Log.INFO, "value", Integer.toString(val));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                photo = imageBitmap;
                profilePhoto.setImageBitmap(imageBitmap);
            }
        }
    }

    private void enterData() {
        // Get the inputs
        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        int pNumber = Integer.valueOf(mobileNumber.getText().toString());
        String h = height.getText().toString();
        String w = weight.getText().toString();
        int p = pregnant.isChecked() ? CreatePatientRequest.PREGNANT : CreatePatientRequest.NOT_PREGNANT;

        // Put into the intent
        Intent intent = new Intent(this, PatientSignUpPass.class);
        intent.putExtra("firstName", fName);
        intent.putExtra("lastName", lName);
        intent.putExtra("mobileNumber", pNumber);
        intent.putExtra("height", h);
        intent.putExtra("weight", w);
        intent.putExtra("pregnant", p);
        intent.putExtra("doctorID", doctorID);

        if (photo != null) {
            Bitmap bitmap = extractThumbnail(photo,5,5);
            photoDataUrl = bitmapToString(bitmap);
            intent.putExtra("photoDataUrl", photoDataUrl);
        }

        // Start the intent (goes to the next stage of patient sign up: setting the password)
        startActivity(intent);
    }

    public String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream os = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,90, os);
        byte[] bytes = os.toByteArray();
        String result = Base64.encodeToString(bytes, Base64.DEFAULT);
        return result;
    }
}