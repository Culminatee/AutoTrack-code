package com.example.autotrack.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.autotrack.R;
import com.example.autotrack.baseactivity.BaseActivity;
import com.example.autotrack.dashboard.laporankeuangan.LaporanFragment;
import com.example.autotrack.dashboard.notifikasi.NotifikasiFragment;
import com.example.autotrack.dashboard.registrasikendaraan.PilihKendaraanFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Setup toolbar dari BaseActivity
        setupToolbar(R.id.toolbar, "Pilih Kendaraan", false);

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load default fragment
        loadFragment(new PilihKendaraanFragment(), "Dashboard");

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            String title = "";

            if (itemId == R.id.navigation_pilih_kendaraan) {
                selectedFragment = new PilihKendaraanFragment();
                title = "Dashboard";
            } else if (itemId == R.id.navigation_laporan) {
                selectedFragment = new LaporanFragment();
                title = "Laporan Keuangan";
            } else if (itemId == R.id.navigation_pengingat) {
                selectedFragment = new NotifikasiFragment();
                title = "Pengingat";
            } else if (itemId == R.id.navigation_profil) {
                selectedFragment = new ProfileFragment();
                title = "Profil";
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, title);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (!(currentFragment instanceof PilihKendaraanFragment)) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_pilih_kendaraan);
        } else {
            super.onBackPressed();
        }
    }
}
