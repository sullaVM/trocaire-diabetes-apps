package com.example.doctor_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.doctor_app.data.responses.LogRecord;

import java.util.ArrayList;

public class LogListArrayAdapter extends ArrayAdapter<LogRecord> {

    private ArrayList<LogRecord> LogRecords;

    public LogListArrayAdapter(Context context,
                                int resource,
                                ArrayList<LogRecord> objects) {
        super(context, resource, objects);
        this.LogRecords = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.log_list_item, null);

        // Get UI components
        TextView date = v.findViewById(R.id.textView);
        TextView comment = v.findViewById(R.id.textView2);

        // Get reading
        LogRecord entry = LogRecords.get(position);

        // Fill components
        date.setText("Date: " + entry.time);
        comment.setText(entry.note);

        return v;
    }
}
