package com.example.TakeMEOwnerApp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class fragment_2 extends Fragment {
    public static View root = null;
    public fragment_2()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_2, container, false);

        BarChart barChart = root.findViewById(R.id.barchart_seven_days_income22);

        //TextView textView = root.findViewById(R.id.frag2_text);
        //textView.setText("meaw");

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1f, 220));
        barEntries.add(new BarEntry(2f, 320));
        barEntries.add(new BarEntry(3f, 420));
        barEntries.add(new BarEntry(4f, 280));
        barEntries.add(new BarEntry(5f, 340));
        barEntries.add(new BarEntry(6f, 400));
        barEntries.add(new BarEntry(7f, 200));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Income");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Last 7 days' income");
        barChart.animateY(2000);

        System.out.println("mewaw");

        return root;
    }
}