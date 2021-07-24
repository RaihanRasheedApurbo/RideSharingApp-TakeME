package com.example.takemeuserapp.ui.gallery;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takemeuserapp.ApiDataService;
import com.example.takemeuserapp.MainActivity;
import com.example.takemeuserapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    ListView listView_ride_history;

    private Geocoder geocoder;
    public static Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        listView_ride_history = root.findViewById(R.id.list_ride_history);

        ArrayList<RideHistory> rideHistories = new ArrayList<>();

        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.viewRideHistory(MainActivity.main_token, 7, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in GalleryFragment getting ride history");
            }

            @Override
            public void onResponse(Object responseObject)
            {

                //System.out.println("inside viewRideHistory in GalleryFragment");
                //System.out.println(responseObject);

                try{
                    System.out.println("inside viewRideHistory in GalleryFragment");
                    //System.out.println(responseObject);
                    JSONObject respone = new JSONObject(responseObject.toString());
                    JSONArray rides = new JSONArray(respone.getString("ride"));
                    //System.out.println(rides);
                    System.out.println("array length: "+rides.length());
                    for(int i=0;i<rides.length();i++)
                    {
                        JSONObject ride = rides.getJSONObject(i);

                        System.out.println(ride.getString("source")+" "+ride.getString("destination")+" "+ride.getString("fare")+" "+ride.getString("time"));
                        JSONArray sourceCoord = ride.getJSONArray("source");
                        JSONArray destCoord = ride.getJSONArray("destination");
//                        System.out.println(sourceCoord.get(0));
                        double sourceLat = Double.parseDouble(sourceCoord.getString(0));
                        double sourceLang = Double.parseDouble(sourceCoord.getString(1));
                        double destLat = Double.parseDouble(destCoord.getString(0));
                        double destLang = Double.parseDouble(destCoord.getString(1));
//                        System.out.println(Double.parseDouble(sourceCoord.getString(0)));
//                        System.out.println(geocoder.getFromLocation(sourceLat,sourceLang,1));
                        List<Address> sourceAddr = geocoder.getFromLocation(sourceLat,sourceLang,1);
                        List<Address> destAddr = geocoder.getFromLocation(destLat,destLang,1);
                        System.out.println(sourceAddr);
                        System.out.println(destAddr);
                        String sourceName = "Not Found";
                        String destName = "Not Found";
                        if(sourceAddr.size()>=1)
                        {
                            sourceName = sourceAddr.get(0).getThoroughfare();

                        }
                        if(destAddr.size()>=1)
                        {
                            destName = destAddr.get(0).getThoroughfare();

                        }
                        String[] dateTIme = ride.getString("time").split("T");
                        String date = dateTIme[0];
                        String[] timeFragment = dateTIme[1].split(":");

                        String time = timeFragment[0]+":"+timeFragment[1];
                        DecimalFormat df = new DecimalFormat("###.##");
                        double fare = Double.parseDouble(ride.getString("fare"));

                        fare = Double.parseDouble(df.format(fare));
                        System.out.println(sourceName+" "+destName+" "+fare+" "+date+" "+time);

                        rideHistories.add(new RideHistory(sourceName, destName, date,
                                time, fare));

                    }
                    RideHistoryAdapter adapter = new RideHistoryAdapter(GalleryFragment.context, R.layout.ride_history_list_items,
                            rideHistories);

                    listView_ride_history.setAdapter(adapter);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
        });

//        for (int i = 0; i < 10; i++) {
//            rideHistories.add(new RideHistory("Uttara", "Banani", "12/07/2021",
//                    "12:10", 220.71));
//        }

//        RideHistoryAdapter adapter = new RideHistoryAdapter(this.getContext(), R.layout.ride_history_list_items,
//                rideHistories);
//
//        listView_ride_history.setAdapter(adapter);


        return root;
    }
}