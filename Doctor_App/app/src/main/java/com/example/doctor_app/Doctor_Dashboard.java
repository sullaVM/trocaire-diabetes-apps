package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.media.ThumbnailUtils.extractThumbnail;

public class Doctor_Dashboard extends AppCompatActivity {
    
    private static int datasetSize = 50;
    protected ArrayList<Patient> patientDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor__dashboard);

        getPatients();

        if(patientDataset.size() == 0) {
            setContentView(R.layout.doctor_dashboard_empty_state);
        } else {
            Doctor_Dashboard_Array_Adapter adapter = new Doctor_Dashboard_Array_Adapter(this,
                    R.layout.doctor_dashboard_list_item, patientDataset);

            ListView listView = findViewById(R.id.listViewPatients);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(messageClickedHandler);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPatients();
    }

    private void getPatients() {
        patientDataset = new ArrayList<>();
        for (int i = 0; i < datasetSize; i++) {
            String patientName = "Patient " + i;
            int patientImage = R.drawable.blank_icon;
            Patient patient = new Patient(patientName, patientImage);
            patientDataset.add(patient);
        }
    }

    private AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Patient patient = new Patient(patientDataset.get(position).patientName,
                    patientDataset.get(position).patientImage);

            Intent intent = new Intent(getApplicationContext(), Info.class);
            intent.putExtra("info", patient);
            startActivity(intent);
        }
    };
}
