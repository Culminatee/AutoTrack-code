package com.example.autotrack.dashboard.registrasikendaraan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.autotrack.R;
import com.example.autotrack.baseactivity.BaseActivity;
import com.example.autotrack.dashboard.laporankeuangan.LaporanActivity;
import com.example.autotrack.database.DatabaseHelper;
import com.example.autotrack.fuel.AddFuelActivity;
import com.example.autotrack.service.ServiceActivity;

public class KelolaKendaraanActivity extends BaseActivity {
    Button btnAdd, btnView, btnService;
    private DatabaseHelper db;
    private int vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_kendaraan);

        db = new DatabaseHelper(this);

        // Setup toolbar dari BaseActivity
        setupToolbar(R.id.toolbar, "Kelola Kendaraan", false);

        // Ambil data dari Intent
        Intent intent = getIntent();
        String model = intent.getStringExtra("model");
        String plat = intent.getStringExtra("plat");
        int userId = intent.getIntExtra("user_id", -1);

        // Tampilkan informasi kendaraan
        TextView tvModel = findViewById(R.id.tvModel);
        TextView tvPlat = findViewById(R.id.tvPlat);
        tvModel.setText("Model: " + model);
        tvPlat.setText("Plat: " + plat);

        // Ambil vehicle_id dari DB berdasarkan user_id dan plat
        vehicleId = db.getVehicleIdByPlat(userId, plat);

        // Inisialisasi tombol
        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        btnService = findViewById(R.id.btnService);

        // Tombol Tambah BBM
        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(KelolaKendaraanActivity.this, AddFuelActivity.class);
            i.putExtra("vehicle_id", vehicleId);
            startActivity(i);
        });

        // Tombol Lihat Riwayat BBM
        btnView.setOnClickListener(v -> {
            Intent i = new Intent(KelolaKendaraanActivity.this, LaporanActivity.class);
            i.putExtra("vehicle_id", vehicleId);
            startActivity(i);
        });



        // Tombol Service
        btnService.setOnClickListener(v -> {
            Intent i = new Intent(KelolaKendaraanActivity.this, ServiceActivity.class);
            i.putExtra("vehicle_id", vehicleId);
            startActivity(i);
        });
    }
}
