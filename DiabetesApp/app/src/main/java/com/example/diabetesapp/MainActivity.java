package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.GetPatientIDRequest;
import com.example.diabetesapp.data.responses.GetPatientIDResponse;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageButton done;
    EditText username;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
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

        username = findViewById(R.id.username);

        done = findViewById(R.id.button2);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {
        GetPatientIDRequest getPatientIDRequest = new GetPatientIDRequest(username.getText().toString());
        getPatientIDRequest.makeRequest(getBaseContext(), new Consumer<GetPatientIDResponse>() {
            @Override
            public void accept(GetPatientIDResponse getPatientIDResponse) {
                if (getPatientIDResponse.success != null && getPatientIDResponse.success) {
                    Intent intent = new Intent(getBaseContext(), InputPassword.class);

                    intent.putExtra("tag", getPatientIDResponse.patientID);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    /*
    private void requestUserPhoto(int patientID, Response.Listener responseListener, Response.ErrorListener errorListener) {
        GetPatientProfileRequest patientProfileRequest = new GetPatientProfileRequest(patientID);
        patientProfileRequest.makeRequest(this, responseListener, errorListener);
    }

    private Bitmap getImage(int val) {
        String photoPath = this.getFilesDir() + "/Image" + val + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(photoPath, options);
    }
    */
}
