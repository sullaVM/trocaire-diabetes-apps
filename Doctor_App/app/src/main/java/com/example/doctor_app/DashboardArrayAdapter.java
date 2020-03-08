package com.example.doctor_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        // Get patient
        Patient patient = patientList.get(position);

        // Fill UI components

        name.setText(patient.getName());

        Bitmap image;
        File file = new File(filesDir + "/Image" + patient.getPhotoDataUrl() + ".jpg");
        BitmapFactory.Options o = new BitmapFactory.Options();
        image = BitmapFactory.decodeFile(file.getAbsolutePath(),o);
        Bitmap thumbnail = extractThumbnail(image,80,80);
        profilePhoto.setImageBitmap(thumbnail);

        mobileNumber.setText(Integer.toString(patient.getNumber()));

        return v;
    }
}
