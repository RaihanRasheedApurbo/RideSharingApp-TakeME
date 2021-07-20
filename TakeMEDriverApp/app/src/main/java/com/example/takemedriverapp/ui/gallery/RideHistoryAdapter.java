package com.example.takemedriverapp.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.takemedriverapp.R;

import java.util.ArrayList;

public class RideHistoryAdapter extends ArrayAdapter<RideHistory> {

    private Context mContext;
    private int mResource;


    public RideHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<RideHistory> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView source = convertView.findViewById(R.id.ride_history_item_from);
        TextView dest = convertView.findViewById(R.id.ride_history_item_to);
        TextView fare = convertView.findViewById(R.id.ride_history_fare);
        TextView date_time = convertView.findViewById(R.id.ride_history_item_date_time);


        source.setText(getItem(position).getSource());
        dest.setText(getItem(position).getDestination());
        fare.setText("BDT " + getItem(position).getFare().toString());
        date_time.setText(getItem(position).getDate());

        return convertView;
    }
}
