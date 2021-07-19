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

        source.setText(getItem(position).getSource());
        dest.setText(getItem(position).getDestination());
        return convertView;
    }
}
