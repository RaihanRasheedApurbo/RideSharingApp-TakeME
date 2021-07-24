package com.example.takemedriverapp.ui.slideshow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class Vpadapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    private final ArrayList<String> FragmentTitle = new ArrayList<>();


    public Vpadapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void add_fragment(String title, Fragment fragment)
    {

        fragmentArrayList.add(fragment);
        FragmentTitle.add(title);
        System.out.println("size of fragmentArraylist :"+fragmentArrayList.size());
        System.out.println("size of FragmentTitle :"+FragmentTitle.size());

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentTitle.get(position);
    }
}
