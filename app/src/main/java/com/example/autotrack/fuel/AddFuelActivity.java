package com.example.autotrack.fuel;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.autotrack.R;
import com.example.autotrack.baseactivity.BaseActivity;
import com.example.autotrack.database.DatabaseHelper;

import java.util.Calendar;

public class AddFuelActivity extends BaseActivity {
    private EditText etDate, etPricePerLiter, etPay, etOdometer, etLastOdometer;
    private Button btnSave;
    private DatabaseHelper db;
    private int vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fuel);

        setupToolbar(R.id.topAppBar, "Pengisian Bensin", true);

        // Inisialisasi view
        etDate = findViewById(R.id.etDate);
        etPricePerLiter = findViewById(R.id.etPricePerLiter);
        etPay = findViewById(R.id.etPay);
        etOdometer = findViewById(R.id.etOdometer);
        btnSave = findViewById(R.id.btnSave);
        etLastOdometer = findViewById(R.id.etLastOdometer);


        // Inisialisasi database
        db = new DatabaseHelper(this);

        // Ambil vehicle_id dari Intent
        Intent intent = getIntent();
        vehicleId = intent.getIntExtra("vehicle_id", -1);

        if (vehicleId == -1) {
            Toast.makeText(this, "Kendaraan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tampilkan date picker saat klik tanggal
        etDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddFuelActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                        etDate.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        double lastOdo = db.getLastOdometer(vehicleId);
        etLastOdometer.setText("km terakhir:" + lastOdo);

        // Simpan data saat tombol ditekan
        btnSave.setOnClickListener(v -> {
            String date = etDate.getText().toString().trim();
            String priceStr = etPricePerLiter.getText().toString().trim();
            String payStr = etPay.getText().toString().trim();
            String odoStr = etOdometer.getText().toString().trim();

            if (date.isEmpty() || priceStr.isEmpty() || payStr.isEmpty() || odoStr.isEmpty()) {
                Toast.makeText(AddFuelActivity.this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double pricePerLiter = Double.parseDouble(priceStr);
                double totalPay = Double.parseDouble(payStr);
                double odometer = Double.parseDouble(odoStr);
                double liters = totalPay / pricePerLiter;

                boolean inserted = db.insertFuelLog(vehicleId, date, pricePerLiter, liters, totalPay, odometer);
                if (inserted) {
                    Toast.makeText(AddFuelActivity.this, "Data tersimpan", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddFuelActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddFuelActivity.this, "Input tidak valid", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
