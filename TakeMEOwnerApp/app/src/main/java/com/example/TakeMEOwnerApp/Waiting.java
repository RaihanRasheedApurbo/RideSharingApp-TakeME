package com.example.TakeMEOwnerApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class Waiting extends AppCompatActivity {

    public static int SPLASH_TIME_OUT = 2500;
    JSONObject responseData, bodyData, headerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        login();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, SPLASH_TIME_OUT);


    }

    void login()
    {
        ApiDataService apiDataService = new ApiDataService(Waiting.this);

        apiDataService.getData("bruce@wayne.com", "iAmBatman" , new ApiDataService.VolleyResponseListener() {
            @Override
            public void onError(Object message) {
                System.out.println("xplication Error");
            }

            @Override
            public void onResponse(Object responseObject) {

                try {
                    responseData = new JSONObject(responseObject.toString());

                    bodyData = (JSONObject) responseData.get("body");
                    headerData = (JSONObject) responseData.get("headers");

                    MainActivity.main_token = (String) headerData.get("Auth-Token");
                    System.out.println("xps " + MainActivity.main_token);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


}