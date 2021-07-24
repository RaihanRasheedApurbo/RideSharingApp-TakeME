package com.example.takemedriverapp.ui.slideshow;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.takemedriverapp.R;
import com.google.android.material.tabs.TabLayout;

public class SlideshowFragment extends Fragment {
    static View root = null;
    private SlideshowViewModel slideshowViewModel;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    FragmentActivity myContext;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        slideshowViewModel =
//                new ViewModelProvider(this).get(SlideshowViewModel.class);
        root = inflater.inflate(R.layout.fragment_slideshow, container, false);

//        FragmentManager fragManager = myContext.getSupportFragmentManager();
//
//
//        tabLayout = root.findViewById(R.id.tab_layout_income_history);
//        viewPager = root.findViewById(R.id.viewpager_income_history);
//
//        tabLayout.setupWithViewPager(viewPager);
//        System.out.println("kill meh hi from slideshowfragment");
//
//        Vpadapter vpadapter = new Vpadapter(fragManager,Vpadapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//
//        System.out.println("muhahahaha");
////        vpadapter.add_fragment("Today", new fragment_1());
//        vpadapter.add_fragment("Last 7 days", new fragment_2());
//        vpadapter.add_fragment("Last Month", new fragment_3());
//        viewPager.setAdapter(vpadapter);
//        viewPager.refreshDrawableState();
//        System.out.println("muhahahaha2");






        return root;
    }

    @Override
    public void onResume() {
        System.out.println("onResume of HomeFragment");
        FragmentManager fragManager = myContext.getSupportFragmentManager();


        tabLayout = root.findViewById(R.id.tab_layout_income_history);
        viewPager = root.findViewById(R.id.viewpager_income_history);

        tabLayout.setupWithViewPager(viewPager);
        System.out.println("kill meh hi from slideshowfragment");

        Vpadapter vpadapter = new Vpadapter(fragManager,Vpadapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        System.out.println("muhahahaha");
//        vpadapter.add_fragment("Today", new fragment_1());

        vpadapter.add_fragment("Last 7 days", new fragment_2());
        vpadapter.add_fragment("Last Month", new fragment_3());

        viewPager.setAdapter(vpadapter);
        viewPager.refreshDrawableState();
        System.out.println("muhahahaha2");
        System.out.println(vpadapter.getCount());
        super.onResume();


    }

    @Override
    public void onPause() {
        System.out.println("OnPause of HomeFragment");
        super.onPause();
    }
}