package com.example.diabetesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.GetPatientIDRequest;
import com.example.diabetesapp.data.requests.StoreBSLRequest;
import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.requests.StoreWeightRequest;
import com.example.diabetesapp.data.responses.GetPatientIDResponse;
import com.example.diabetesapp.data.responses.StoreBSLResponse;
import com.example.diabetesapp.data.responses.StoreRBPResponse;
import com.example.diabetesapp.data.responses.StoreWeightResponse;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String filename = "StoredData.txt";

    ImageButton done, submit;
    TextView amount;
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

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        amount = findViewById(R.id.amount);

        done = findViewById(R.id.button2);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done.setBackground(getDrawable(R.drawable.button_background_pressed_48dp));
                checkLogin();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        done.setBackground(getDrawable(R.drawable.button_background_48dp));
    }

    private void submitData(){
        File testFile = new File(this.getFilesDir(), filename);
        if (testFile != null) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] text = line.split(" ");
                    if(text[0].equals("BSL")) storeBSL(Integer.parseInt(text[1]), Float.parseFloat(text[2]));
                    else if(text[0].equals("RBP")) storeRBP(Integer.parseInt(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]));
                    else if(text[0].equals("W")) storeWeight(Integer.parseInt(text[1]), Float.parseFloat(text[2]));
                }
                testFile.delete();
                amount.setText("");
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TextFile.txt file.");
            }
        }
    }

    private void storeBSL(int mPatientID, float data){
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        try {
            StoreBSLRequest storeBSLRequest = new StoreBSLRequest(mPatientID, timestamp, data, null);
            storeBSLRequest.makeRequest(this, new Consumer<StoreBSLResponse>() {
                @Override
                public void accept(StoreBSLResponse storeBSLResponse) {
                    Log.d("Upload", "BSL request submitted successfully");
                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeRBP(int mPatientID, float data1, float data2){
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();try {
            StoreRBPRequest storeRBPRequest = new StoreRBPRequest(mPatientID, timestamp, data1, data2);
            storeRBPRequest.makeRequest(this, new Consumer<StoreRBPResponse>() {
                @Override
                public void accept(StoreRBPResponse storeRBPResponse) {
                    Log.d("Upload", "RBP request submitted successfully");
                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeWeight(int mPatientID, float data){
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        try {
            StoreWeightRequest storeWeightRequest = new StoreWeightRequest(mPatientID, timestamp, data);
            storeWeightRequest.makeRequest(this, new Consumer<StoreWeightResponse>() {
                @Override
                public void accept(StoreWeightResponse storeWeightResponse) {
                    Log.d("Upload", "Weight request submitted successfully");
                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Trouble Parsing Float", Toast.LENGTH_SHORT).show();
        }
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
        done.setBackground(getDrawable(R.drawable.button_background_48dp));

        amount.setText(Integer.toString(findNumber()));

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) { /*we are connected to a network*/ }
        else {
            submit.setImageResource(R.drawable.baseline_wifi_off_black_48dp);
        }

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private int findNumber(){
        int val = 0;
        File testFile = new File(this.getFilesDir(), filename);
        if (testFile != null && testFile.length()>0) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    val++;
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TextFile.txt file.");
            }
        }
        return val;
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                submit.setImageResource(R.drawable.baseline_wifi_black_48dp);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {submitData();}
                });
            }else{
                submit.setImageResource(R.drawable.baseline_wifi_off_black_48dp);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {}
                });
            }
        }
    };

}
