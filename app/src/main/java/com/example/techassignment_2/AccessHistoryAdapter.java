package com.example.techassignment_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AccessHistoryAdapter extends ArrayAdapter<String> {

    public AccessHistoryAdapter(Context context, ArrayList<String> history) {
        super(context, 0, history);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        String entry = getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(entry);

        return convertView;
    }
}
