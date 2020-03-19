package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

import com.example.doctor_app.data.requests.GetGraphingDataRequest;
import com.example.doctor_app.data.responses.BSLRecord;
import com.example.doctor_app.data.responses.GetGraphingDataResponse;
import com.example.doctor_app.data.responses.RBPRecord;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class InfoList extends AppCompatActivity {

    private int patientID;
    private String patientBSLUnit;
    private static String startDate = "1";
    private static String endDate = "2";
    private int readingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);

        String patientName = getIntent().getStringExtra("name");
        patientID = getIntent().getIntExtra("id", 0);
        patientBSLUnit = getIntent().getStringExtra("unit");
        readingType = getIntent().getIntExtra("type",0);

        // UI Components
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("Patient " + patientName);
        setSupportActionBar(toolbar);

        getData(startDate,endDate);
    }

    private void getData(String start, String end) {
        GetGraphingDataRequest graphRequest =
                new GetGraphingDataRequest(patientID, start,
                        end, patientBSLUnit);
        graphRequest.makeRequest(getBaseContext(), new Consumer<GetGraphingDataResponse>() {
            @Override
            public void accept(GetGraphingDataResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "GetGraphingData", "Request succeeded");
                    try {
                        success(response);
                    } catch (Exception e) {
                        Log.println(Log.INFO, "graph", "No data for range picked.");
                    }
                } else {
                    Log.println(Log.INFO, "GetGraphingData", "Request failed");
                }
            }
        });
    }

    private void success(GetGraphingDataResponse response) {

        //response.BSL = dummyData(); // For testing
        //response.RBP = dummyDataRBP(); // For testing

        // Fill the list
        if(readingType == 0) { // BSL type
            ArrayList<BSLRecord> readings = new ArrayList<>();
            for (int i = 0; i < response.BSL.length; i++) {
                readings.add(response.BSL[i]);
            }
            InfoListArrayAdapter adapter = new InfoListArrayAdapter(this,
                    R.layout.info_list_item, readings);

            ListView listView = findViewById(R.id.listViewPatients);
            listView.setAdapter(adapter);
        } else { // RBP type
            ArrayList<RBPRecord> readings = new ArrayList<>();
            for (int i = 0; i < response.RBP.length; i++) {
                readings.add(response.RBP[i]);
            }
            InfoListArrayAdapterRBP adapter = new InfoListArrayAdapterRBP(this,
                    R.layout.info_list_item, readings);

            ListView listView = findViewById(R.id.listViewPatients);
            listView.setAdapter(adapter);
        }
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

    public void updateDate(View view) {
        getData(startDate, endDate);
    }

    public void showDatePickerDialogStart(View v) {
        DialogFragment newFragment = new DatePickerFragmentStart();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragmentStart extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            Timestamp timestamp;

            // Set the start date
            startDate = year + "-" + (month + 1) + "-" + day + " " + "00:00:00.000"; // Months are zero-indexed

            timestamp = Timestamp.valueOf(startDate);
            startDate = timestamp.toString();

            // Set the end date
            LocalDate endLocal = LocalDate.of(year, month + 1, day).plusDays(5); // Months are zero indexed
            endDate = endLocal.getYear() + "-" + endLocal.getMonthValue() + "-" + endLocal.getDayOfMonth()
                    + " " + "00:00:00.000";

            timestamp = Timestamp.valueOf(endDate);
            endDate = timestamp.toString();
        }
    }

    // For test purposes
    private BSLRecord[] dummyData() {

        BSLRecord day0Morning = new BSLRecord();
        day0Morning.time = "2020-02-10 13:10:02.047";
        day0Morning.value = (float)6.4;

        BSLRecord day0Afternoon = new BSLRecord();
        day0Afternoon.time = "2020-02-10 18:10:02.047";
        day0Afternoon.value = (float)7;

        BSLRecord day1Morning = new BSLRecord();
        day1Morning.time = "2020-02-11 12:10:02.047";
        day1Morning.value = (float)4;

        BSLRecord day1Afternoon = new BSLRecord();
        day1Afternoon.time = "2020-02-11 17:10:02.047";
        day1Afternoon.value = (float)7;

        BSLRecord day2Morning = new BSLRecord();
        day2Morning.time = "2020-02-12 09:10:02.047";
        day2Morning.value = (float)11;

        BSLRecord day2Afternoon = new BSLRecord();
        day2Afternoon.time = "2020-02-12 09:10:02.047";
        day2Afternoon.value = (float)11;

        BSLRecord day2Evening = new BSLRecord();
        day2Evening.time = "2020-02-12 09:10:02.047";
        day2Evening.value = (float)1;

        BSLRecord[] result = {day0Morning, day0Afternoon, day1Morning, day1Afternoon, day2Morning,
                day2Afternoon, day2Evening};

        return result;
    }

    // For test purposes
    private RBPRecord[] dummyDataRBP() {

        RBPRecord day0Morning = new RBPRecord();
        day0Morning.time = "2020-02-10 13:10:02.047";
        day0Morning.systole = (float)100;
        day0Morning.diastole = (float)70;

        RBPRecord day0Afternoon = new RBPRecord();
        day0Afternoon.time = "2020-02-10 18:10:02.047";
        day0Afternoon.systole = (float)120;
        day0Afternoon.diastole = (float)85;

        RBPRecord day1Morning = new RBPRecord();
        day1Morning.time = "2020-02-11 12:10:02.047";
        day1Morning.systole = (float)99;
        day1Morning.diastole = (float)90;

        RBPRecord day1Afternoon = new RBPRecord();
        day1Afternoon.time = "2020-02-11 17:10:02.047";
        day1Afternoon.systole = (float)138;
        day1Afternoon.diastole = (float)100;

        RBPRecord day2Morning = new RBPRecord();
        day2Morning.time = "2020-02-12 09:10:02.047";
        day2Morning.systole = (float)160;
        day2Morning.diastole = (float)101;

        RBPRecord day2Afternoon = new RBPRecord();
        day2Afternoon.time = "2020-02-12 09:10:02.047";
        day2Afternoon.systole = (float)180;
        day2Afternoon.diastole = (float)85;

        RBPRecord day2Evening = new RBPRecord();
        day2Evening.time = "2020-02-12 09:10:02.047";
        day2Evening.systole = (float)1;
        day2Evening.diastole = (float)84;

        RBPRecord[] result = {day0Morning, day0Afternoon, day1Morning, day1Afternoon, day2Morning,
                day2Afternoon, day2Evening};

        return result;
    }
}
