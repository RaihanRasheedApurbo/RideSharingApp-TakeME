package com.example.TakeMEOwnerApp;

public class Vehicle {
    String vehicle_id, model;
    int capacity;
    String owner_id;
    int reg_no;
    String driver_id;

    public Vehicle(String vehicle_id, String model, int reg_no) {
        this.vehicle_id = vehicle_id;
        this.model = model;
        this.reg_no = reg_no;
    }

    public Vehicle()
    {

    }
}
