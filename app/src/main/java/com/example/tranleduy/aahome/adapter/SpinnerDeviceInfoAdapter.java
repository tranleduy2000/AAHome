package com.example.tranleduy.aahome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tranleduy.aahome.R;

/**
 * Created by ${DUY} on 17-Jun-16.
 */
public class SpinnerDeviceInfoAdapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private String[] data;

    public SpinnerDeviceInfoAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data = context.getResources().getStringArray(R.array.spin_device);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        TextView txt = (TextView) view.findViewById(R.id.textView);
        txt.setText(data[position]);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        TextView txt = (TextView) view.findViewById(R.id.textView);
        txt.setText(data[position]);
        return view;
    }
}
