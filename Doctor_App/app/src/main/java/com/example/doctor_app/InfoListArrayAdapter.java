package com.example.doctor_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.doctor_app.data.responses.BSLRecord;

import java.util.ArrayList;

public class InfoListArrayAdapter extends ArrayAdapter<BSLRecord> {
    private ArrayList<BSLRecord> BSLRecords;
    private final String TYPE = "BSL Reading";

    public InfoListArrayAdapter(Context context,
                                 int resource,
                                 ArrayList<BSLRecord> objects) {
        super(context, resource, objects);
        this.BSLRecords = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.info_list_item, null);

        // Get UI components
        TextView type = v.findViewById(R.id.textView);
        TextView value = v.findViewById(R.id.textView2);
        ImageView flag = v.findViewById(R.id.imageView2);

        // Get reading
        BSLRecord reading = BSLRecords.get(position);

        // Fill components
        type.setText(TYPE + ": " + reading.time.substring(0,16));
        value.setText(Float.toString(reading.value));

        if(reading.value >= 0 && reading.value < 4) {
            // Hypoglycemia
            flag.setColorFilter(ContextCompat.getColor(getContext(), R.color.pomegranate));
        } else if(reading.value >= 4 && reading.value <= 8) {
            // Normal
            flag.setColorFilter(ContextCompat.getColor(getContext(), R.color.design_default_color_secondary_variant));
        } else {
            // Hyperglycemia (8.1 - 30)
            flag.setColorFilter(ContextCompat.getColor(getContext(), R.color.design_default_color_error));
        }

        return v;
    }
}
