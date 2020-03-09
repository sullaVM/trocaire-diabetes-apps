package com.example.diabetesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ImageView;

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

    ImageView done;
    EditText username;

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

        username = findViewById(R.id.username);

        done = findViewById(R.id.button2);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextScreen();
            }
        });
    }

    private void requestUserPhoto(int patientID, Response.Listener responseListener, Response.ErrorListener errorListener) {
        GetPatientProfileRequest patientProfileRequest = new GetPatientProfileRequest(patientID);
        patientProfileRequest.makeRequest(this, responseListener, errorListener);
    }

    private void nextScreen() {
        String user = username.getText().toString();
        //GET PASSWORD FROM USER
        Intent intent = new Intent(this, InputPassword.class);

        //ENTER PASSWORD HERE INSTEAD OF USER
        intent.putExtra("tag", user);
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
