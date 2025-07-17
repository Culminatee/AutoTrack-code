package com.example.autotrack.dashboard.registrasikendaraan;

public class KendaraanModel {
    private String tipe, merk, model, plat;
    private boolean isAddButton = false;

    // Konstruktor kendaraan biasa
    public KendaraanModel(String tipe, String merk, String model, String plat) {
        this.tipe = tipe;
        this.merk = merk;
        this.model = model;
        this.plat = plat;
        this.isAddButton = false;
    }

    // Konstruktor untuk tombol tambah
    public KendaraanModel(boolean isAddButton) {
        this.tipe = "";
        this.merk = "";
        this.model = "";
        this.plat = "";
        this.isAddButton = isAddButton;
    }

    public String getTipe() {
        return tipe;
    }

    public String getMerk() {
        return merk;
    }

    public String getModel() {
        return model;
    }

    public String getPlat() {
        return plat;
    }

    public boolean isAddButton() {
        return isAddButton;
    }
}


