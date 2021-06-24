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
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public static String main_token = "";

    private static MainActivity instance;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FrameLayout frameLayout;
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView recyclerView;
    TextView textView_username, textView_email;

    JSONObject responseData, bodyData, headerData;

    ArrayList<Driver_class> drivers = new ArrayList<>();
    ArrayList<Vehicle> vehicles = new ArrayList<>();

    RecyclerViewAdapter recyclerViewAdapter;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Marker currentMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

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



        //**************** Bottom Driver Info Slider**************************//

        recyclerView = findViewById(R.id.recyclerview_driver);



        //**************** Bottom Driver Info Slider End**************************//

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }


    public static MainActivity getInstance() {
        return instance;
    }

    public void set_owner_data(String s1, String s2)
    {
        View header = navigationView.getHeaderView(0);

        textView_username = header.findViewById(R.id.textView_owner_name);
        textView_email = header.findViewById(R.id.textView_owner_email);

        textView_username.setText(s1);
        textView_email.setText(s2);
    }

    public void add_vehicle(String id, String model, int regno)
    {

        Vehicle car = new Vehicle(id, model, regno);
        vehicles.add(car);

    }

    public void add_driver(String driver_name, String id, double driver_income, double lat, double lang)
    {
        //String driver_name = "Driver " + new Integer(drivers.size()+1).toString();
        drivers.add(new Driver_class(driver_name, id,driver_income, lat,lang));
    }

    public void update_bottom_slider()
    {
        for (int i = 0; i < drivers.size(); i++) {
            drivers.get(i).vehicle = vehicles.get(i);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewAdapter = new RecyclerViewAdapter( drivers,MainActivity.this, recyclerView);
        recyclerView.setAdapter( recyclerViewAdapter);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    //mapbox overrides
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                System.out.println("sitting idle");
            }
        });

//        mapboxMap.addMarker(new MarkerOptions()
//                .position(new LatLng(48.85819, 2.29458))
//                .title("Eiffel Tower"));
//        setMarker(new LatLng(new LatLng(48.85819, 2.29458)),"Apurbo");
    }

    public void setMarker(LatLng location, String markerName)
    {
        if(currentMarker!=null)
        {
            mapboxMap.removeMarker(currentMarker);
        }
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,13));
        currentMarker = mapboxMap.addMarker(new MarkerOptions()
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