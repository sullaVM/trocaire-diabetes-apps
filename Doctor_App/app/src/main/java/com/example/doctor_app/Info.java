package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.doctor_app.data.requests.GetGraphingDataRequest;
import com.example.doctor_app.data.responses.BSLRecord;
import com.example.doctor_app.data.responses.GetGraphingDataResponse;
import com.example.doctor_app.data.responses.RBPRecord;
import com.example.doctor_app.data.responses.WeightRecord;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Info extends AppCompatActivity {

    private Patient patient;

    private static String startDate;
    private static String endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        patient = getIntent().getParcelableExtra("info");

        // Get UI components
        TextView name = findViewById(R.id.textView2);
        name.setText(patient.getName());

        // UI styling
        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setNoDataText("Loading data ...");
        Paint p = lineChart.getPaint(Chart.PAINT_INFO);
        p.setColor(Color.BLACK);

        // Set initial start date
        LocalDate localStart =  LocalDate.now().minusDays(5);
        startDate = localStart.getYear() + "-" + localStart.getMonthValue() + "-" + localStart.getDayOfMonth()
                + " " + "00:00:00.000";

        Timestamp timestamp = Timestamp.valueOf(startDate);
        startDate = timestamp.toString();

        // Set initial end date
        LocalDate localEnd =  LocalDate.now();
        endDate = localEnd.getYear() + "-" + localEnd.getMonthValue() + "-" + localEnd.getDayOfMonth()
                + " " + "00:00:00.000";

        timestamp = Timestamp.valueOf(endDate);
        endDate = timestamp.toString();

        getData(startDate,endDate);
    }

    private void getData(String start, String end) {
        GetGraphingDataRequest graphRequest =
                new GetGraphingDataRequest(patient.getPatientID(), start,
                        end,patient.getBslUnit());
        graphRequest.makeRequest(getBaseContext(), new Consumer<GetGraphingDataResponse>() {
            @Override
            public void accept(GetGraphingDataResponse response) {
                if (response != null && response.success) {
                    Log.println(Log.INFO, "GetGraphingData", "Request succeeded");
                    success(response);
                } else {
                    Log.println(Log.INFO, "GetGraphingData", "Request failed");
                    fail();
                }
            }
        });
    }

    private void success(GetGraphingDataResponse response) {

        try {
            // Test info
            int i = 0;
            Log.println(Log.INFO, "RBPTime", response.RBP[i].time);
            Log.println(Log.INFO, "RBPSystole", Float.toString(response.RBP[i].systole));
            Log.println(Log.INFO, "RBPDiastole", Float.toString(response.RBP[i].diastole));

            Log.println(Log.INFO, "BSLTime", response.BSL[i].time);
            Log.println(Log.INFO, "BSLValue", Float.toString(response.BSL[i].value));

            Log.println(Log.INFO, "WeightTime", response.Weight[i].time);
            Log.println(Log.INFO, "WeightValue", Float.toString(response.Weight[i].value));
        } catch (Exception e) {
            Log.println(Log.INFO, "graph", "No graph data for time picked");
        }

        // Update the UI with the graph data
        graph(response.RBP, response.BSL, response.Weight);
    }

    private void fail() {
        // Finish
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void graph(RBPRecord[] RBP, BSLRecord[] BSL, WeightRecord[] weight) {

        ArrayList<Entry> data1 = new ArrayList<Entry>();
        Entry one = new Entry(0f, 10f);
        data1.add(one);
        Entry two = new Entry(1f, 8f);
        data1.add(two);
        Entry three = new Entry(2f, 9f);
        data1.add(three);
        Entry four = new Entry(3f, 2f);
        data1.add(four);
        Entry five = new Entry(4f, 4f);
        data1.add(five);

        LineDataSet set1 = new LineDataSet(data1,null);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColors(Color.BLACK);
        set1.setHighLightColor(Color.BLACK);
        set1.setCircleColor(Color.GRAY);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);

        lineChart.setNoDataText("Loading data ...");
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.getLegend().setEnabled(false);

        lineChart.setData(data);

        lineChart.getDescription().setText("");

        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);


        String startLabel = startDate.substring(5,7) + "/" + startDate.substring(8,10);
        final String[] labels = new String[] { startLabel, "Day 2", "Day 3", "Day 4", "Day 5"};
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return labels[(int)value];
            }
        };
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

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
        dialogDelete.setNegativeButton("CANCEL",null);
        dialogDelete.show();
    }

    public void updateDate(View view) {
        getData(startDate,endDate);
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
            startDate = year + "-" + (month+1) + "-" + day + " " + "00:00:00.000"; // Months are zero-indexed

            timestamp = Timestamp.valueOf(startDate);
            startDate = timestamp.toString();

            // Set the end date
            LocalDate endLocal = LocalDate.of(year, month+1, day).plusDays(5); // Months are zero indexed
            endDate = endLocal.getYear() + "-" + endLocal.getMonthValue() + "-" + endLocal.getDayOfMonth()
                            + " " + "00:00:00.000";

            timestamp = Timestamp.valueOf(endDate);
            endDate = timestamp.toString();
        }
    }

    public void showDatePickerDialogStart(View v) {
        DialogFragment newFragment = new DatePickerFragmentStart();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
