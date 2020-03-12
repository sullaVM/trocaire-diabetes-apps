package com.example.doctor_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.DialogFragment;

import com.example.doctor_app.data.requests.GetGraphingDataRequest;
import com.example.doctor_app.data.responses.BSLRecord;
import com.example.doctor_app.data.responses.GetGraphingDataResponse;
import com.example.doctor_app.data.responses.RBPRecord;
import com.example.doctor_app.data.responses.WeightRecord;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;

public class Info extends AppCompatActivity {

    private static String startDate;
    private static String endDate;
    private static int type;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        patient = getIntent().getParcelableExtra("info");

        // UI Components
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("Patient " + patient.getName());
        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        type = 0;
                        getData(startDate, endDate, type);
                        break;
                    case 1:
                        type = 1;
                        getData(startDate, endDate,type);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
            });

        LineChart lineChart = findViewById(R.id.lineChart);
        lineChart.setNoDataText("No data for date picked.");
        Paint p = lineChart.getPaint(Chart.PAINT_INFO);
        p.setColor(Color.BLACK);

        // Set initial start date
        LocalDate localStart = LocalDate.now().minusDays(5);
        ZoneId systemZone = ZoneId.systemDefault(); // my timezone
        ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(Instant.now());
        Timestamp startTimeStamp = new Timestamp(localStart.atStartOfDay().toInstant(currentOffsetForMyZone).toEpochMilli());
        startDate = startTimeStamp.toString();

        // Set initial end date
        Timestamp endTimeStamp = new Timestamp(System.currentTimeMillis());
        endDate = endTimeStamp.toString();

        // Initially on the blood glucose tab, so display the blood glucose graph
        getData(startTimeStamp.toString(), endTimeStamp.toString(), 0);
    }

    private void getData(String start, String end, final int type) {
        GetGraphingDataRequest graphRequest =
                new GetGraphingDataRequest(patient.getPatientID(), start,
                        end, patient.getBslUnit());
        graphRequest.makeRequest(getBaseContext(), new Consumer<GetGraphingDataResponse>() {
            @Override
            public void accept(GetGraphingDataResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "GetGraphingData", "Request succeeded");
                    success(response, type);
                } else {
                    Log.println(Log.INFO, "GetGraphingData", "Request failed");
                    fail();
                }
            }
        });
    }

    private void success(GetGraphingDataResponse response, final int type) {

        try {
            switch(type) {
                case 0:
                    graphBloodGlucose(response.BSL);
                    break;
                case 1:
                    graphBloodPressure(response.RBP);
                    break;
            }
        } catch (Exception e) {
            Log.println(Log.INFO, "graph", "No data for range picked.");
        }
    }

    private void fail() {
        // Finish
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void graphBloodPressure(RBPRecord[] RBP) {
        TextView title = findViewById(R.id.textView6);
        title.setText("Daily Average Blood Pressure");

        TextView unit = findViewById(R.id.textView8);
        unit.setText("TODO");

        TextView daysAverage = findViewById(R.id.textView5);
        daysAverage.setText("Average BP Since " + "_" + " = " + "_");

        //TODO
    }

    private void graphBloodGlucose(BSLRecord[] BSL) {

        TextView title = findViewById(R.id.textView6);
        title.setText("Daily Average Blood Glucose");

        TextView unit = findViewById(R.id.textView8);
        unit.setText("mmol/L");

        TextView daysAverage = findViewById(R.id.textView5);
        daysAverage.setText("Average BG Since " + "_" + " = " + "_");

        String start = BSL[0].time.substring(5,7) + "/" + BSL[0].time.substring(8,10);
        int lastChange = 0;

        ArrayList<Entry> data1 = new ArrayList<Entry>();
        int entryIndex = 0;

        for(int i = 0; i < BSL.length; i++) {

            String day = BSL[i].time.substring(5,7) + "/" + BSL[i].time.substring(8,10);

            if(day.compareTo(start) != 0) {
                // Found a new day

                // Compute the average of the past day
                Float sum = (float)0;
                int count = 0;
                for(int j = lastChange; j < i; j++) {

                    sum = sum + BSL[j].value;
                    count++;
                }
                Float average = sum / count;

                Entry entry = new Entry(entryIndex,average);
                data1.add(entry);
                entryIndex++;

                lastChange = i;
                start = day;
            }
        }

        // Compute the average of the last day
        Float sum = (float)0;
        int count = 0;
        for(int j = lastChange; j < BSL.length; j++) {

            sum = sum + BSL[j].value;
            count++;
        }
        Float average = sum / count;
        Entry entry = new Entry(entryIndex,average);
        data1.add(entry);

        // Calculate the average of the days
        sum = (float)0;
        for(int i = 0; i < data1.size(); i++) {
            sum = sum + data1.get(i).getY();
        }
        average = sum / data1.size();
        daysAverage = findViewById(R.id.textView5);
        daysAverage.setText("Average BG Since " + start + " = " + average);

        // Graph
        LineDataSet set1 = new LineDataSet(data1, null);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColors(Color.BLACK);
        set1.setHighLightColor(Color.BLACK);
        set1.setCircleColor(Color.GRAY);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        LineChart lineChart = findViewById(R.id.lineChart);

        lineChart.setNoDataText("Loading data ...");
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.getLegend().setEnabled(false);

        lineChart.setData(data);

        lineChart.getDescription().setText("");

        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public void delete(View view) {
        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(this);
        dialogDelete.setTitle("Delete patient?");
        dialogDelete.setMessage("This will delete the current patient.");
        dialogDelete.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Perform deletion
                finish();
            }
        });
        dialogDelete.setNegativeButton("CANCEL", null);
        dialogDelete.show();
    }

    public void updateDate(View view) {
        getData(startDate, endDate, type);
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
}
