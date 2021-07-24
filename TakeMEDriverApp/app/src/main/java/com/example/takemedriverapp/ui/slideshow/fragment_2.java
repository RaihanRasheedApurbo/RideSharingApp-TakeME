package com.example.takemedriverapp.ui.slideshow;

import android.graphics.Color;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.takemedriverapp.ApiDataService;
import com.example.takemedriverapp.MainActivity2;
import com.example.takemedriverapp.R;
import com.example.takemedriverapp.ui.gallery.GalleryFragment;
import com.example.takemedriverapp.ui.gallery.RideHistory;
import com.example.takemedriverapp.ui.gallery.RideHistoryAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.util.ArrayUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_2 extends Fragment {
    public static View root = null;
    public fragment_2()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_2, container, false);

        //fragment_2.this.getView().findViewById(R.id.barchart_seven_days_income22)
        //root.findViewById(R.id.barchart_seven_days_income22);
//        final Fragment fragment_2 = new fragment_2();
        
        //BarChart barChart = root.findViewById(R.id.barchart_seven_days_income22);
//        if(fragment_2 == null)
//            System.out.println("this is nulllllll");
//        else
//            System.out.println("not nulllllll");

        BarChart barChart = root.findViewById(R.id.barchart_seven_days_income22);

        //TextView textView = root.findViewById(R.id.frag2_text);
        //textView.setText("meaw");

        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.viewRideHistory(MainActivity2.main_token, 7, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in fragment2 getting ride history");
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Object responseObject)
            {

                try{
                    System.out.println("inside viewRideHistory in fragment2");
                    System.out.println(responseObject);
                    JSONObject respone = new JSONObject(responseObject.toString());
                    JSONArray rides = new JSONArray(respone.getString("ride"));
                    System.out.println(rides);
                    System.out.println("array length: "+rides.length());



                    HashMap<String, Integer> map = new HashMap<>();
                    LocalDate today = LocalDate.now();
                    String oldDate = today.toString();
                    System.out.println("Date before Addition: "+oldDate);
                    //Specifying date format that matches the given date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    try{
                        //Setting the date to the given date
                        c.setTime(sdf.parse(oldDate));
                    }catch(ParseException e){
                        e.printStackTrace();
                    }

                    //Number of Days to add
                    String newDate = sdf.format(c.getTime());
                    map.put(newDate,0);
                    for(int i=0;i<6;i++)
                    {
                        c.add(Calendar.DAY_OF_MONTH, -1);
                        newDate = sdf.format(c.getTime());
                        map.put(newDate,0);
                        System.out.println("Date after Addition: "+newDate);

                    }




                    for(int i=0;i<rides.length();i++)
                    {
                        JSONObject ride = rides.getJSONObject(i);
//
//
                        String[] dateTIme = ride.getString("time").split("T");
                        String date = dateTIme[0];

//                        String[] timeFragment = dateTIme[1].split(":");
//
//                        String time = timeFragment[0]+":"+timeFragment[1];
                        DecimalFormat df = new DecimalFormat("###.##");
                        double fare = Double.parseDouble(ride.getString("fare"));

                        fare = Double.parseDouble(df.format(fare));

                        if(map.containsKey(date))
                        {
                            Integer t = map.get(date);
                            t += (int) fare;
                            map.put(date,t);
                        }
                        else
                        {
                            map.put(date,(int)fare);
                        }
//                        System.out.println(sourceName+" "+destName+" "+fare+" "+date+" "+time);
//
//                        rideHistories.add(new RideHistory(sourceName, destName, date,
//                                time, fare));

                    }
                    String[] sortedDates = new String[7];
                    int front = 0;
                    for (Map.Entry me : map.entrySet()) {
                        System.out.println("Key: "+me.getKey() + " & Value: " + me.getValue());
                        sortedDates[front++] = (String) me.getKey();
                        if(front==7)
                        {
                            break;
                        }
                    }


                    Arrays.sort(sortedDates);
                    ArrayList<BarEntry> barEntries = new ArrayList<>();

                    for(int i=sortedDates.length-1;i>=0;i--)
                    {
                        System.out.println(sortedDates[i]);
                        barEntries.add(new BarEntry((float) i+1,map.get(sortedDates[i])));

                    }





                    BarDataSet barDataSet = new BarDataSet(barEntries, "Income");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData(barDataSet);
                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Last 7 days' income");
                    barChart.animateY(2000);

//                    System.out.println("mewaw");


//                    RideHistoryAdapter adapter = new RideHistoryAdapter(GalleryFragment.context, R.layout.ride_history_list_items,
//                            rideHistories);
//
//                    listView_ride_history.setAdapter(adapter);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
        });



        return root;
    }
}