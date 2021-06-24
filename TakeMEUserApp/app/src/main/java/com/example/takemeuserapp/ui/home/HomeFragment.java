package com.example.takemeuserapp.ui.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.takemeuserapp.ApiDataService;
import com.example.takemeuserapp.MainActivity;
import com.example.takemeuserapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
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
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Looper.getMainLooper;
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

    //variables needed for threading

    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 10000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS;
    // Variables needed to listen to location updates
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);
    private MapboxNavigation mapboxNavigation;
    private Location prevDriverLocation;






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
    // Fahad's Variables
    FrameLayout frameLayout;
    BottomSheetBehavior bottomSheetBehavior;
    int time_spent = 0;
    ProgressDialog progressDialog;
    String driver_choice = "any";
    String car_choice = "any";
    TextView bottom_text, textView_estimated_fare;
    Button bottom_cancel, bottom_start_end, popup_confirm;
    PopupWindow popupWindow;
    ConstraintLayout constraintLayout;
    ImageButton popup_close;
    RadioGroup driver_selector_group, car_selector_group;
    RadioButton radioButton_driver_select, radioButton_car_select;
    LayoutInflater inflater1;
    double user_lat, user_long, dest_lat, dest_long, driver_lat, driver_long;
    double estimated_fare = 0.0;

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

                dest_lat = place.getLatLng().latitude;
                dest_long = place.getLatLng().longitude;

                updatePassengerDestination(place.getLatLng().longitude,place.getLatLng().latitude);
                autocompleteFragment.getView().setVisibility(View.GONE);
                        startButton.setText("Find Driver");
                        startButton.setEnabled(true);
                        startButton.setVisibility(View.VISIBLE);







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
        bottom_cancel = root.findViewById(R.id.bottom_start_button);
        bottom_start_end = root.findViewById(R.id.bottom_cancel_button);
        constraintLayout = root.findViewById(R.id.fragment_home_constraint);

        inflater1 = inflater;

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

                                cancel_match();
                                locationEngine.removeLocationUpdates(callback); // this should be stoped otherwise two callback will be present after restarting the activity
                                Intent intent = getActivity().getIntent();
                                getActivity().finish();
                                startActivity(intent);
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

        bottom_start_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userState == UserState.FINDING)
                {
                    userState = UserState.PICKING;
                    bottom_cancel.setVisibility(View.INVISIBLE);
                    bottom_cancel.setEnabled(false);
                    bottom_text.append("\nYour Driver is on the way!");
                    bottom_start_end.setText("End Ride");
                }
                else if(userState==UserState.PICKING || userState==UserState.RIDING)
                {
                    //bottom_text.setText("Welcome");
                    locationEngine.removeLocationUpdates(callback);
                    // after finishing the activity the location engine doesn't stop... so we need to stop it
                    // otherwise we will have two callback thread
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                    end_ride();
//                    userState = UserState.RESTING;
//                    bottom_cancel.setVisibility(View.INVISIBLE);
//                    bottom_cancel.setEnabled(false);
                }

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
                addDriverIconSymbolLayer(style);

                mapboxMap.addOnMapClickListener(HomeFragment.this);
                startButton = root.findViewById(R.id.startButton);
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(userState==UserState.RESTING)
                        {
                            System.out.println("searching");
                            user_long = locationComponent.getLastKnownLocation().getLongitude();
                            user_lat = locationComponent.getLastKnownLocation().getLatitude();

                            show_popup_window();

                        }

                    }
                });

            }
        });
    }


    public void show_popup_window()
    {

        //LayoutInflater layoutInflater = (LayoutInflater) root.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater1.inflate(R.layout.popup_driver_select,null);

        //instantiate popup window
        //popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupWindow = new PopupWindow(customView, 900, 1600);

        //display the popup window
        popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

        popup_close = customView.findViewById(R.id.imageButton_close_popup);
        popup_confirm = customView.findViewById(R.id.popup_button_confirm);

        driver_selector_group = customView.findViewById(R.id.radio_group);
        car_selector_group = customView.findViewById(R.id.radio_group_2);
        textView_estimated_fare = customView.findViewById(R.id.text_estimated_fare);

        //RadioGroup radioGroup = (RadioGroup) findViewById(R.id.yourRadioGroup);
        String temp = "Estimated Fare : ";

        driver_selector_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(checkedId == customView.findViewById(R.id.radioButton3).getId())
                    textView_estimated_fare.setText(temp + "300");
            }
        });

        car_selector_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(checkedId == customView.findViewById(R.id.radioButton_car_1).getId())
                    textView_estimated_fare.setText(temp + "200");
                else if(checkedId == customView.findViewById(R.id.radioButton_car_2).getId())
                    textView_estimated_fare.setText(temp + "400");
                else if(checkedId == customView.findViewById(R.id.radioButton_car_3).getId())
                    textView_estimated_fare.setText(temp + "600");
            }
        });

        // confirm button
        popup_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = driver_selector_group.getCheckedRadioButtonId();
                int selectedId2 = car_selector_group.getCheckedRadioButtonId();
                //radioButton_driver_select = (RadioButton)customView.findViewById(selectedId);

                if(selectedId == customView.findViewById(R.id.radioButton1).getId())
                {
                    driver_choice = "experienced";
                    System.out.println("Exp driver selected");
                }
                else if(selectedId == customView.findViewById(R.id.radioButton2).getId())
                {
                    driver_choice = "nearest";
                    System.out.println("Nearest driver selected");
                }
                else if(selectedId == customView.findViewById(R.id.radioButton3).getId())
                {
                    driver_choice = "new";
                    System.out.println("New driver selected");
                }

                if(selectedId2 == customView.findViewById(R.id.radioButton_car_1).getId())
                {
                    car_choice = "economy";
                    System.out.println("economy car selected");
                }
                else if(selectedId2 == customView.findViewById(R.id.radioButton_car_2).getId())
                {
                    car_choice = "budget";
                    System.out.println("budget car selected");
                }
                else if(selectedId2 == customView.findViewById(R.id.radioButton_car_3).getId())
                {
                    car_choice = "premium";
                    System.out.println("premium car selected");
                }

                popupWindow.dismiss();

                progressDialog = new ProgressDialog(HomeFragment.super.getContext());
                progressDialog.show();
                progressDialog.setContentView(R.layout.waiting_screen);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                time_spent = 0;
                userState = UserState.FINDING;
                api_call_driver_search();

            }
        });

        // cross button
        popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    public void updatePassengerDestination(double lon, double lat)
    {

        //Point destinationPoint = Point.fromLngLat(90.37609,23.83287);




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

//            startButton.setText("Start Navigation");
//            startButton.setEnabled(true);
//            startButton.setBackgroundResource(R.color.mapboxBlue);
//
//            userState = user.PICKING;


        }


    }

    public void updateDriverLocation(double lon, double lat)
    {

        //Point destinationPoint = Point.fromLngLat(90.37609,23.83287);




        Point destinationPoint = Point.fromLngLat(lon,lat);

        if(destinationPoint!=null)
        {
            Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                    locationComponent.getLastKnownLocation().getLatitude());
            GeoJsonSource source = mapboxMap.getStyle().getSourceAs("driver-source-id");
            if (source != null) {
                source.setGeoJson(Feature.fromGeometry(destinationPoint));
            }

//            getRoute(originPoint, destinationPoint);

//            startButton.setText("Start Navigation");
//            startButton.setEnabled(true);
//            startButton.setBackgroundResource(R.color.mapboxBlue);
//
//            userState = user.PICKING;


        }


    }


    public void api_call_driver_search()
    {

        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.searchDriver(MainActivity.main_token,  driver_choice,
                user_lat, user_long, dest_lat, dest_long,
            new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in finding Driver");
                userState = UserState.RESTING;
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Object responseObject)
            {

                try
                {
                    JSONObject responseData = new JSONObject(responseObject.toString());
                    //System.out.println(responseData);

                    if(responseData.has("driverInfo"))
                    {
                        JSONObject driverInfo = (JSONObject) responseData.get("driverInfo");
                        System.out.println(responseData);
                        JSONArray driverLocation = (JSONArray) driverInfo.get("vehicleLocation");

                        double lat = Double.parseDouble(driverLocation.getString(1));
                        double lon = Double.parseDouble(driverLocation.getString(0));

                        System.out.println("driverInfo: " + driverInfo);
                        System.out.println("driverLocation: " + lat + " , " + lon);

                        updateDriverLocation(lon, lat);

                        System.out.println(driverInfo.get("driverName"));
                        System.out.println(driverInfo.get("driverPhone"));

                        update_bottom_slider(driverInfo.get("driverName").toString(), driverInfo.get("driverPhone").toString());

                        progressDialog.dismiss();

                    }
                    else
                    {
                        String message = (String) responseData.get("message");
                        if(time_spent>12)
                        {
                            bottom_text.setText("No Driver Found!");
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            //stop_searching();
                            progressDialog.dismiss();
                            return;
                        }
                        time_spent += 3;
                        TimeUnit.SECONDS.sleep(3);
                        api_call_driver_search();
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


    public void fetchRideDetails()
    {

        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.searchDriver(MainActivity.main_token,  driver_choice,
                user_lat, user_long, dest_lat, dest_long,
                new ApiDataService.VolleyResponseListener() {

                    @Override
                    public void onError(Object message) {
                        System.out.println("fetching driver info");
                        System.out.println("Problem in finding Driver");
//                        userState = UserState.RESTING;
//                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(Object responseObject)
                    {


                    try{

                        JSONObject responseData = new JSONObject(responseObject.toString());
                        //System.out.println(responseData);

                        if(responseData.has("driverInfo"))
                        {
                            JSONObject driverInfo = (JSONObject) responseData.get("driverInfo");
                            //System.out.println(responseData);
                            JSONArray driverLocation = (JSONArray) driverInfo.get("vehicleLocation");
                            //System.out.println(responseData.getString("status"));
                            String status = responseData.getString("status");
                            double lat = Double.parseDouble(driverLocation.getString(1));
                            double lon = Double.parseDouble(driverLocation.getString(0));
                            Location currentDriverLocation = new Location("");
                            currentDriverLocation.setLatitude(lat);
                            currentDriverLocation.setLongitude(lon);
                            //System.out.println("driverInfo: " + driverInfo);
                            //System.out.println("driverLocation: " + lat + " , " + lon);

                            if(prevDriverLocation != null)
                            {
                                boolean notNear = Math.abs(prevDriverLocation.getLatitude()-currentDriverLocation.getLatitude()) > 0.001 || Math.abs(prevDriverLocation.getLongitude()-currentDriverLocation.getLongitude()) > 0.001;

                                if(notNear)
                                {
                                    prevDriverLocation = currentDriverLocation;
                                    updateDriverLocation(lon, lat);
                                }

                            }
                            else
                            {
                                prevDriverLocation = currentDriverLocation;
                            }


                            if(status.contains("riding") && userState!=UserState.RIDING)
                            {
                                System.out.println("state synchronization is happening in fetch ride details");
//                                bottom_cancel.setText("Cancel Ride");
                                bottom_cancel.setVisibility(View.INVISIBLE);
                                bottom_cancel.setEnabled(false);
                                userState = UserState.RIDING;

                                bottom_start_end.setVisibility(View.VISIBLE);
                                bottom_start_end.setEnabled(true);
                                bottom_start_end.setText("End Ride");

                            }

                            if(status.contains("ended"))
                            {
                                System.out.println("ride ended");
                                // fahad show a popup window where that will say to user that the ride has eneded and he or she
                                // have to pay 50 tk or something like that.....
                                locationEngine.removeLocationUpdates(callback); // this should be stoped otherwise two callback will be present after restarting the activity
                                Intent intent = getActivity().getIntent();
                                getActivity().finish();
                                startActivity(intent);
                            }
                            if(status.contains("denied"))
                            {
                                System.out.println("canceling ride after driver canceled");
                                // fahad show a popup here and then do something like below
                                 // this should be stoped otherwise two callback will be present after restarting the activity

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                locationEngine.removeLocationUpdates(callback);
                                                Intent intent = getActivity().getIntent();
                                                getActivity().finish();
                                                startActivity(intent);
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Sorry!\nRide cancelled by driver").setPositiveButton("ok", dialogClickListener).show();


                            }
                        }



//                            if(responseData.has("driverInfo"))
//                            {
//                                JSONObject driverInfo = (JSONObject) responseData.get("driverInfo");
//
//                                JSONArray driverLocation = (JSONArray) driverInfo.get("vehicleLocation");
//
//                                double lat = Double.parseDouble(driverLocation.getString(1));
//                                double lon = Double.parseDouble(driverLocation.getString(0));
//
//                                System.out.println("driverInfo: " + driverInfo);
//                                System.out.println("driverLocation: " + lat + " , " + lon);
//
//                                updateDriverLocation(lon, lat);
//
//                                System.out.println(driverInfo.get("driverName"));
//                                System.out.println(driverInfo.get("driverPhone"));
//
//                                update_bottom_slider(driverInfo.get("driverName").toString(), driverInfo.get("driverPhone").toString());
//
//                                progressDialog.dismiss();
//
//                            }
//                            else
//                            {
//                                String message = (String) responseData.get("message");
//                                if(time_spent>12)
//                                {
//                                    bottom_text.setText("No Driver Found!");
//                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                                    //stop_searching();
//                                    progressDialog.dismiss();
//                                    return;
//                                }
//                                time_spent += 3;
//                                TimeUnit.SECONDS.sleep(3);
//                                api_call_driver_search();
//                                //first time or no match so nothing I guess
//                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });


    }


    public void update_bottom_slider(String name, String phone)
    {
        bottom_text.setText("You have been matched with a driver!\n" + "Name : " + name + "\nPhone : " + phone);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


//        bottom_start_end.setVisibility(View.VISIBLE);
//        bottom_start_end.setEnabled(true);
//        bottom_start_end.setText("Confirm Driver");

        userState = UserState.PICKING;

        bottom_cancel.setText("Cancel Ride");
        bottom_cancel.setVisibility(View.VISIBLE);
        bottom_cancel.setEnabled(true);

    }

    public void cancel_match()
    {
        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.cancelMatch(MainActivity.main_token, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in cancel ride");
            }

            @Override
            public void onResponse(Object responseObject) {

                try {
                    JSONObject responseData = new JSONObject(responseObject.toString());
                    System.out.println("Ride Cancelled by user");
                    System.out.println(responseData);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    public void end_ride() {
        ApiDataService apiDataService = new ApiDataService(this.getContext());
        double end_lat = locationComponent.getLastKnownLocation().getLatitude();
        double end_long = locationComponent.getLastKnownLocation().getLongitude();
        apiDataService.endRide(MainActivity.main_token, end_lat, end_long, new ApiDataService.VolleyResponseListener() {

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

    }
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
                BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker_light));
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

    private void addDriverIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("driver-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker_dark));
        GeoJsonSource geoJsonSource = new GeoJsonSource("driver-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("driver-symbol-layer-id", "driver-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("driver-icon-id"),
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
        System.out.println("initing location engine");
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

//                System.out.println("kill meh");
                //System.out.println("userstate: " + fragment.userState);
                if (fragment.userState == UserState.FINDING)
                {

                }
                else if(fragment.userState == UserState.PICKING)
                {
                    fragment.fetchRideDetails();
                }
                else if(fragment.userState == UserState.RIDING)
                {
                    fragment.fetchRideDetails();
                }
                else if(fragment.userState == UserState.RESTING)
                {

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