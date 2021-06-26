package com.example.takemedriverapp.ui.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takemedriverapp.ApiDataService;
import com.example.takemedriverapp.MainActivity2;
import com.example.takemedriverapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import okhttp3.EventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {

    private HomeViewModel homeViewModel;

    // new code
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button startButton;
    private boolean lastResponseReceived = true;


    // boiler plate code of mapbox ended ... Apurbo's code starts from below...
    // state management
    enum DriverState {
        RESTING,
        FINDING,
        PICKING,
        RIDING
    }
    DriverState driverState;

    static View root = null;
    // new code ended
    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 10000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS;
    // Variables needed to listen to location updates
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);
    private MapboxNavigation mapboxNavigation;
    private Location prevLocation;


    // Fahad's Variables
    FrameLayout frameLayout;
    BottomSheetBehavior bottomSheetBehavior;
    int time_spent = 0;
    ProgressDialog progressDialog;
    TextView bottom_text;
    Button bottom_start, bottom_cancel, bottom_end_ride;

    double driver_lat, driver_long;
    double curr_dest_lat, curr_dest_long;
    double passenger_lat = 0;
    double passenger_long = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);


        System.out.println("kill meh");

        System.out.println("kill meh again");
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        root = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        driverState = DriverState.RESTING;
        startButton = root.findViewById(R.id.startButton);
        startButton.setText("Search Passenger");
        startButton.setEnabled(true);




        //Fahad's************************************************

        frameLayout = root.findViewById(R.id.bottomsheet1);
        bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottom_text = root.findViewById(R.id.bottom_sheet_text);
        bottom_start = root.findViewById(R.id.bottom_start_button);
        bottom_cancel = root.findViewById(R.id.bottom_cancel_button);
        bottom_end_ride = root.findViewById(R.id.bottom_end_ride_button);

        bottom_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver_lat = locationComponent.getLastKnownLocation().getLatitude();
                driver_long = locationComponent.getLastKnownLocation().getLongitude();

                double distance_now = calculateDistanceInMeter(driver_lat, driver_long, passenger_lat, passenger_long);
                if(distance_now>25000)
                    Toast.makeText(getApplicationContext(),distance_now + " feets away",Toast.LENGTH_SHORT).show();
                else
                {
                    System.out.println("calling start ride");
                    // fahad instead of creating a toast we need to call backend api and find passenger destination
                    // then change the ui so that start ride and cancel button vanishes, and we create a button called end ride between
                    // those vanished button. Then we have to change the map.... leave that to me... I will do that....
//                    bottom_text.setText("Destination: \nLat: " + curr_dest_lat + "\nLon: " + curr_dest_long);
                    start_ride();

                }

            }
        });

        bottom_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked // Reset Things
                                //reset_passenger();
                                // we have to call backend here and update backend so that it knows ride has been canceld by driver
                                // fahad call backend here..... to cancel the ride....

                                progressDialog = new ProgressDialog(HomeFragment.super.getContext());
                                progressDialog.show();
                                progressDialog.setContentView(R.layout.waiting_screen);
                                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                cancel_ride();
//                                locationEngine.removeLocationUpdates(callback);
//                                Intent intent = getActivity().getIntent();
//                                getActivity().finish();
//                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        bottom_end_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Ride Ended",Toast.LENGTH_SHORT).show();
                end_ride();
                driverState = DriverState.RESTING;
                locationEngine.removeLocationUpdates(callback);
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
//                reset_passenger();
//                bottom_end_ride.setVisibility(View.INVISIBLE);
//                bottom_end_ride.setEnabled(false);
            }
        });
        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

                addDestinationIconSymbolLayer(style);

                mapboxMap.addOnMapClickListener(HomeFragment.this);
                startButton = root.findViewById(R.id.startButton);
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(driverState==DriverState.PICKING)
                        {
                            boolean simulateRoute = false;
                            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                    .directionsRoute(currentRoute)
                                    .shouldSimulateRoute(simulateRoute)
                                    .build();
                            // Call this method with Context from within an Activity

                            NavigationLauncher.startNavigation(getActivity(), options);

                        }
                        else if(driverState==DriverState.RESTING)
                        {

                            //Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
                            // find passenger using loading screen and calling backend apis below...
                            // Fahad code here.....................................

                            progressDialog = new ProgressDialog(HomeFragment.super.getContext());
                            progressDialog.show();
                            progressDialog.setContentView(R.layout.waiting_screen);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            time_spent = 0;
                            api_call_passenger_search();

                            //Point destinationPoint = getPassenger(); // write getPassenger Function
                            // assuming passenger has been found his lat lang is (90.37609,23.83287)

                        }

                    }
                });

            }
        });
    }


    public void api_call_passenger_search()
    {

        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.searchPassenger(MainActivity2.main_token, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in finding Passenger");
            }

            @Override
            public void onResponse(Object responseObject)
            {

                try
                {
                    JSONObject responseData = new JSONObject(responseObject.toString());
                    System.out.println(responseData);

                    if(responseData.has("passengerInfo"))
                    {
                        JSONObject passengerInfo = (JSONObject) responseData.get("passengerInfo");

                        JSONObject passengerData = (JSONObject) passengerInfo.get("passengerData");
                        JSONArray pickUpPoint = (JSONArray) passengerInfo.get("pickUpPoint");
                        JSONArray dropOutPoint = (JSONArray) passengerInfo.get("dropOutPoint");

                        double lat = Double.parseDouble(pickUpPoint.getString(0));
                        double lon = Double.parseDouble(pickUpPoint.getString(1));

                        curr_dest_lat = Double.parseDouble(dropOutPoint.getString(0));
                        curr_dest_long = Double.parseDouble(dropOutPoint.getString(1));

                        //System.out.println("passengerData: " + passengerData);
                        System.out.println(passengerData.get("name"));
                        System.out.println(passengerData.get("phone"));
                        System.out.println("pickUpPoint: " + lat + " , " + lon);

                        update_bottom_slider(passengerData.get("name").toString(), passengerData.get("phone").toString());
                        update_marker_passenger(lon, lat);

                        //stop_searching();
                        progressDialog.dismiss();

                    }
                    else
                    {
                        String message = (String) responseData.get("message");
                        if(time_spent>12)
                        {
                            bottom_text.setText("No Passenger Found!");
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            stop_searching();
                            progressDialog.dismiss();
                            return;
                        }
                        time_spent += 3;
                        TimeUnit.SECONDS.sleep(3);
                        api_call_passenger_search();
                        //first time or no match so nothing I guess
                    }

                }
                catch (JSONException | InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        });


    }


    public void stop_searching()
    {
        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.stopSearchPassenger(MainActivity2.main_token, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in stop search");
            }

            @Override
            public void onResponse(Object responseObject) {

                try {
                    JSONObject responseData = new JSONObject(responseObject.toString());
                    System.out.println(responseData);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    public void cancel_ride()
    {
        ApiDataService apiDataService = new ApiDataService(this.getContext());
        driverState = DriverState.RESTING;
        apiDataService.cancelMatch(MainActivity2.main_token, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in cancel ride");
                System.out.println(message);
                progressDialog.dismiss();

            }

            @Override
            public void onResponse(Object responseObject) {
                try {

                    progressDialog.dismiss();

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    locationEngine.removeLocationUpdates(callback);
                                    Intent intent = getActivity().getIntent();
                                    getActivity().finish();
                                    startActivity(intent);
                                    driverState = DriverState.RESTING;
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("You have been fined BDT 20.00\nfor cancelling ride").setPositiveButton("ok", dialogClickListener).show();


                    JSONObject responseData = new JSONObject(responseObject.toString());
                    System.out.println("Ride Cancelled by driver");
                    System.out.println(responseData);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

    }

    public void start_ride()
    {
        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.startRide(MainActivity2.main_token, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in starting ride");
                Toast.makeText(getApplicationContext(),"Problem in starting ride so canceling and restarting the app",Toast.LENGTH_SHORT).show();
                end_ride();
                locationEngine.removeLocationUpdates(callback);
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            }

            @Override
            public void onResponse(Object responseObject) {

                try {
                    JSONObject responseData = new JSONObject(responseObject.toString());
                    System.out.println(responseData);
                    String message = (String) responseData.get("message");
                    System.out.println("message: "+message);
                    JSONArray latLang = (JSONArray) responseData.get("dropOutPoint");
                    double lat = (Double) latLang.get(0);
                    double lang = (Double) latLang.get(1);
                    System.out.println(latLang.get(0)+ " "+ latLang.get(1));
                    update_marker_passenger(lang,lat);
                    System.out.println("Ride Started by driver");



                    bottom_start.setVisibility(View.INVISIBLE);
                    bottom_start.setEnabled(false);

                    bottom_cancel.setVisibility(View.INVISIBLE);
                    bottom_cancel.setEnabled(false);

                    bottom_end_ride.setVisibility(View.VISIBLE);
                    bottom_end_ride.setEnabled(true);
                    bottom_end_ride.setText("End Ride");
                    Toast.makeText(getApplicationContext(),"Ride Started",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println(e);
                    Toast.makeText(getApplicationContext(),"Problem in starting ride so canceling and restarting the app",Toast.LENGTH_SHORT).show();
                    end_ride();
                    locationEngine.removeLocationUpdates(callback);
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });




    }

    public void end_ride()
    {
        driverState = DriverState.RESTING;
        ApiDataService apiDataService = new ApiDataService(this.getContext());
        double end_lat = locationComponent.getLastKnownLocation().getLatitude();
        double end_long = locationComponent.getLastKnownLocation().getLongitude();
        apiDataService.endRide(MainActivity2.main_token, end_lat, end_long, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in Ending ride");
            }

            @Override
            public void onResponse(Object responseObject) {

                try {
                    JSONObject responseData = new JSONObject(responseObject.toString());
                    System.out.println("Ride Ended");
                    System.out.println(responseData);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

//        bottom_start.setVisibility(View.INVISIBLE);
//        bottom_start.setEnabled(false);
//
//        bottom_cancel.setVisibility(View.INVISIBLE);
//        bottom_cancel.setEnabled(false);
//
//        bottom_end_ride.setVisibility(View.VISIBLE);
//        bottom_end_ride.setEnabled(true);
//        bottom_end_ride.setText("End Ride");


    }

    public void update_bottom_slider(String name, String phone)
    {
        bottom_text.setText("You have been matched with a passenger!\n" + "Name : " + name + "\nPhone : " + phone);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottom_start.setVisibility(View.VISIBLE);
        bottom_start.setEnabled(true);

        bottom_cancel.setVisibility(View.VISIBLE);
        bottom_cancel.setEnabled(true);

    }


    public void update_marker_passenger(double lon, double lat)
    {

        //Point destinationPoint = Point.fromLngLat(90.37609,23.83287);

        passenger_long = lon;
        passenger_lat = lat;


        Point destinationPoint = Point.fromLngLat(lon,lat);

        if(destinationPoint!=null)
        {
            Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                    locationComponent.getLastKnownLocation().getLatitude());
            GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
            if (source != null) {
                source.setGeoJson(Feature.fromGeometry(destinationPoint));
            }

            getRoute(originPoint, destinationPoint);

            startButton.setText("Start Navigation");
            startButton.setEnabled(true);
            startButton.setBackgroundResource(R.color.mapboxBlue);

            driverState = DriverState.PICKING;


        }


    }

    public void updateNavigationPath()
    {

        //Point destinationPoint = Point.fromLngLat(90.37609,23.83287);
        System.out.println("inside updateNavigationPath");
        double lon=0,lat=0;
        lon = passenger_long;
        lat = passenger_lat;


        if(lat==0 && lon == 0)
        {
            return;
        }
        Point destinationPoint = Point.fromLngLat(lon,lat);
        System.out.println("lat lang is not 0");
        if(destinationPoint!=null)
        {
//            Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                    locationComponent.getLastKnownLocation().getLatitude());
//            GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//            if (source != null) {
//                source.setGeoJson(Feature.fromGeometry(destinationPoint));
//            }
            System.out.println("calling getRoute from updateNavigationPath");
            getRoute(Point.fromLngLat(prevLocation.getLongitude(),prevLocation.getLatitude()), destinationPoint);

//            startButton.setText("Start Navigation");
//            startButton.setEnabled(true);
//            startButton.setBackgroundResource(R.color.mapboxBlue);
//
//            driverState = DriverState.PICKING;


        }


    }


    public void reset_passenger()
    {

        bottom_text.setText("Welcome");
        bottom_start.setVisibility(View.INVISIBLE);
        bottom_start.setEnabled(false);

        bottom_cancel.setVisibility(View.INVISIBLE);
        bottom_cancel.setEnabled(false);



        startButton.setText("Search Passenger");
        driverState = DriverState.RESTING;

    }


    public int calculateDistanceInMeter(double userLat, double userLng, double venueLat, double venueLng)
    {

        double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * 1000 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }






    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }
    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

//        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
//        String toastString = Double.toString(destinationPoint.latitude()) + "," + Double.toString(destinationPoint.longitude());
//        Toast.makeText(getApplicationContext(),toastString,Toast.LENGTH_SHORT).show();
//        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                locationComponent.getLastKnownLocation().getLatitude());
//
//        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//        if (source != null) {
//            source.setGeoJson(Feature.fromGeometry(destinationPoint));
//        }
//
//        getRoute(originPoint, destinationPoint);
//        button.setEnabled(true);
//        button.setBackgroundResource(R.color.mapboxBlue);
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        System.out.println("inside getRoute");
        NavigationRoute.builder(getActivity())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
//                            navigationMapRoute = null;
                            navigationMapRoute.removeRoute();
//                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        } else {
//                            navigationMapRoute = new NavigationMapRoute.Builder
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
//                            .withMapboxNavigation(mapboxNavigation)
//                                    .withVanishRouteLineEnabled(true)
//                            navigationMapRoute.
                        }
                        navigationMapRoute.addRoute(currentRoute);
//                        navigationMapRoute.addProgressChangeListener(new ProgressChangeListener() {
//                            @Override
//                            public void onProgressChange(Location location, RouteProgress routeProgress) {
//
//                            }
//                        });
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(getActivity(), loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(getActivity());

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private static class MainActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<HomeFragment> fragmentWeakReference;

        MainActivityLocationCallback(HomeFragment fragment) {
            this.fragmentWeakReference = new WeakReference<>(fragment);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @SuppressLint("StringFormatMatches")
        @Override
        public void onSuccess(LocationEngineResult result) {
            HomeFragment fragment = fragmentWeakReference.get();

            if (fragment != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                // Create a Toast which displays the new location's coordinates
//                Toast.makeText(fragment.getActivity(),
//                        String.valueOf(result.getLastLocation().getLatitude()) + " " + String.valueOf(result.getLastLocation().getLongitude()),
//                        Toast.LENGTH_SHORT).show();
//                System.out.println("kill meh");
                Location prevLocation = fragment.prevLocation;
                // Pass the new location to the Maps SDK's LocationComponent
                if (fragment.mapboxMap != null && result.getLastLocation() != null) {
//                    System.out.println("inside forceLocationupdate");
                    fragment.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                }
                if(prevLocation != null)
                {
                    boolean notNear = Math.abs(prevLocation.getLatitude()-location.getLatitude()) > 0.001 || Math.abs(prevLocation.getLongitude()-location.getLongitude()) > 0.001;
//                    System.out.println("calculated notNear: notNear "+notNear);




                    if(notNear)
                    {

                        fragment.sendLocation(location);

                        fragment.prevLocation = location;
                        if(fragment.driverState == DriverState.PICKING || fragment.driverState == DriverState.RIDING)
                        {
//                            System.out.println("picking or riding");
//                            System.out.println("need to redraw");

                             fragment.updateNavigationPath();
//                            CameraPosition position = new CameraPosition.Builder()
//                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
//                                    .zoom(10)
//                                    .tilt(20)
//                                    .build();
//                            fragment.mapboxMap.setCameraPosition(position);
//                            System.out.println("cameraposition has been set");
                            fragment.locationComponent.setCameraMode(CameraMode.TRACKING);
                            fragment.locationComponent.setRenderMode(RenderMode.COMPASS);


                            // fahad this section gets executed if gps location changes while picking or riding....
                            // we have to call backend codes here to inform backend all the time about our position so that owner and user can see a driver's realtime location
                            // so constant position update can be done from here.... the object named location in this scope has the current location of the
                            // driver...


                        }
                    }
                }
                else
                {
                    fragment.prevLocation = location;
                }

                if(fragment.driverState == DriverState.PICKING || fragment.driverState == DriverState.RIDING)
                {
                    if(fragment.lastResponseReceived)
                    {
                        fragment.lastResponseReceived = false;
                        fragment.fetchRideDetails();

                    }


                }



            }
        }



        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            HomeFragment fragment = fragmentWeakReference.get();
            if (fragment.getActivity() != null) {
                Toast.makeText(fragment.getActivity(), exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void fetchRideDetails()
    {

        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.searchPassenger(MainActivity2.main_token,
                new ApiDataService.VolleyResponseListener() {

                    @Override
                    public void onError(Object message) {
                        lastResponseReceived = true;
                        System.out.println("fetching ride info");
                        System.out.println("Problem in ride info");
//                        userState = UserState.RESTING;
//                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(Object responseObject)
                    {
                        lastResponseReceived = true;

                        try{

                            JSONObject responseData = new JSONObject(responseObject.toString());
                            //System.out.println(responseData);

                            System.out.println(responseData);
                            String status = responseData.getString("status");
                            System.out.println("status: "+status);
                            if(status.contains("cancelled"))
                            {
                                System.out.println("ride has been canceled");
                                driverState = DriverState.RESTING;
                                //fahad show a popup and restart the whole thing after
                                // after the user press ok do below stuff....

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                locationEngine.removeLocationUpdates(callback);
                                                Intent intent = getActivity().getIntent();
                                                getActivity().finish();
                                                startActivity(intent);
                                                driverState = DriverState.RESTING;
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Sorry!\nRide cancelled by passenger").setPositiveButton("ok", dialogClickListener).show();




                            }



                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(getActivity(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            //finish();
        }
    }
    public void sendLocation(Location location)
    {
        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.updateLocation(MainActivity2.main_token,location.getLatitude(),location.getLongitude(), new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in updating location");
            }

            @Override
            public void onResponse(Object responseObject)
            {

                try
                {
                    System.out.println("sending location in backend");
                    JSONObject responseData = new JSONObject(responseObject.toString());
                    System.out.println(responseData);



                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}