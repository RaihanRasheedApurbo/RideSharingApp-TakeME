package com.example.TakeMEOwnerApp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

public class fragment_3 extends Fragment {

    public static View root = null;

    public fragment_3()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_3, container, false);

        PieChart pieChart = root.findViewById(R.id.pie_chart);

        ApiDataService apiDataService = new ApiDataService(this.getContext());

        apiDataService.viewRideHistory(MainActivity.main_token, 30, new ApiDataService.VolleyResponseListener() {

            @Override
            public void onError(Object message) {
                System.out.println("Problem in fragment3 getting ride history");
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Object responseObject)
            {

                try{
                    System.out.println("inside viewRideHistory in fragment3");
                    System.out.println(responseObject);
                    JSONObject respone = new JSONObject(responseObject.toString());
                    JSONArray rides = new JSONArray(respone.getString("ride"));
                    System.out.println(rides);
                    System.out.println("array length: "+rides.length());



                    HashMap<String, Integer> map = new HashMap<>();
                    LocalDate today = LocalDate.now();
                    String oldDate = today.toString();
//                    System.out.println("Date before Addition: "+oldDate);
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
                    for(int i=0;i<30-1;i++)
                    {
                        c.add(Calendar.DAY_OF_MONTH, -1);
                        newDate = sdf.format(c.getTime());
                        map.put(newDate,0);
//                        System.out.println("Date after Addition: "+newDate);

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
                    String[] sortedDates = new String[30];
                    int front = 0;
                    for (Map.Entry me : map.entrySet()) {
//                        System.out.println("Key: "+me.getKey() + " & Value: " + me.getValue());
                        sortedDates[front++] = (String) me.getKey();
                        if(front==30)
                        {
                            break;
                        }
                    }


                    Arrays.sort(sortedDates);
                    int[] weeks = new int[4];
                    int index = sortedDates.length-1;
                    ArrayList<PieEntry> income = new ArrayList<>();
                    for(int i=0;i<4;i++)
                    {
                        weeks[i] = 0;
                        for(int j=0;j<7;j++)
                        {
                            weeks[i]+= map.get(sortedDates[index--]);
                        }
                        System.out.println("week :"+i+" "+weeks[i]);
                        income.add(new PieEntry(weeks[i], "Week "+(i+1)));
                    }


//                    ArrayList<PieEntry> income = new ArrayList<>();
//
//                    income.add(new PieEntry(2040, "Week 1"));
//                    income.add(new PieEntry(1870, "Week 2"));
//                    income.add(new PieEntry(3110, "Week 3"));
//                    income.add(new PieEntry(1590, "Week 4"));
//
                    PieDataSet pieDataSet = new PieDataSet(income, "Income of Last Month");
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    pieDataSet.setValueTextColor(Color.BLACK);
                    pieDataSet.setValueTextSize(16f);

                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setCenterText("Income of Last Month");
                    pieChart.animate();
//                    for(int i=sortedDates.length-1;i>=0;i--)
//                    {
//                        System.out.println(sortedDates[i]);
////                        barEntries.add(new BarEntry((float) i+1,map.get(sortedDates[i])));
//
//                    }





//                    BarDataSet barDataSet = new BarDataSet(barEntries, "Income");
//                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//                    barDataSet.setValueTextColor(Color.BLACK);
//                    barDataSet.setValueTextSize(16f);
//
//                    BarData barData = new BarData(barDataSet);
//                    barChart.setFitBars(true);
//                    barChart.setData(barData);
//                    barChart.getDescription().setText("Last 7 days' income");
//                    barChart.animateY(2000);

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