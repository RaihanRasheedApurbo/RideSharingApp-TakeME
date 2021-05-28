package com.example.takemedriverapp;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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

    /**
     *
     * @param email like "robin@loa.com"
     * @param password like "thisisrobin"
     * @param volleyResponseListener the onResponse method should be something like this
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
     *
     * @param token the token retrieved from login
     * @param volleyResponseListener onResponse you get plain object containing driver data
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
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("auth-token", token);

                // at last we are
                // returning our params.
                return params;
            }
        };


        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     *
     * @param token the token retrieved from login
     * @param volleyResponseListener onResponse
     * this method should be implemented following the given sample
     *
     * <pre>
     * {@code
     *  try {
     *      responseData = new JSONObject(responseObject.toString());
     *      System.out.println(responseData);
     *
     *      if(responseData.has("passengerInfo")) {
     *          passengerInfo = (JSONObject) responseData.get("passengerInfo");
     *          System.out.println(passengerInfo);
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
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("auth-token", token);

                // at last we are
                // returning our params.
                return params;
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     *
     * @param token the token retrieved from login
     * @param volleyResponseListener
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
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("auth-token", token);

                // at last we are
                // returning our params.
                return params;
            }
        };

        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * A function that matches a custom passenger to the given driverID and removes the driver from the pool
     * @param driverID driverID
     * @param passengerID passengerID like "607478178c29c1408cfad290"
     * @param volleyResponseListener onResponse
     * I don't think this is needed :3
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
    public void customMatching(String apiRoute, String passengerID, String driverID, VolleyResponseListener volleyResponseListener) {

        // Request a string response from the provided URL.
        //String url = LOCAL_URL + "/api/dummy/owner/reqTest";
        String url = BASE_URL + "/api/passenger/accept" + "?passengerID=" + passengerID + "&driverID=" + driverID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                volleyResponseListener::onResponse,
                volleyResponseListener::onError);

        /*StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
        };*/

        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest);
    }

}
