package com.example.autotrack.fuel;

public class FuelLog {
    private int id;
    private String date;
    private double pricePerLiter;
    private double totalPay;
    private double liters;
    private double odometer;

    public FuelLog(int id, String date, double pricePerLiter, double totalPay, double liters, double odometer) {
        this.id = id;
        this.date = date;
        this.pricePerLiter = pricePerLiter;
        this.totalPay = totalPay;
        this.liters = liters;
        this.odometer = odometer;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public double getPricePerLiter() { return pricePerLiter; }
    public double getTotalPay() { return totalPay; }
    public double getLiters() { return liters; }
    public double getOdometer() { return odometer; }

    public void setId(int id) {
        this.id = id;
    }
}

