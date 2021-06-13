package com.example.takemeuserapp.ui.home;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takemeuserapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
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
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

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



    // Fahad's Variables
    FrameLayout frameLayout;
    BottomSheetBehavior bottomSheetBehavior;
    //int time_spent = 0;
    //ProgressDialog progressDialog;
    TextView bottom_text;
    Button bottom_start, bottom_cancel;





    // boiler plate code of mapbox ended ... Apurbo's code starts from below...
    // state management
    enum UserState {
        RESTING,
        FINDING,
        PICKING,
        RIDING
    }
    UserState userState;

    static View root = null;
    //Fahad's variable should start here....


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
        //Apurbo's code *****************





        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        root = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        userState = UserState.RESTING;
        startButton = root.findViewById(R.id.startButton);
        startButton.setVisibility(View.GONE);
//        startButton.setText("Search Passenger");
//        startButton.setEnabled(true);
        Places.initialize(getApplicationContext(), getString(R.string.apiKey));
        PlacesClient placesClient = Places.createClient(getContext());


        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);

//        autocompleteFragment.setLocationBias(RectangularBounds));
        autocompleteFragment.setCountries("BD");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                autocompleteFragment.getView().setVisibility(View.GONE);

            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        //Apurbo's code ended here **********************


        //Fahad's code****************************

        frameLayout = root.findViewById(R.id.bottomsheet1);
        bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottom_text = root.findViewById(R.id.bottom_sheet_text);
        bottom_start = root.findViewById(R.id.bottom_start_button);
        bottom_cancel = root.findViewById(R.id.bottom_cancel_button);




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
                        if(userState==UserState.PICKING)
                        {
                            boolean simulateRoute = true;
                            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                    .directionsRoute(currentRoute)
                                    .shouldSimulateRoute(simulateRoute)
                                    .build();
                            // Call this method with Context from within an Activity
                            NavigationLauncher.startNavigation(getActivity(), options);
                        }
                        else if(userState==UserState.RESTING)
                        {

                            //Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
                            // find passenger using loading screen and calling backend apis below...
                            // Fahad code here.....................................

//                            progressDialog = new ProgressDialog(HomeFragment.super.getContext());
//                            progressDialog.show();
//                            progressDialog.setContentView(R.layout.waiting_screen);
//                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//                            time_spent = 0;
//                            api_call_passenger_search();

                            //Point destinationPoint = getPassenger(); // write getPassenger Function
                            // assuming passenger has been found his lat lang is (90.37609,23.83287)

                        }

                    }
                });

            }
        });
    }


//    public void api_call_passenger_search()
//    {
//
//        ApiDataService apiDataService = new ApiDataService(this.getContext());
//
//        apiDataService.searchPassenger(MainActivity2.main_token, new ApiDataService.VolleyResponseListener() {
//
//            @Override
//            public void onError(Object message) {
//                System.out.println("Problem in finding Passenger");
//            }
//
//            @Override
//            public void onResponse(Object responseObject)
//            {
//
//                try
//                {
//                    JSONObject responseData = new JSONObject(responseObject.toString());
//                    //System.out.println(responseData);
//
//                    if(responseData.has("passengerInfo"))
//                    {
//                        JSONObject passengerInfo = (JSONObject) responseData.get("passengerInfo");
//
//                        JSONObject passengerData = (JSONObject) passengerInfo.get("passengerData");
//                        JSONArray pickUpPoint = (JSONArray) passengerInfo.get("pickUpPoint");
//
//                        double lat = Double.parseDouble(pickUpPoint.getString(0));
//                        double lon = Double.parseDouble(pickUpPoint.getString(1));
//
//
//                        //System.out.println("passengerData: " + passengerData);
//                        System.out.println(passengerData.get("name"));
//                        System.out.println(passengerData.get("phone"));
//                        System.out.println("pickUpPoint: " + lat + " , " + lon);
//
//                        update_bottom_slider(passengerData.get("name").toString(), passengerData.get("phone").toString());
//                        update_marker_passenger(lon, lat);
//
//                        stop_searching();
//                        progressDialog.dismiss();
//
//                    }
//                    else
//                    {
//                        String message = (String) responseData.get("message");
//                        if(time_spent>12)
//                        {
//                            bottom_text.setText("No Passenger Found!");
//                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                            stop_searching();
//                            progressDialog.dismiss();
//                            return;
//                        }
//                        time_spent += 3;
//                        TimeUnit.SECONDS.sleep(3);
//                        api_call_passenger_search();
//                        //first time or no match so nothing I guess
//                    }
//
//                }
//                catch (JSONException | InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//
//    }


//    public void stop_searching()
//    {
//        ApiDataService apiDataService = new ApiDataService(this.getContext());
//
//        apiDataService.stopSearchPassenger(MainActivity2.main_token, new ApiDataService.VolleyResponseListener() {
//
//            @Override
//            public void onError(Object message) {
//                System.out.println("Problem in stop search");
//            }
//
//            @Override
//            public void onResponse(Object responseObject) {
//
//                try {
//                    JSONObject responseData = new JSONObject(responseObject.toString());
//                    System.out.println(responseData);
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//            }
//        });
//    }

//    public void update_bottom_slider(String name, String phone)
//    {
//        bottom_text.setText("You have been matched with a passenger!\n" + "Name : " + name + "\nPhone : " + phone);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//        bottom_start.setVisibility(View.VISIBLE);
//        bottom_start.setEnabled(true);
//
//        bottom_cancel.setVisibility(View.VISIBLE);
//        bottom_cancel.setEnabled(true);
//
//    }
//
//
//    public void update_marker_passenger(double lon, double lat)
//    {
//
//        //Point destinationPoint = Point.fromLngLat(90.37609,23.83287);
//
//        passenger_long = lon;
//        passenger_lat = lat;
//
//
//        Point destinationPoint = Point.fromLngLat(lon,lat);
//
//        if(destinationPoint!=null)
//        {
//            Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                    locationComponent.getLastKnownLocation().getLatitude());
//            GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//            if (source != null) {
//                source.setGeoJson(Feature.fromGeometry(destinationPoint));
//            }
//
//            getRoute(originPoint, destinationPoint);
//
//            startButton.setText("Start Navigation");
//            startButton.setEnabled(true);
//            startButton.setBackgroundResource(R.color.mapboxBlue);
//
//            driverState = DriverState.PICKING;
//
//
//        }
//
//
//    }
//
//
//    public void reset_passenger()
//    {
//
//        bottom_text.setText("Welcome");
//        bottom_start.setVisibility(View.INVISIBLE);
//        bottom_start.setEnabled(false);
//
//        bottom_cancel.setVisibility(View.INVISIBLE);
//        bottom_cancel.setEnabled(false);
//
//
//
//        startButton.setText("Search Passenger");
//        driverState = DriverState.RESTING;
//
//    }
//
//
//    public int calculateDistanceInMeter(double userLat, double userLng, double venueLat, double venueLng)
//    {
//
//        double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
//
//        double latDistance = Math.toRadians(userLat - venueLat);
//        double lngDistance = Math.toRadians(userLng - venueLng);
//
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
//                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
//
//        double c = 2 * 1000 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
//        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
//    }






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



//package com.example.mapbox;
//
//        import androidx.annotation.NonNull;
//        import androidx.appcompat.app.AppCompatActivity;
//
//        import android.app.FragmentManager;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.view.View;
//
//        import com.google.android.gms.common.api.Status;
//        import com.google.android.gms.maps.model.LatLng;
//        import com.google.android.libraries.places.api.Places;
//        import com.google.android.libraries.places.api.model.Place;
//        import com.google.android.libraries.places.api.model.RectangularBounds;
//        import com.google.android.libraries.places.api.model.TypeFilter;
//        import com.google.android.libraries.places.api.net.PlacesClient;
//        import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
//        import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
//
//        import java.util.Arrays;
//
//public class MainActivity4 extends AppCompatActivity {
//    private static final String TAG = "MainActivity4";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main4);
//
//        Places.initialize(getApplicationContext(), getString(R.string.apiKey));
//        PlacesClient placesClient = Places.createClient(this);
//
//
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
//
////        autocompleteFragment.setLocationBias(RectangularBounds));
//        autocompleteFragment.setCountries("BD");
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
//                autocompleteFragment.getView().setVisibility(View.GONE);
//
//            }
//
//
//            @Override
//            public void onError(@NonNull Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
//
//
//
//
//
//
//
//
//
//
//    }
//}