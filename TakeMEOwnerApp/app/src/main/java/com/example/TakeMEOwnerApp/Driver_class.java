package com.example.TakeMEOwnerApp;

public class Driver_class {

    String name;
    String id;
    Double income;
    Vehicle vehicle;
    double lat;
    double lang;
    String status ="";
    double destLat;
    double destLang;


    public Driver_class(String name, String id, double income, double lat, double lang) {
        this.name = name;
        this.id = id;
        this.income = income;
        this.lat = lat;
        this.lang = lang;
    }

    public void setDestCoord(double lat, double lang)
    {
        destLat = lat;
        destLang = lang;
    }


}
