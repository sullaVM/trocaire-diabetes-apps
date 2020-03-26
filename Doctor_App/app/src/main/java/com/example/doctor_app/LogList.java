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

import com.example.doctor_app.data.requests.GetPatientLogsRequest;
import com.example.doctor_app.data.responses.GetPatientLogsResponse;
import com.example.doctor_app.data.responses.LogRecord;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class LogList extends AppCompatActivity {

    private String patientName;
    private int patientID;
    private static String startDate = ""; // Set by the date picker
    private static String endDate = ""; // Set by the date picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        patientName = getIntent().getStringExtra("name");
        patientID = getIntent().getIntExtra("id", 0);

        // UI Components
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("Patient " + patientName);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void addComment(View view) {
        Intent intent = new Intent(getApplicationContext(), PatientComment.class);
        intent.putExtra("name", patientName);
        intent.putExtra("id", patientID);
        startActivity(intent);
    }

    private void getData(String start, String end) {
        GetPatientLogsRequest logRequest = new GetPatientLogsRequest(patientID, start, end);

        logRequest.makeRequest(getBaseContext(), new Consumer<GetPatientLogsResponse>() {
            @Override
            public void accept(GetPatientLogsResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "GetPatientLog", "Request succeeded");
                    try {
                        success(response);
                    } catch (Exception e) {
                       Log.println(Log.INFO, "GetPatientLog", "No entries for range picked.");
                   }
                } else {
                    Log.println(Log.INFO, "GetPatientLog", "Request failed");
                }
            }
        });
    }

    private void success(GetPatientLogsResponse response) {
        ArrayList<LogRecord> comments = new ArrayList<>();
        for (int i = 0; i < response.logs.length; i++) {
            comments.add(response.logs[i]);
        }
        LogListArrayAdapter adapter = new LogListArrayAdapter(this,
                R.layout.log_list_item, comments);

        ListView listView = findViewById(R.id.listViewComments);
        listView.setAdapter(adapter);
    }

    public void updateDate(View view) {
        getData(startDate, endDate);
    }

    public void showDatePickerDialogStart(View v) {
        DialogFragment newFragment = new LogList.DatePickerFragmentStart();
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
            Timestamp endTimeStamp = new Timestamp(System.currentTimeMillis());
            endDate = endTimeStamp.toString();
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
}
