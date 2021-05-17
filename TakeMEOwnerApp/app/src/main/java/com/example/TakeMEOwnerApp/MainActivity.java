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
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FrameLayout frameLayout;
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView recyclerView;
    TextView textView_username, textView_email;

    ArrayList<Driver_class> drivers;

    RecyclerViewAdapter recyclerViewAdapter;

    private MapView mapView;
    private MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent homeintent = new Intent(MainActivity.this, Waiting.class);
        startActivity(homeintent);

        Mapbox.getInstance(this,getString(R.string.access_token));
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        frameLayout = findViewById(R.id.bottomsheet1);
        bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        toolbar = findViewById(R.id.toolbar3);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textView_username = header.findViewById(R.id.textView_owner_name);
        textView_email = header.findViewById(R.id.textView_owner_email);

        textView_username.setText("Fahad Rahman");
        textView_email.setText("fahad110490@gmail.com");


        //**************** Bottom Driver Info Slider**************************//

        recyclerView = findViewById(R.id.recyclerview_driver);
        drivers = new ArrayList<>();
        Driver_class a = new Driver_class("Abdur Rahman Fahad", "201605069");
        Driver_class b = new Driver_class("Rayhan Rasheed Apurba", "201605062");
        Driver_class c = new Driver_class("Mohib Hossain Rafi", "201605078");
        a.income = 209.45;
        b.income = 405.25;
        c.income = 112.35;
        Vehicle car = new Vehicle("6074779ae70efe2e", "Toyota Premium", 111503145);
        a.vehicle = car;
        b.vehicle = car;
        c.vehicle = car;
        drivers.add(a);
        drivers.add(b);
        drivers.add(c);

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
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
//        mapboxMap.addMarker(new MarkerOptions()
//                .position(new LatLng(48.85819, 2.29458))
//                .title("Eiffel Tower"));
//        setMarker(new LatLng(new LatLng(48.85819, 2.29458)),"Apurbo");
    }

    public void setMarker(LatLng location, String markerName)
    {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,13));
        mapboxMap.addMarker(new MarkerOptions()
                .position(location)
                .title(markerName));
    }

    @Override
    protected void onStart(){
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}