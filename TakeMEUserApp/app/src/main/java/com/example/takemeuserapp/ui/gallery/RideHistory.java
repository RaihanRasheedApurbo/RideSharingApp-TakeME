package com.example.takemeuserapp.ui.gallery;

public class RideHistory {
    String source, destination;
    String date, time;
    Double fare;

    public RideHistory(String source, String destination, String date, String time, Double fare) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.fare = fare;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return time + " " + date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public void setTime(String time) {
        this.time = time;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }
}
