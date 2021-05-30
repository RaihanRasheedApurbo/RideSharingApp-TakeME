package com.example.TakeMEOwnerApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Waiting extends AppCompatActivity {

    public static int SPLASH_TIME_OUT = 1500;
    JSONObject responseData, bodyData, headerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        login();
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


                    // ********************************************************

                    api_call2();

                    // *********************************************************


                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    void api_call2()
    {
        ApiDataService apiDataService2 = new ApiDataService(Waiting.this);
        final JSONObject[] responseData = new JSONObject[1];
        final String[] owner_name = new String[1];
        final String[] owner_email = new String[1];


        apiDataService2.getProfileData(MainActivity.main_token, new ApiDataService.VolleyResponseListener() {
            @Override
            public void onError(Object message) {
                System.out.println("Error in Api Call 2");
            }

            @Override
            public void onResponse(Object responseObject) {
                try {
                    responseData[0] = new JSONObject(responseObject.toString());
                    owner_name[0] = (String) responseData[0].get("name");
                    owner_email[0] = (String) responseData[0].get("email");
                    System.out.println("hello" + owner_name[0]);
                    System.out.println("hello" + owner_email[0]);

                    MainActivity.getInstance().set_owner_data(owner_name[0], owner_email[0]);
                    System.out.println("uiui calling api 3");
                    api_call3();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    void api_call3()
    {
        ApiDataService apiDataService2 = new ApiDataService(Waiting.this);
        final JSONArray[] responseData = {new JSONArray()};


        apiDataService2.getVehicles(MainActivity.main_token, new ApiDataService.VolleyResponseListener() {
            @Override
            public void onError(Object message) {
                System.out.println("Error in Api Call 3");
            }

            @Override
            public void onResponse(Object responseObject) {
                try {

                    responseData[0] = new JSONArray(responseObject.toString());
                    for (int i = 0; i < responseData[0].length(); i++) {
                        String model = (String)new JSONObject(responseData[0].get(i).toString()).get("model");
                        String type = model + " " + (String)new JSONObject(responseData[0].get(i).toString()).get("type");
                        String regno = (String) new JSONObject(responseData[0].get(i).toString()).get("regNo");
                        String id = (String)new JSONObject(responseData[0].get(i).toString()).get("_id");
                        String driver_id = (String)new JSONObject(responseData[0].get(i).toString()).get("driverID");

                        //System.out.println("uiui "+ driver_id);

                        api_call_vehicle_info(id, model, type, regno, driver_id);
                    }

                    MainActivity.getInstance().update_bottom_slider();



                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    void api_call_vehicle_info(String vehichleId, String model, String type, String regno, String driver_id )
    {
        ApiDataService apiDataService2 = new ApiDataService(Waiting.this);
        final JSONObject[] responseData = new JSONObject[1];
        final JSONArray[] responseData2 = {new JSONArray()};

        apiDataService2.getVehicleInfo(vehichleId, MainActivity.main_token, new ApiDataService.VolleyResponseListener() {
            @Override
            public void onError(Object message) {
                System.out.println("Error in Api Call 4");
            }

            @Override
            public void onResponse(Object responseObject) {
                try {

                    double driver_income = 0.0;
                    responseData[0] = new JSONObject(responseObject.toString());
                    responseData2[0] = new JSONArray(responseData[0].get("data").toString());
                    String driver_name = new JSONObject(responseData2[0].get(0).toString()).get("name").toString();
                    //System.out.println(new JSONArray(responseData2[0].get(2).toString()).toString() );//get("total")

                    if(new JSONArray(responseData2[0].get(2).toString()).length()>0)
                        driver_income = new JSONObject(new JSONArray(responseData2[0].get(2).toString()).get(0).toString()).getDouble("total");
                    //System.out.println(new JSONArray(responseData2[0].get(2).toString()).length());
                    System.out.println("Driver Income " + driver_income);

                    MainActivity.getInstance().add_vehicle(vehichleId ,type, new Integer(regno).intValue());
                    MainActivity.getInstance().add_driver(driver_name, driver_id, driver_income);

                    MainActivity.getInstance().update_bottom_slider();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}