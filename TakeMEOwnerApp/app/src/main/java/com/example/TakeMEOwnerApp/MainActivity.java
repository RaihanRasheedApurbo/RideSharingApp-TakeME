package com.example.TakeMEOwnerApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FrameLayout frameLayout;
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView recyclerView;

    ArrayList<Driver_class> drivers;

    RecyclerViewAdapter recyclerViewAdapter;

    private MapView mapView;

    public static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent homeintent = new Intent(MainActivity.this, Waiting.class);
        startActivity(homeintent);

        Mapbox.getInstance(this,getString(R.string.access_token));
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        frameLayout = findViewById(R.id.bottomsheet1);
        bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        toolbar = findViewById(R.id.toolbar3);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);



        //**************** Bottom Driver Info Slider**************************//

        recyclerView = findViewById(R.id.recyclerview_driver);
        drivers = new ArrayList<>();
        drivers.add(new Driver_class("Fahad", "69"));
        drivers.add(new Driver_class("Apurba", "62"));
        drivers.add(new Driver_class("Rafi", "78"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewAdapter = new RecyclerViewAdapter( drivers,MainActivity.this, recyclerView);
        recyclerView.setAdapter( recyclerViewAdapter);

        //**************** Bottom Driver Info Slider**************************//




        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    //mapbox overrides
//    @Override
//    protected void onStart(){
//        super.onStart();
//        mapView.onStart();
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onStop(){
//        super.onStop();
//        mapView.onStop();
//    }
//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
}