package com.example.doctor_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.util.Consumer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DashboardArrayAdapter extends ArrayAdapter<Patient> {
    private ArrayList<Patient> patientList;
    private File filesDir;

    public DashboardArrayAdapter(Context context,
                                 int resource,
                                 ArrayList<Patient> objects, File filesDir) {
        super(context, resource, objects);
        this.patientList = objects;
        this.filesDir = filesDir;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.dashboard_list_item, null);

        // Get UI components
        TextView name = v.findViewById(R.id.textView);
        final ImageView profilePhoto = v.findViewById(R.id.imageView);
        TextView mobileNumber = v.findViewById(R.id.textView2);
        ImageView phone = v.findViewById(R.id.phone);

        // Get patient
        Patient patient = patientList.get(position);

        // Fill UI components

        if (patient.getToCall()) {
            phone.setVisibility(View.VISIBLE);
        } else {
            phone.setVisibility(View.INVISIBLE);
        }

        name.setText(patient.getName());
        mobileNumber.setText(Integer.toString(patient.getNumber()));

        Bitmap image;


        File file = new File(filesDir + "/Image" + patient.getPatientID() + ".jpg");
        if (file.exists()) {
            image = BitmapFactory.decodeFile(file.getAbsolutePath());
            profilePhoto.setImageBitmap(image);
        } else {
            try {
                DownloadTask downloadTask = new DownloadTask(patient, profilePhoto, new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        profilePhoto.setImageBitmap(bitmap);
                    }
                });
                URL url = new URL(patient.getPhotoDataUrl());
                downloadTask.execute(url);
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        return v;
    }

    private class DownloadTask extends AsyncTask<URL, String, String> {

        Patient patient;
        ImageView imageView;
        Consumer<Bitmap> callback;

        private DownloadTask(Patient patient, ImageView imageView, final Consumer<Bitmap> callback) {
            this.patient = patient;
            this.imageView = imageView;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(URL... f_url) {
            int count;
            try {
                URLConnection connection = f_url[0].openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(f_url[0].openStream(),
                        8192);

                ByteArrayOutputStream output = new ByteArrayOutputStream();

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                byte[] byteArray = output.toByteArray();
                byte[] bytes = Base64.decode(byteArray, Base64.NO_WRAP);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                callback.accept(bitmap);

                try {
                    File file = new File(filesDir + "/Image" + patient.getPatientID() + ".jpg");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }
    }
}
