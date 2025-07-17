package com.example.autotrack.dashboard.laporankeuangan;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.autotrack.R;
import com.example.autotrack.baseactivity.BaseActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LaporanActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LaporanAdapter adapter;
    private int vehicleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        setupToolbar(R.id.topAppBar, "Laporan Kendaraan", true);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        vehicleId = getIntent().getIntExtra("vehicle_id", -1);
        if (vehicleId == -1) {
            finish();
            return;
        }

        adapter = new LaporanAdapter(this, vehicleId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Pengisian Bensin");
                    } else {
                        tab.setText("Laporan Servis");
                    }
                }).attach();
    }
}
