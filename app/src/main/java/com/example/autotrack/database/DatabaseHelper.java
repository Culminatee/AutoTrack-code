package com.example.autotrack.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.autotrack.dashboard.laporankeuangan.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dbkel8";
    public static final int DATABASE_VERSION = 5;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "email TEXT)");

        db.execSQL("CREATE TABLE vehicles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "tipe TEXT, " +
                "merk TEXT, " +
                "model TEXT, " +
                "plat TEXT, " +
                "kapasitas_bbm INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))");

        db.execSQL("CREATE TABLE fuel_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "vehicle_id INTEGER, " +
                "date TEXT, " +
                "price_per_liter REAL, " +
                "liters REAL, " +
                "total_cost REAL, " +
                "odometer INTEGER, " +
                "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id))");

        db.execSQL("CREATE TABLE service_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "vehicle_id INTEGER, " +
                "last_service_odometer INTEGER, " +
                "next_oli_mesin INTEGER, " +
                "next_oli_gardan INTEGER, " +
                "next_cvt INTEGER, " +
                "next_kampas_rem INTEGER, " +
                "next_filter_udara INTEGER, " +
                "next_air_radiator INTEGER, " +
                "total_cost INTEGER, " +
                "date TEXT, " +
                "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id))");

        db.execSQL("CREATE TABLE notifikasi (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "tipe TEXT, " +
                "beban TEXT, " +
                "tanggal TEXT, " +
                "catatan TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS fuel_logs");
        db.execSQL("DROP TABLE IF EXISTS service_logs");
        db.execSQL("DROP TABLE IF EXISTS vehicles");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS notifikasi");
        onCreate(db);
    }


    // =========================
    // USER LOGIN / REGISTER
    // =========================

    public boolean insertUser(String username, String password, String firstName, String lastName, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password}
        );
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int result = db.update("users", values, "username = ?", new String[]{username});
        return result > 0;
    }


    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT first_name, last_name, email FROM users WHERE username = ?", new String[]{username});
    }

    // =========================
    // VEHICLES
    // =========================

    public boolean insertVehicle(int userId, String tipe, String merk, String model, String plat, double kapasitasBBM) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("tipe", tipe);
        values.put("merk", merk);
        values.put("model", model);
        values.put("plat", plat);
        values.put("kapasitas_bbm", kapasitasBBM);
        long result = db.insert("vehicles", null, values);
        return result != -1;
    }

    public Cursor getVehiclesByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM vehicles WHERE user_id = ?", new String[]{String.valueOf(userId)});
    }

    public int getVehicleIdByPlat(int userId, String plat) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM vehicles WHERE user_id = ? AND plat = ?",
                new String[]{String.valueOf(userId), plat});
        if (cursor.moveToFirst()) {
            int vehicleId = cursor.getInt(0);
            cursor.close();
            return vehicleId;
        }
        cursor.close();
        return -1;
    }

    public List<Vehicle> getAllVehiclesByUser(int userId) {
        List<Vehicle> vehicles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, model, plat FROM vehicles WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String model = cursor.getString(1);
                String plat = cursor.getString(2);
                vehicles.add(new Vehicle(id, model, plat));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return vehicles;
    }

    // Total biaya bensin untuk kendaraan tertentu
    public int getTotalFuelCost(int vehicleId) {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(total_cost) FROM fuel_logs WHERE vehicle_id = ?",
                new String[]{String.valueOf(vehicleId)});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    // Total biaya servis untuk kendaraan tertentu
    public int getTotalServiceCost(int vehicleId) {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(total_cost) FROM service_logs WHERE vehicle_id = ?",
                new String[]{String.valueOf(vehicleId)});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public boolean deleteVehicleById(int vehicleId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("fuel_logs", "vehicle_id = ?", new String[]{String.valueOf(vehicleId)});
        db.delete("service_logs", "vehicle_id = ?", new String[]{String.valueOf(vehicleId)});

        int result = db.delete("vehicles", "id = ?", new String[]{String.valueOf(vehicleId)});
        return result > 0;
    }

    // =========================
    // FUEL LOG
    // =========================

    public boolean insertFuelLog(int vehicleId, String date, double pricePerLiter, double liters, double totalCost, double odometer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("vehicle_id", vehicleId);
        values.put("date", date);
        values.put("price_per_liter", pricePerLiter);
        values.put("liters", liters);
        values.put("total_cost", totalCost);
        values.put("odometer", odometer);
        long result = db.insert("fuel_logs", null, values);
        return result != -1;
    }

    public Cursor getFuelLogsByVehicleId(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM fuel_logs WHERE vehicle_id = ? ORDER BY date DESC", new String[]{String.valueOf(vehicleId)});
    }

    public double getLastOdometer(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT odometer FROM fuel_logs WHERE vehicle_id = ? ORDER BY id DESC LIMIT 1", new String[]{String.valueOf(vehicleId)});
        if (cursor != null && cursor.moveToFirst()) {
            double lastOdo = cursor.getDouble(0);
            cursor.close();
            return lastOdo;
        }
        return 0;
    }


    // =========================
    // SERVICE LOG
    // =========================

    public boolean insertServiceLog(int vehicleId, int lastOdometer, int nextOliMesin, int nextOliGardan,
                                    int nextCVT, int nextKampasRem, int nextFilterUdara, int nextAirRadiator,
                                    String date, int totalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("vehicle_id", vehicleId);
        values.put("last_service_odometer", lastOdometer);
        values.put("next_oli_mesin", nextOliMesin);
        values.put("next_oli_gardan", nextOliGardan);
        values.put("next_cvt", nextCVT);
        values.put("next_kampas_rem", nextKampasRem);
        values.put("next_filter_udara", nextFilterUdara);
        values.put("next_air_radiator", nextAirRadiator);
        values.put("date", date);
        values.put("total_cost", totalCost);

        long result = db.insert("service_logs", null, values);
        return result != -1;

    }

    public Cursor getServiceLogsByVehicle(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM service_logs WHERE vehicle_id = ?", new String[]{String.valueOf(vehicleId)});
    }

    public int[] getLastServiceKM(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT next_oli_mesin, next_oli_gardan, next_cvt, next_kampas_rem, next_filter_udara, next_air_radiator FROM service_logs WHERE vehicle_id = ? ORDER BY id DESC LIMIT 1", new String[]{String.valueOf(vehicleId)});

        int[] result = new int[6];
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 6; i++) {
                result[i] = cursor.getInt(i);
            }
        } else {
            // Jika belum ada data sebelumnya, isi dengan odometer awal (misalnya 0)
            for (int i = 0; i < 6; i++) result[i] = 0;
        }
        cursor.close();
        return result;
    }

    public double getLastOdometerService(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double lastOdo = 0;

        // Cek data terakhir dari service_logs
        Cursor cursor = db.rawQuery("SELECT last_service_odometer FROM service_logs WHERE vehicle_id = ? ORDER BY id DESC LIMIT 1",
                new String[]{String.valueOf(vehicleId)});
        if (cursor.moveToFirst()) {
            lastOdo = cursor.getDouble(0);
        }
        cursor.close();
        return lastOdo;
    }

    // =========================
    // NOTIFICATION LOG
    // =========================

    public boolean insertNotifikasi(int userId, String tipe, String beban, String tanggal, String catatan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("tipe", tipe);
        values.put("beban", beban);
        values.put("tanggal", tanggal);
        values.put("catatan", catatan);

        long res = db.insert("notifikasi", null, values);
        return res != -1;
    }

    public Cursor getAllNotifikasiByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM notifikasi WHERE user_id = ?", new String[]{String.valueOf(userId)});
    }



    public boolean deleteNotifikasi(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("notifikasi", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

}
