package com.example.takemedriverapp;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static String main_token = "";
    private static MainActivity2 instance;

    NavigationView navigationView;
    TextView textView_driver_name, textView_driver_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        instance = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        //Fahad's************************************************

        login();


        //********************************************************


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    public static MainActivity2 getInstance() {
        return instance;
    }

    public void login()
    {

        ApiDataService apiDataService = new ApiDataService(MainActivity2.this);

        String email = "robin@loa.com";
        final String[] name = { "" };
        apiDataService.driverLoginData(email, "thisisrobin" , new ApiDataService.VolleyResponseListener() {
            @Override
            public void onError(Object message) {
                System.out.println("xplication Error");
            }

            @Override
            public void onResponse(Object responseObject) {

                try {
                    JSONObject responseData = new JSONObject(responseObject.toString());

                    JSONObject bodyData = (JSONObject) responseData.get("body");
                    JSONObject headerData = (JSONObject) responseData.get("headers");

                    main_token = (String) headerData.get("Auth-Token");
                    System.out.println("xps " + main_token);

                    JSONObject driverInfo = (JSONObject) bodyData.get("data");
                    name[0] = (String)driverInfo.get("name");

                    set_driver_name_email(name[0], email);
                    //System.out.println("driverInfo:" + driverInfo.get("name"));


                    // ********************************************************

                    //api_call2();

                    // *********************************************************


                    //finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    public void set_driver_name_email(String name, String email)
    {
        View header = navigationView.getHeaderView(0);

        textView_driver_name = header.findViewById(R.id.driver_name);
        textView_driver_name.setText(name);

        textView_driver_email = header.findViewById(R.id.driver_email);
        textView_driver_email.setText(email);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}