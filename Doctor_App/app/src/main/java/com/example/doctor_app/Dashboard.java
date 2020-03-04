package com.example.doctor_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private static int dataSetSize = 50;
    protected ArrayList<Patient> patientDataSet;
    private int mDoctorID;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDoctorID = getIntent().getIntExtra("tag", -1);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                back();
                return true;
            default:
                return false;
        }
    }

    private void back() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
