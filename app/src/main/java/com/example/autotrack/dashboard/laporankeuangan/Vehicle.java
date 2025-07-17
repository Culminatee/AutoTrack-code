package com.example.autotrack.dashboard.laporankeuangan;

public class Vehicle {
    private int id;
    private String model;
    private String plat;

    public Vehicle(int id, String model, String plat) {
        this.id = id;
        this.model = model;
        this.plat = plat;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getPlat() {
        return plat;
    }
}
