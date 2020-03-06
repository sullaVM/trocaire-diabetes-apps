package com.example.doctor_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Consumer;

import com.example.doctor_app.data.requests.GetDoctorsPatientsRequest;
import com.example.doctor_app.data.responses.GetDoctorsPatientsResponse;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private int mDoctorID;
    protected ArrayList<Patient> patientDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Top bar (contains sign out menu item)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the doctor ID
        mDoctorID = getIntent().getIntExtra("tag", -1);
        Log.d("doctorID",Integer.toString(mDoctorID));

        // Get the patients of the doctor
        getPatients();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPatients();
    }

    // Currently using dummy patients
    private void getPatients() {

        // Get the patient IDs using the API
        GetDoctorsPatientsRequest patientsRequest = new GetDoctorsPatientsRequest(mDoctorID);
        patientsRequest.makeRequest(getBaseContext(), new Consumer<GetDoctorsPatientsResponse>() {
            @Override
            public void accept(GetDoctorsPatientsResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "GetDoctorsPatients", "Request succeeded");
                    success(response.patientIDs);
                } else {
                    Log.println(Log.INFO, "GetDoctorsPatients", "Request failed");
                    fail();
                }
            }
        });
    }

    private void success(Integer[] patientIDs) {
        patientDataSet = new ArrayList<>();
        for (int i = 0; i < patientIDs.length; i++) {
            Patient patient = new Patient(mDoctorID, "Test", Integer.toString(patientIDs[i]),
                    "123", 456, "1", "1", "0");
            patientDataSet.add(patient);
        }

        if (patientDataSet.size() == 0) {
            // Show an empty screen if no patients
            setContentView(R.layout.dashboard_empty_state);
        } else {
            // Else show the list of patients
            DashboardArrayAdapter adapter = new DashboardArrayAdapter(this,
                    R.layout.dashboard_list_item, patientDataSet);

            ListView listView = findViewById(R.id.listViewPatients);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(messageClickedHandler);
        }
    }

    private void fail() {
        setContentView(R.layout.dashboard_empty_state);
    }

    // Called by list item selection. Goes to patient info (i.e. the graphs for that patient)
    private AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), Info.class);
            intent.putExtra("info", patientDataSet.get(position));
            startActivity(intent);
        }
    };

    // Called by button to launch patient sign up
    public void addPatient(View view) {
        Intent intent = new Intent(getApplicationContext(), PatientSignUpDetails.class);
        intent.putExtra("doctorID", mDoctorID);
        startActivity(intent);
    }

    // Top bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);
        return true;
    }

    // Top bar menu items
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

    // Return to login activity (i.e. sign out)
    private void back() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
