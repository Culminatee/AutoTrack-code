package com.example.autotrack.dashboard.laporankeuangan;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.autotrack.fuel.FuelReportFragment;
import com.example.autotrack.service.ServiceReportFragment;

public class LaporanAdapter extends FragmentStateAdapter {

    private final int vehicleId;

    public LaporanAdapter(@NonNull FragmentActivity fragmentActivity, int vehicleId) {
        super(fragmentActivity);
        this.vehicleId = vehicleId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return FuelReportFragment.newInstance(vehicleId);
        } else {
            return ServiceReportFragment.newInstance(vehicleId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

