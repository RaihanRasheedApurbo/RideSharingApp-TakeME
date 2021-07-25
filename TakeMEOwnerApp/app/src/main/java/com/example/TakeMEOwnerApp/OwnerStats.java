package com.example.TakeMEOwnerApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class OwnerStats extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    FragmentActivity myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_stats);

        FragmentManager fragManager = getSupportFragmentManager();


        tabLayout = findViewById(R.id.tab_layout_income_history);
        viewPager = findViewById(R.id.viewpager_income_history);

        tabLayout.setupWithViewPager(viewPager);

        Vpadapter vpadapter = new Vpadapter(fragManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

//        vpadapter.add_fragment("Today", new fragment_1());
        vpadapter.add_fragment("Last 7 days", new fragment_2());
        vpadapter.add_fragment("Last Month", new fragment_3());

        viewPager.setAdapter(vpadapter);

    }
}