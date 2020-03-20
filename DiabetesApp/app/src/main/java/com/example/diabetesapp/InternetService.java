package com.example.diabetesapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.example.diabetesapp.data.requests.StoreBSLRequest;
import com.example.diabetesapp.data.requests.StoreRBPRequest;
import com.example.diabetesapp.data.requests.StoreWeightRequest;
import com.example.diabetesapp.data.responses.StoreBSLResponse;
import com.example.diabetesapp.data.responses.StoreRBPResponse;
import com.example.diabetesapp.data.responses.StoreWeightResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;

public class InternetService extends Service {

    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String filename = "StoredData.txt";

    public InternetService() {
        super();
        Log.i("HERE", "here I am!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    //check internet connection
                    if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                        if (context != null) {
                            boolean show = false;
                            if (ConnectionHelper.lastNoConnectionTs == -1) {//first time
                                show = true;
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            } else {
                                if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                    show = true;
                                    ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                }
                            }

                            if (show && ConnectionHelper.isOnline) {
                                ConnectionHelper.isOnline = false;
                                Log.i("NETWORK123", "Connection lost");
                                //manager.cancelAll();
                            }
                        }
                    } else {
                        Log.i("NETWORK123", "Connected");
                        submitData();

                        ConnectionHelper.isOnline = true;
                    }
                }
            }
        };
        registerReceiver(receiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
    }

    private void submitData() {
        File testFile = new File(this.getFilesDir(), filename);
        if (testFile != null) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] text = line.split(" ");
                    if (text[0].equals("BSL"))
                        storeBSL(Integer.parseInt(text[1]), Float.parseFloat(text[2]));
                    else if (text[0].equals("RBP"))
                        storeRBP(Integer.parseInt(text[1]), Float.parseFloat(text[2]), Float.parseFloat(text[3]));
                    else if (text[0].equals("W"))
                        storeWeight(Integer.parseInt(text[1]), Float.parseFloat(text[2]));
                }
                testFile.delete();
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TextFile.txt file.");
            }
        }
    }

    private void storeBSL(int mPatientID, float data) {
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

    private void storeRBP(int mPatientID, float data1, float data2) {
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        try {
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

    private void storeWeight(int mPatientID, float data) {
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
}