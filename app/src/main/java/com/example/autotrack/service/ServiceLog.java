package com.example.autotrack.service;

public class ServiceLog {
    private int id, totalCost;
    private String date;
    private int odometer, nextOliMesin, nextOliGardan, nextCVT, nextKampasRem, nextFilterUdara, nextAirRadiator;

    public ServiceLog(int id, String date, int odometer, int nextOliMesin, int nextOliGardan, int nextCVT,
                      int nextKampasRem, int nextFilterUdara, int nextAirRadiator, int totalCost) {
        this.id = id;
        this.date = date;
        this.odometer = odometer;
        this.nextOliMesin = nextOliMesin;
        this.nextOliGardan = nextOliGardan;
        this.nextCVT = nextCVT;
        this.nextKampasRem = nextKampasRem;
        this.nextFilterUdara = nextFilterUdara;
        this.nextAirRadiator = nextAirRadiator;
        this.totalCost = totalCost;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public int getOdometer() { return odometer; }
    public int getTotalCost() {
        return totalCost;
    }
    public int getNextOliMesin() { return nextOliMesin; }
    public int getNextOliGardan() { return nextOliGardan; }
    public int getNextCVT() { return nextCVT; }
    public int getNextKampasRem() { return nextKampasRem; }
    public int getNextFilterUdara() { return nextFilterUdara; }
    public int getNextAirRadiator() { return nextAirRadiator; }
}
