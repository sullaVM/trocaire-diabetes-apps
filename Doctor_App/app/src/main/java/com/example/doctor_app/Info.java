package com.example.doctor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import java.io.File;
import java.util.ArrayList;

public class Info extends AppCompatActivity {

    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        patient = getIntent().getParcelableExtra("info");

        TextView name = findViewById(R.id.textView2);
        name.setText(patient.getPatientName());

        graph();
    }

    private void graph() {

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

        LineDataSet set1 = new LineDataSet(data1, "Readings");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColors(Color.BLACK);
        set1.setHighLightColor(Color.BLACK);
        set1.setCircleColor(Color.GRAY);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setData(data);

        lineChart.setBackgroundColor(Color.WHITE);

        lineChart.getDescription().setText("Last 5 Days");

        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);


        final String[] labels = new String[] { "Day 1", "Day 2", "Day 3", "Day 4", "Day 5"};
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
                // Perform deletion;
                finish();
            }
        });
        dialogDelete.setNegativeButton("CANCEL",null);
        dialogDelete.show();
    }
}
