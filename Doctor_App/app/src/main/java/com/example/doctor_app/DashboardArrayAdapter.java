package com.example.doctor_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DashboardArrayAdapter extends ArrayAdapter<Patient> {
    ArrayList<Patient> patientList;

    public DashboardArrayAdapter(Context context,
                                 int resource,
                                 ArrayList<Patient> objects) {
        super(context, resource, objects);
        patientList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.dashboard_list_item, null);
        TextView textView = v.findViewById(R.id.textView);
        ImageView imageView = v.findViewById(R.id.imageView);
        textView.setText(patientList.get(position).getName());
        imageView.setImageResource(R.drawable.blank_icon);
        return v;
    }
}
