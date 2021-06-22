package com.example.takemeuserapp;

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
     * A function for user login
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
    public void userLoginData(String email, String password, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/passenger/login/";

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

    //New changes from here
    /**
     * A function to show user dashboard
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
    public void getUserProfileData(String token, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/passenger/dashboard/";

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
     * A function that handles driverSearch
     * @param token String the token retrieved from login
     * @param requirement String preferable method(for now send "nearest")
     * @param pickUpLat Double pick up point latitude
     * @param pickUpLon Double pick up point longitude
     * @param dropLat Double destination latitude
     * @param dropLon Double destination longitude
     * @param volleyResponseListener Interface onResponse
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *
     *      if(responseData.has("driverInfo")) {
     *          JSONObject driverInfo = (JSONObject) responseData.get("driverInfo");
     *
     *          JSONArray driverLocation = (JSONArray) driverInfo.get("vehicleLocation");
     *
     *          double lat = Double.parseDouble(driverLocation.getString(0));
     *          double lon = Double.parseDouble(driverLocation.getString(1));
     *
     *          System.out.println("driverInfo: " + driverInfo);
     *          System.out.println("driverLocation: " + lat + " , " + lon);
     *      } else {
     *          message = responseData.get("message");
     *          //didn't find any preferable driver or the driver denied the ride
     *      }
     *
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void searchDriver(String token, String requirement, double pickUpLat, double pickUpLon, double dropLat, double dropLon, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/passenger/searchDriver/";

        double[] pickUpPoint = {pickUpLat, pickUpLon};
        double[] dropOutPoint = {dropLat, dropLon};

        Map<String,String> params = new HashMap<>();
        params.put("requirement", requirement);

        try {
            JSONArray pick = new JSONArray(Arrays.toString(pickUpPoint));
            JSONArray drop = new JSONArray(Arrays.toString(dropOutPoint));

            System.out.println("pick: "+ pick + " drop: " + drop);
            params.put("pickUpPoint", pick.toString());
            params.put("dropOutPoint", drop.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println(new JSONObject(params).toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                volleyResponseListener::onResponse,
                volleyResponseListener::onError) {
            @Override
            public Map<String, String> getHeaders() {
                return makeHeaders("auth-token", token);
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
    }

    /**
     * A function to cancel matched driver
     * @param token String the token retrieved from login
     * @param volleyResponseListener Interface
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      JSONObject responseData = new JSONObject(responseObject.toString());
     *      String message = (String) responseData.get('message');
     *      System.out.println(message);
     *
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void cancelMatch(String token, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/passenger/cancelMatch";

        Map<String,String> params = new HashMap<>();
        params.put("entity", "passenger");
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
     * A function to end ride with the riding driver
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
     *      //this contains ride info
     *      //duration is in seconds and
     *      //distance is in meters
     *      //time is in GMT+0
     *   } catch (JSONException e) {
     *      e.printStackTrace();
     *   }
     * }</pre>
     *
     */
    public void endRide(String token, double lat, double lon, VolleyResponseListener volleyResponseListener) {
        String url = BASE_URL + "/api/passenger/endRide";
        double[] location = {lat, lon};
        Map<String,String> params = new HashMap<>();
        params.put("entity", "passenger");
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
