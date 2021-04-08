package com.example.TakeMEOwnerApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
                String name = drivers.get(itemPosition).name;
                // Go to new window instead of making toast
                Toast.makeText( parent.getContext(), "Hello " + name, Toast.LENGTH_SHORT).show();
            }
        });


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView1.setText( drivers.get(position).name );
        holder.textView2.setText( drivers.get(position).id );
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView1, textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.text_view_driver_name);
            textView2 = itemView.findViewById(R.id.text_view_driver_id);
        }
    }
}
