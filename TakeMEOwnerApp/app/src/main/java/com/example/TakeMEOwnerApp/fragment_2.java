package com.example.TakeMEOwnerApp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
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

        BarChart barChart = root.findViewById(R.id.barchart_seven_days_income22);

        //TextView textView = root.findViewById(R.id.frag2_text);
        //textView.setText("meaw");
        ApiDataService apiDataService = new ApiDataService(this.getContext());
        int vehicleLength = MainActivity.getInstance().vehicles.size();
        String vechicleID = MainActivity.getInstance().vehicles.get(0).getVehicle_id();
        System.out.println("vechicle ID :   "+vechicleID+" len: "+vehicleLength);
        apiDataService.viewRideHistory(MainActivity.main_token, vechicleID, 7, new ApiDataService.VolleyResponseListener() {

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

//        ArrayList<BarEntry> barEntries = new ArrayList<>();
//
//        barEntries.add(new BarEntry(1f, 220));
//        barEntries.add(new BarEntry(2f, 320));
//        barEntries.add(new BarEntry(3f, 420));
//        barEntries.add(new BarEntry(4f, 280));
//        barEntries.add(new BarEntry(5f, 340));
//        barEntries.add(new BarEntry(6f, 400));
//        barEntries.add(new BarEntry(7f, 200));
//
//        BarDataSet barDataSet = new BarDataSet(barEntries, "Income");
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(16f);
//
//        BarData barData = new BarData(barDataSet);
//        barChart.setFitBars(true);
//        barChart.setData(barData);
//        barChart.getDescription().setText("Last 7 days' income");
//        barChart.animateY(2000);

        System.out.println("mewaw");

        return root;
    }
}