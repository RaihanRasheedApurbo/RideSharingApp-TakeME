package com.example.takemedriverapp.ui.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.takemedriverapp.R;
import com.example.takemedriverapp.ui.slideshow.Vpadapter;
import com.example.takemedriverapp.ui.slideshow.fragment_1;
import com.example.takemedriverapp.ui.slideshow.fragment_2;
import com.example.takemedriverapp.ui.slideshow.fragment_3;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TestFragment extends Fragment {

    private TestViewModel testViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        testViewModel = new ViewModelProvider(this).get(TestViewModel.class);

        View root = inflater.inflate(R.layout.testfrag, container, false);

        BarChart barChart = root.findViewById(R.id.barchart_seven_days_income1);

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

        return root;
    }
}