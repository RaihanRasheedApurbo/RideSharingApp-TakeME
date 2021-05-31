package com.example.takemedriverapp.ui.home;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takemedriverapp.MainActivity2;
import com.example.takemedriverapp.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
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
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

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
    private Button cancelButton;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);


        System.out.println("kill meh");
        if(root == null)
        {
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

            cancelButton = root.findViewById(R.id.endButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
    //                Intent intent = getIntent();
    //                finish();
    //                startActivity(intent);
//                    HomeFragment fragment = (HomeFragment)
//                            getParentFragmentManager().findFragmentById(R.id.nav_home);
//                    System.out.println("total fragments: "+getParentFragmentManager().getFragments().size());
//                    getParentFragmentManager().beginTransaction()
//                            .detach(fragment)
//                            .attach(fragment)
//                            .commit();

                }
            });
        }

//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        // new code



        //new code ended
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
                            boolean simulateRoute = true;
                            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                    .directionsRoute(currentRoute)
                                    .shouldSimulateRoute(simulateRoute)
                                    .build();
                            // Call this method with Context from within an Activity
                            NavigationLauncher.startNavigation(getActivity(), options);
                        }
                        else if(driverState==DriverState.RESTING)
                        {

                            Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
                            // find passenger using loading screen and calling backend apis below...
                            // Fahad code here.....................................
                            //Point destinationPoint = getPassenger(); // write getPassenger Function
                            // assuming passenger has been found his lat lang is (90.37609,23.83287)

                            Point destinationPoint = Point.fromLngLat(90.37609,23.83287);

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

                                cancelButton.setVisibility(v.VISIBLE);
                                cancelButton.setEnabled(true);
                            }



                        }

                    }
                });

            }
        });
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
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
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
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
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