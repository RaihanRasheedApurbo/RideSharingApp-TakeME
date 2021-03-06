package com.example.takemeuserapp;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    public static String main_token = "";
    private static MainActivity instance;

    NavigationView navigationView;
    TextView textView_user_name, textView_user_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        //Fahad's************************************************

        login();

        //********************************************************


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


    public static MainActivity getInstance() {
        return instance;
    }

    public void login()
    {

        ApiDataService apiDataService = new ApiDataService(MainActivity.this);

        String email = "ivifo@visinas.st";
        final String[] name = { "" };
        apiDataService.userLoginData(email, "WXu25ufe" , new ApiDataService.VolleyResponseListener() {
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

                    JSONObject userInfo = (JSONObject) bodyData.get("data");
                    //System.out.println("xpx" + userInfo);

                    name[0] = (String)userInfo.get("name");

                    set_user_name_email(name[0], email);

                    // ********************************************************
                    // second api call if needed

                    // *********************************************************


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void set_user_name_email(String name, String email)
    {
        View header = navigationView.getHeaderView(0);

        textView_user_name = header.findViewById(R.id.user_name);
        textView_user_name.setText(name);

        textView_user_email = header.findViewById(R.id.user_email);
        textView_user_email.setText(email);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}