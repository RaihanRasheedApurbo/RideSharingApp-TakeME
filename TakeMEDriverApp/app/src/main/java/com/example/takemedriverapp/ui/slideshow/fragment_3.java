package com.example.takemedriverapp.ui.slideshow;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.takemedriverapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class fragment_3 extends Fragment {

    public static View root = null;

    public fragment_3()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_3, container, false);

        PieChart pieChart = root.findViewById(R.id.pie_chart);

        ArrayList<PieEntry> income = new ArrayList<>();

        income.add(new PieEntry(2040, "Week 1"));
        income.add(new PieEntry(1870, "Week 2"));
        income.add(new PieEntry(3110, "Week 3"));
        income.add(new PieEntry(1590, "Week 4"));

        PieDataSet pieDataSet = new PieDataSet(income, "Income of Last Month");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Income of Last Month");
        pieChart.animate();


        return root;
    }
}