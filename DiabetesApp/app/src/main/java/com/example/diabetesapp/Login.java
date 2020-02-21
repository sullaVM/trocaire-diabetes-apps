package com.example.diabetesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    Button back;
    int currentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__login);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        currentNumber = findNumber();

        LinearLayout layout = findViewById(R.id.buttonLayout);

        LinearLayout linear1 = new LinearLayout(this);
        linear1.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 1; i <= currentNumber; i++) {
            if (i % 4 == 1) {
                linear1 = new LinearLayout(this);
                linear1.setOrientation(LinearLayout.HORIZONTAL);
            }
            if (linear1.getParent() != null) {
                ((ViewGroup) linear1.getParent()).removeView(linear1); // <- fix
            }
            layout.addView(linear1);

            ImageButton b = new ImageButton(this);
            b.setImageBitmap(getImage(i));
            b.setId(i);
            b.setTag(i);
            b.setPadding(8, 3, 8, 3);
            b.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            linear1.addView(b);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = Integer.parseInt(v.getTag().toString());
                    nextScreen(tag);
                }
            });
        }
    }

    private void nextScreen(int tag) {
        Intent intent = new Intent(this, InputPassword.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
    }

    private Bitmap getImage(int val) {
        String photoPath = this.getFilesDir() + "/Image" + val + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(photoPath, options);
    }

    private int findNumber() {
        int val = 1;
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
        return val;
    }

    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
