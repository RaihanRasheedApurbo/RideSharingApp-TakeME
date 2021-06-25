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
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public static String main_token = "";
    private static final String TAG = "MainActivity";
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
    private JSONObject currentResponse;
    private int currentDriver = -1;

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


        // update realtime driver locations.....
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Log.i(TAG, "Thread Name 2: " + Thread.currentThread().getName());
                    synchronized (this) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject newResponse = null;
//                    JSONObject newResponse = fetchDriverInfo();

                    if (currentResponse != null && newResponse != null) {
                        updateUIandMap(currentResponse, newResponse);
                    }
                    if (newResponse != null) {
                        currentResponse = newResponse;
                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Download finished...", Toast.LENGTH_SHORT).show();
//                            drivers.get(0).name = "kill meh";
//                            int updateIndex = 0;
//                            recyclerViewAdapter.drivers.set(updateIndex, drivers.get(0));
//                            recyclerViewAdapter.notifyItemChanged(updateIndex);
////                            setMarker(new LatLng(23.82792221582292, 90.36122243055756),"kill meh");

                        }
                    });

                    //                handler.postDelayed(new Runnable() {
                    //                    @Override
                    //                    public void run() {
                    //                        Toast.makeText(MainActivity.this, "10 seconds passed since download was finished...", Toast.LENGTH_SHORT).show();
                    //                        setMarker(new LatLng(23.82792221582292, 90.36122243055756),"kill meh");
                    //                    }
                    //                }, 10000);
                    //
                    //                Log.i(TAG, "run: Download finished.");
                    newResponse = fetchDriverInfo();
                    updateStatus();

                }
            }
        };
//        runnable.run();
        Log.i(TAG, "Thread Name 1: " + Thread.currentThread().getName());
        Thread thread = new Thread(runnable);
        thread.start();



    }

    private void updateStatus() {
        System.out.println("inside updateStatus");
        System.out.println(drivers.size());
        for(int i=0;i<drivers.size();i++)
        {
            System.out.println(i);
            System.out.println(drivers.get(i).id);
            ApiDataService apiDataService = new ApiDataService(MainActivity.this);
            apiDataService.viewDriver(MainActivity.main_token,vehicles.get(i).vehicle_id,new ApiDataService.VolleyResponseListener() {
                @Override
                public void onError(Object message) {

                    System.out.println(message);

                    System.out.println("Error in updateStatus");
                }

                @Override
                public void onResponse(Object responseObject) {
                    try {
                        System.out.println("response of updateStatus");
                        System.out.println(responseObject);
//                        JSONObject obj = (JSONObject) responseObject;
                        JSONObject responseData = new JSONObject(responseObject.toString());
                        System.out.println(responseData.has("status"));
                        System.out.println(responseData.has("status"));
                        if(responseData.has("status"))
                        {
                            System.out.println(responseData.get("status"));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void updateUIandMap(JSONObject currentResponse, JSONObject newResponse) {

    }

    public void setCurrentDriver(int i)
    {
        currentDriver = i;
    }

    private JSONObject fetchDriverInfo() {
        ApiDataService apiDataService2 = new ApiDataService(MainActivity.this);



        apiDataService2.getVehicles(MainActivity.main_token, new ApiDataService.VolleyResponseListener() {
            @Override
            public void onError(Object message) {

                System.out.println("Error in fetchDriverInfo");
            }

            @Override
            public void onResponse(Object responseObject) {
                try {

                    JSONArray response = new JSONArray(responseObject.toString());
                    System.out.println("inside fetchDriverInfo");
                    System.out.println(response);
                    System.out.println("current driver:"+currentDriver);
                    boolean reRender = false;
                    for(int i=0;i<response.length();i++)
                    {
                        JSONObject obj = (JSONObject) response.get(i);
                        JSONObject loc = (JSONObject) obj.get("location");
                        JSONArray latLang = loc.getJSONArray("coordinates");

                        System.out.println("hi");
                        System.out.println(latLang.get(1)+" "+latLang.get(0));

                        boolean changed = Math.abs(drivers.get(i).lat - (double) latLang.get(1)) > 0.01 || Math.abs(drivers.get(i).lang - (double) latLang.get(0)) > 0.01;
                        if(changed)
                        {
                            System.out.println("location changed");
                            System.out.println(drivers.get(i).lat+" "+drivers.get(i).lang);
                            drivers.get(i).lat = (double) latLang.get(1);
                            drivers.get(i).lang = (double) latLang.get(0);
                            if(currentDriver==i)
                            {
                                setMarker(new LatLng((double) latLang.get(1),(double) latLang.get(0)),drivers.get(i).name);
                            }

                        }
//                        System.out.println(loc);
                    }


//                    System.out.println("hello");
//                    System.out.println(responseData[0]);
//                    for (int i = 0; i < responseData[0].length(); i++) {
//                        String model = (String)new JSONObject(responseData[0].get(i).toString()).get("model");
//                        String type = model + " " + (String)new JSONObject(responseData[0].get(i).toString()).get("type");
//                        String regno = (String) new JSONObject(responseData[0].get(i).toString()).get("regNo");
//                        String id = (String)new JSONObject(responseData[0].get(i).toString()).get("_id");
//                        String driver_id = (String)new JSONObject(responseData[0].get(i).toString()).get("driverID");
//
//                        JSONObject location = (JSONObject) new JSONObject(responseData[0].get(i).toString()).get("location");
//                        JSONArray coordinates = (JSONArray) location.get("coordinates");
//                        double lang = (Double) coordinates.get(0);
//                        double lat = (Double) coordinates.get(1);
//                        System.out.println("coordinate: "+coordinates);
//                        System.out.println("lat :"+lat+" lang: "+lang);
//                        System.out.println("location: "+location);
//
//
//
//
//                        //System.out.println("uiui "+ driver_id);
//
//                        api_call_vehicle_info(id, model, type, regno, driver_id,lat,lang,i);
//                    }
//                    System.out.println("hello");
//
//                    MainActivity.getInstance().update_bottom_slider();
//
//
//
//                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
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