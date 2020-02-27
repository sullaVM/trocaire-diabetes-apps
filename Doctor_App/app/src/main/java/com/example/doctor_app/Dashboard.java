package com.example.doctor_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private static int dataSetSize = 50;
    protected ArrayList<Patient> patientDataSet;
    private AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Patient patient = new Patient(patientDataSet.get(position).patientName,
                    patientDataSet.get(position).patientImage);

            Intent intent = new Intent(getApplicationContext(), Info.class);
            intent.putExtra("info", patient);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getPatients();

        if (patientDataSet.size() == 0) {
            setContentView(R.layout.dashboard_empty_state);
        } else {
            DashboardArrayAdapter adapter = new DashboardArrayAdapter(this,
                    R.layout.dashboard_list_item, patientDataSet);

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

    public void addPatient(View view) {
        Intent intent = new Intent(getApplicationContext(), PatientSignUpDetails.class);
        startActivity(intent);
    }

    private void getPatients() {
        patientDataSet = new ArrayList<>();
        for (int i = 0; i < dataSetSize; i++) {
            String patientName = "Patient " + i;
            int patientImage = R.drawable.blank_icon;
            Patient patient = new Patient(patientName, patientImage);
            patientDataSet.add(patient);
        }
    }
}
