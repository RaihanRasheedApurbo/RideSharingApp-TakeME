package com.example.TakeMEOwnerApp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ApiDataService {
    protected Context context;
    public static final String BASE_URL = "https://take-me-backend.herokuapp.com";
    public static final String LOCAL_URL = "http://192.168.1.4:3000";

    public ApiDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(Object message);

        void onResponse(Object responseObject);
    }
    public void login(String email, String password, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/owner/login";

        Map<String,String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        String requestBody = new JSONObject(params).toString();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                volleyResponseListener::onResponse,
                volleyResponseListener::onError){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("body", new JSONObject(jsonString));
                    jsonResponse.put("headers", new JSONObject(response.headers));
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };


        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getProfileData(String token, VolleyResponseListener volleyResponseListener)
    {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/owner/dashboard";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("auth-token", token);

                // at last we are
                // returning our params.
                return params;
            }
        };


        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void getVehicles(String token, VolleyResponseListener volleyResponseListener)
    {
        String url = BASE_URL + "/api/owner/vehicles";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("auth-token", token);

                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // /api/owner/vehicle/id/{id}?driver={driverValue}

    public void getVehicleInfo(String id, String token, VolleyResponseListener volleyResponseListener)
    {
        String url = BASE_URL + "/api/owner/vehicle/id/" + id + "?driver=true&duration=360";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("auth-token", token);

                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * A function to view Driver current location and his status
     * @param token String token from login auth
     * @param vehicleID String vehicleID of the vehicle to monitor
     * @param volleyResponseListener interface to handle response
     *                               An example of how to call this function is given below
     * <pre>
     * {@code
     *  apiDataService.viewDriver(token, driverID, new ApiDataService.VolleyResponseListener() {
     *                     @Override
     *                     public void onError(Object message) {
     *                         System.out.println(message);
     *                     }
     *
     *                     @Override
     *                     public void onResponse(Object responseObject) {
     *                         try {
     *                             responseData = new JSONObject(responseObject.toString());
     *                             System.out.println(responseData);
     *
     *                             //change here to handle these info
     *                             System.out.println(responseData.getString("vehicleInfo"));
     *                             System.out.println(responseData.getString("driverInfo"));
     *                             System.out.println(responseData.getString("vehicleLocation")); //this is an array from javascript side so I haven't checked how to handle it in java
     *                             if (responseData.has("status")) System.out.println(responseData.getString("status"));  //status will be undefined for inactive driver
     *                             if (responseData.has("passengerInfo")) System.out.println(responseData.getString("passengerInfo")); //passengerInfo will be undefined for driver in searching state
     *
     *                         } catch (JSONException e) {
     *                             e.printStackTrace();
     *                         }
     *                     }
     *                 });
     *  }</pre>
     */
    public void viewDriver(String token, String vehicleID, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/owner/vehicle/id/" + vehicleID + "/status";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("auth-token", token);

                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    /**
     * A function to view ride history
     * @param token String the token retrieved from login
     * @param vehicleID String vehicleID of the vehicle
     * @param duration int duration in days upto how much history is required
     * @param volleyResponseListener Interface
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      JSONObject responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *      JSONArray rides = (JSONArray) responseData.get('ride');
     *      int count = (int) responseData.get('count');
     *      double total = (double) responseData.get('total');
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void viewRideHistory(String token, String vehicleID, int duration, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/owner/vehicle/id/" + vehicleID + "/rideHistory";
        if (duration != -1) url = url + "?duration="+ duration;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError){
           @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("auth-token", token);

                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

}
