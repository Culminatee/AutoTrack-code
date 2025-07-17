package com.example.autotrack.service;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autotrack.R;
import com.example.autotrack.baseactivity.BaseActivity;
import com.example.autotrack.database.DatabaseHelper;

import java.util.Calendar;

public class ServiceActivity extends BaseActivity {

    private EditText etOdometer, etDate, etCost, etLastOdometer;
    private TextView tvOliMesin, tvOliGardan, tvCVT, tvKampasRem, tvFilterUdara, tvAirRadiator;
    private Button btnCalculate, btnSave;

    private DatabaseHelper dbHelper;
    private int vehicleId, totalCost;

    private int lastOdometer;
    private int nextOliMesin, nextOliGardan, nextCVT, nextKampasRem, nextFilterUdara, nextAirRadiator;
    private int[] lastServiceKM = new int[6];
    private Switch switchOliMesin, switchOliGardan, switchCVT, switchKampasRem, switchFilterUdara, switchAirRadiator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        setupToolbar(R.id.topAppBar, "Service", true);

        // Inisialisasi view
        etOdometer = findViewById(R.id.etOdometer);
        etLastOdometer = findViewById(R.id.etLastOdometer);
        etDate = findViewById(R.id.etDate);
        etCost = findViewById(R.id.etCost);
        tvOliMesin = findViewById(R.id.tvOliMesin);
        tvOliGardan = findViewById(R.id.tvOliGardan);
        tvCVT = findViewById(R.id.tvCVT);
        tvKampasRem = findViewById(R.id.tvKampasRem);
        tvFilterUdara = findViewById(R.id.tvFilterUdara);
        tvAirRadiator = findViewById(R.id.tvAirRadiator);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);

        switchOliMesin = findViewById(R.id.switchOliMesin);
        switchOliGardan = findViewById(R.id.switchOliGardan);
        switchCVT = findViewById(R.id.switchCVT);
        switchKampasRem = findViewById(R.id.switchKampasRem);
        switchFilterUdara = findViewById(R.id.switchFilterUdara);
        switchAirRadiator = findViewById(R.id.switchAirRadiator);

        // Tampilkan date picker
        etDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ServiceActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                        etDate.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Inisialisasi DB
        dbHelper = new DatabaseHelper(this);

        loadPreviousServiceData();
        lastServiceKM = dbHelper.getLastServiceKM(vehicleId); // Ambil data sebelumnya
        // Ambil vehicle_id dari Intent
        Intent intent = getIntent();
        vehicleId = intent.getIntExtra("vehicle_id", -1);

        double lastOdo = dbHelper.getLastOdometerService(vehicleId);
        etLastOdometer.setText("km terakhir: " + lastOdo);

        if (vehicleId == -1) {
            Toast.makeText(this, "Kendaraan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnCalculate.setOnClickListener(v -> calculateServiceSchedule());
        btnSave.setOnClickListener(v -> saveServiceData());
    }

    private void calculateServiceSchedule() {
        lastServiceKM = dbHelper.getLastServiceKM(vehicleId);

        String odoStr = etOdometer.getText().toString().trim();
        if (odoStr.isEmpty()) {
            Toast.makeText(this, "Masukkan odometer!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            lastOdometer = Integer.parseInt(odoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Odometer tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Oli Mesin
        if (switchOliMesin.isChecked()) {
            nextOliMesin = lastOdometer + 2000;
        } else {
            nextOliMesin = lastServiceKM[0];
        }
        tvOliMesin.setText("Akan diganti pada " + nextOliMesin + " km");

        // Oli Gardan
        if (switchOliGardan.isChecked()) {
            nextOliGardan = lastOdometer + 4000;
        } else {
            nextOliGardan = lastServiceKM[1];
        }
        tvOliGardan.setText("Akan diganti pada " + nextOliGardan + " km");

        // CVT
        if (switchCVT.isChecked()) {
            nextCVT = lastOdometer + 10000;
        } else {
            nextCVT = lastServiceKM[2];
        }
        tvCVT.setText("Akan diganti pada " + nextCVT + " km");

        // Kampas Rem
        if (switchKampasRem.isChecked()) {
            nextKampasRem = lastOdometer + 10000;
        } else {
            nextKampasRem = lastServiceKM[3];
        }
        tvKampasRem.setText("Akan diganti pada " + nextKampasRem + " km");

        // Filter Udara
        if (switchFilterUdara.isChecked()) {
            nextFilterUdara = lastOdometer + 8000;
        } else {
            nextFilterUdara = lastServiceKM[4];
        }
        tvFilterUdara.setText("Akan diganti pada " + nextFilterUdara + " km");

        // Air Radiator
        if (switchAirRadiator.isChecked()) {
            nextAirRadiator = lastOdometer + 20000;
        } else {
            nextAirRadiator = lastServiceKM[5];
        }
        tvAirRadiator.setText("Akan diganti pada " + nextAirRadiator + " km");
    }


    private void loadPreviousServiceData() {
        // Ambil data terakhir dari database
        int[] data = dbHelper.getLastServiceKM(vehicleId);
        if (data != null && data.length == 6) {
            nextOliMesin = data[0];
            nextOliGardan = data[1];
            nextCVT = data[2];
            nextKampasRem = data[3];
            nextFilterUdara = data[4];
            nextAirRadiator = data[5];
        }
    }

    private void saveServiceData() {
        String date = etDate.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this, "Masukkan tanggal!", Toast.LENGTH_SHORT).show();
            return;
        }

        String costStr = etCost.getText().toString().trim();
        if (costStr.isEmpty()) {
            Toast.makeText(this, "Masukkan biaya servis!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("ServiceActivity", "etDate: " + etDate);
        Log.d("ServiceActivity", "etCost: " + etCost);

        try {
            totalCost = Integer.parseInt(costStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Biaya tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.insertServiceLog(
                vehicleId,
                lastOdometer,
                nextOliMesin,
                nextOliGardan,
                nextCVT,
                nextKampasRem,
                nextFilterUdara,
                nextAirRadiator,
                date,
                totalCost
        );

        if (inserted) {
            Toast.makeText(this, "Data service tersimpan!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show();
        }
    }
}
