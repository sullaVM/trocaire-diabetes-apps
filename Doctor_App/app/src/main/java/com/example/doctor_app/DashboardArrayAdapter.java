package com.example.doctor_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.media.ThumbnailUtils.extractThumbnail;

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
        ImageView profilePhoto = v.findViewById(R.id.imageView);
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
        // Check if saved locally (older versions of the app saved locally)
        File file = new File(filesDir + "/Image" + patient.getPhotoDataUrl() + ".jpg");
        if(file.exists()) {
            BitmapFactory.Options o = new BitmapFactory.Options();
            image = BitmapFactory.decodeFile(file.getAbsolutePath(), o);
            Bitmap thumbnail = extractThumbnail(image,80,80);
            profilePhoto.setImageBitmap(thumbnail);
        } else {
            //If not saved locally, it is saved on the server
            image = stringToBitmap(patient.getPhotoDataUrl());
            Bitmap thumbnail = extractThumbnail(image,80,80);
            profilePhoto.setImageBitmap(thumbnail);
        }

        return v;
    }

    public Bitmap stringToBitmap(String string){
        try {
            byte[] encodeByte = Base64.decode(string,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
