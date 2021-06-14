package com.example.takemedriverapp;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    public Map<String, String> makeHeaders(String key, String value) {
        // below line we are creating a map for
        // storing our values in key and value pair.
        Map<String, String> params = new HashMap<>();

        // on below line we are passing our key
        // and value pair to our parameters.
        params.put(key, value);

        // at last we are
        // returning our params.
        return params;
    }

    /**
     * A function for driver login
     * @param email  String Example "robin@loa.com"
     * @param password String Example "thisisrobin"
     * @param volleyResponseListener Interface the onResponse method should be something like this
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *
     *      bodyData = (JSONObject) responseData.get("body");
     *      headerData = (JSONObject) responseData.get("headers");
     *
     *      System.out.println("body:" + bodyData);
     *      System.out.println("headers:" + headerData);
     *
     *      driverInfo = (JSONObject) bodyData.get("data");
     *      System.out.println("driverInfo:" + driverInfo);
     *
     *      //save the token somewhere
     *      token = (String) headerData.get("Auth-Token");   //if this doesn't work try printing the keys or using 'auth-token'
     *      System.out.println("token:" + token);
     *
     *  } catch (JSONException e) {
     *      e.printStackTrace();
     *  }
     *  }</pre>
     */
    public void driverLoginData(String email, String password, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/driver/login/";

        Map<String,String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        //System.out.println(new JSONObject(params).toString());

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
                    assert response.headers != null;
                    jsonResponse.put("headers", new JSONObject(response.headers));
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
    }

    /**
     * A function to show driver dashboard
     * @param token String the token retrieved from login
     * @param volleyResponseListener Interface onResponse you get plain object containing driver data
     * this method should be implemented following the given sample
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     * } catch (JSONException e) {
     *      e.printStackTrace();
     * }
     *                               }</pre>
     */
    public void getDriverProfileData(String token, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/driver/dashboard/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };


        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest);
    }


    /**
     * A function that handles passengerSearch
     * @param token String the token retrieved from login
     * @param volleyResponseListener Interface onResponse
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *
     *      if(responseData.has("passengerInfo")) {
     *          JSONObject passengerInfo = (JSONObject) responseData.get("passengerInfo");
     *
     *          JSONObject passengerData = (JSONObject) passengerInfo.get("passengerData");
     *          JSONArray pickUpPoint = (JSONArray) passengerInfo.get("pickUpPoint");
     *
     *          double lat = Double.parseDouble(pickUpPoint.getString(0));
     *          double lon = Double.parseDouble(pickUpPoint.getString(1));
     *
     *          System.out.println("passengerData: " + passengerData);
     *          System.out.println("pickUpPoint: " + lat + " , " + lon);
     *
     *          double destLat = Double.parseDouble(dropOutPoint.getString(0));
     *          double desLon = Double.parseDouble(dropOutPoint.getString(1));
     *
     *          System.out.println("passengerData: " + passengerData);
     *          System.out.println("pickUpPoint: " + destLat + " , " + desLon);
     *      } else {
     *          message = responseData.get("message");
     *          //first time or no match so nothing I guess
     *      }
     *
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */

    public void searchPassenger(String token, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/driver/search/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest);
    }


    /**
     * A function to stop passenger searching
     * @param token String the token retrieved from login
     * @param volleyResponseListener Interface
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */

    public void stopSearchPassenger(String token, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/driver/stopSearch/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest);
    }

    //New codes from here and added a comment before search passenger to get the destination coordinates
    /**
     * A function to cancel matched passenger
     * @param token String the token retrieved from login
     * @param volleyResponseListener Interface
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void cancelMatch(String token, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/driver/cancelMatch";

        Map<String,String> params = new HashMap<>();
        params.put("entity", "driver");
        //System.out.println(new JSONObject(params).toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                volleyResponseListener::onResponse,
                volleyResponseListener::onError){
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
    }

    /**
     * A function to start ride with matched passenger
     * @param token String the token retrieved from login
     * @param volleyResponseListener Interface
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void startRide(String token, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/driver/startRide";

        Map<String,String> params = new HashMap<>();
        params.put("entity", "driver");
        //System.out.println(new JSONObject(params).toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                volleyResponseListener::onResponse,
                volleyResponseListener::onError){
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
    }

    /**
     * A function to end ride with the riding passenger
     * @param token String the token retrieved from login
     * @param lat Double latitude of end ride point
     * @param lon Double longitude of end ride point
     * @param volleyResponseListener Interface
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void endRide(String token, double lat, double lon, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/driver/endRide";
        double[] location = {lat, lon};
        Map<String,String> params = new HashMap<>();
        params.put("entity", "driver");
        try {
            System.out.println(Arrays.toString(location));
            JSONArray p = new JSONArray(Arrays.toString(location));
            System.out.println(p);
            params.put("location", p.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println(new JSONObject(params).toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                volleyResponseListener::onResponse,
                volleyResponseListener::onError){
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
    }

    /**
     * A function to update driver's own location
     * @param token String the token retrieved from login
     * @param lat Double latitude
     * @param lon Double longitude
     * @param volleyResponseListener Interface
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void updateLocation(String token, double lat, double lon, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/driver/vehicle/location";

        double[] location = {lat, lon};

        Map<String,String> params = new HashMap<>();
        try {
            System.out.println(Arrays.toString(location));
            JSONArray p = new JSONArray(Arrays.toString(location));
            System.out.println(p);
            params.put("location", p.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println(new JSONObject(params).toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                volleyResponseListener::onResponse,
                volleyResponseListener::onError){
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
    }
}
