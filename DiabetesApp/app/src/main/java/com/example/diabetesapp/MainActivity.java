package com.example.diabetesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.diabetesapp.data.requests.GetPatientProfileRequest;
import com.example.diabetesapp.data.responses.GetPatientProfileResponse;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    ArrayList<Integer> mPatientIDs = new ArrayList<>();

    private static final String TAG = "MainActivity";

    Button temp;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPatientIDs();

        temp = findViewById(R.id.button2);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempScreen();
            }
        });

        LinearLayout layout = findViewById(R.id.buttonLayout);

        LinearLayout linear1 = new LinearLayout(this);
        linear1.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < mPatientIDs.size(); i++) {
            if (i % 4 == 1) {
                linear1 = new LinearLayout(this);
                linear1.setOrientation(LinearLayout.HORIZONTAL);
            }
            if (linear1.getParent() != null) {
                ((ViewGroup) linear1.getParent()).removeView(linear1); // <- fix
            }
            layout.addView(linear1);

            final ImageButton b = new ImageButton(this);
            String photoPath = this.getFilesDir() + "/Image" + mPatientIDs.get(i) + ".jpg";
            File file = new File(photoPath);
            if (file.exists()) {
                b.setImageBitmap(getImage(i));
            } else {
                b.setImageDrawable(getResources().getDrawable(R.drawable.blank_icon));
                requestUserPhoto(mPatientIDs.get(i), new Response.Listener<GetPatientProfileResponse>() {
                    @Override
                    public void onResponse(GetPatientProfileResponse response) {
                        Log.println(Log.INFO, "GetPatientProfile", String.valueOf(response.success));
                        if (response.success) {
                            try {
                                URL url = new URL(response.photoDataUrl);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                b.setImageBitmap(myBitmap);
                            } catch (Exception e) {
                                b.setImageDrawable(getResources().getDrawable(R.drawable.blank_icon));
                            }
                        } else {
                            b.setImageDrawable(getResources().getDrawable(R.drawable.blank_icon));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.println(Log.INFO, "GetPatientProfile", "Request failed");
                    }
                });

            }
            b.setId(mPatientIDs.get(i));
            b.setTag(mPatientIDs.get(i));
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

    private void requestUserPhoto(int patientID, Response.Listener responseListener, Response.ErrorListener errorListener) {
        GetPatientProfileRequest patientProfileRequest = new GetPatientProfileRequest(patientID);
        patientProfileRequest.makeRequest(this, responseListener, errorListener);
    }

    //DELETE LATER
    private void tempScreen() {
        Intent intent = new Intent(this, DataEnter.class);
        startActivity(intent);
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

    private void getPatientIDs() {
        mPatientIDs.add(1);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
