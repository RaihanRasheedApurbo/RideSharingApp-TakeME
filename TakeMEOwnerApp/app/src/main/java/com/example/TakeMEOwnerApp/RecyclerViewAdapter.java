package com.example.TakeMEOwnerApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<Driver_class> drivers;
    Context context;
    RecyclerView recyclerView;

    public RecyclerViewAdapter(ArrayList<Driver_class> drivers, Context context, RecyclerView r) {
        this.drivers = drivers;
        this.context = context;
        this.recyclerView = r;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_item_2, parent, false);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                Driver_class driver =  drivers.get(itemPosition);
                String name = driver.name;
                double lat = driver.lat;
                double lang = driver.lang;
                System.out.println(lat+" "+lang);
                // Go to new window instead of making toast
                MainActivity activity = (MainActivity) context;
                activity.setMarker(new LatLng(lat,lang),name);
                activity.setCurrentDriver(itemPosition);
//                System.out.println("inside RecyclerViewAdapter view.setOnClickListener");

//                Toast.makeText( parent.getContext(), "Hello " + name, Toast.LENGTH_SHORT).show();
            }
        });


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView1.setText( drivers.get(position).name );
        holder.textView2.setText( drivers.get(position).id );
        holder.textView3.setText( Integer.toString(drivers.get(position).vehicle.reg_no) );
        holder.textView4.setText( drivers.get(position).vehicle.model );
        holder.textView5.setText( drivers.get(position).income.toString() );

    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView1, textView2, textView3, textView4, textView5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.text_view_driver_name);
            textView2 = itemView.findViewById(R.id.text_view_driver_id);
            textView3 = itemView.findViewById(R.id.text_view_car_reg_no_value);
            textView4 = itemView.findViewById(R.id.text_view_car_model_value);
            textView5 = itemView.findViewById(R.id.text_view_driver_earning_value);


        }
    }
}
