package com.example.autotrack.dashboard.laporankeuangan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;

import java.util.List;

public class LaporanFragment extends Fragment {

    private DatabaseHelper db;
    private LinearLayout containerReports;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_laporan_keuangan, container, false);

        containerReports = root.findViewById(R.id.containerReports);
        db = new DatabaseHelper(getContext());

        SharedPreferences prefs = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username != null) {
            userId = db.getUserIdByUsername(username);
        }

        loadVehicleReports();

        return root;
    }

    private void loadVehicleReports() {
        List<Vehicle> vehicles = db.getAllVehiclesByUser(userId);

        for (Vehicle v : vehicles) {
            int totalFuelCost = db.getTotalFuelCost(v.getId());
            int totalServiceCost = db.getTotalServiceCost(v.getId());
            int totalOverall = totalFuelCost + totalServiceCost;

            View itemView = createVehicleReportView(
                    v.getModel(),
                    v.getPlat(),
                    totalFuelCost,
                    totalServiceCost,
                    totalOverall);

            containerReports.addView(itemView);
        }
    }

    private View createVehicleReportView(String model, String plat, int fuel, int service, int total) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.item_vehicle_report, containerReports, false);

        TextView tvModel = view.findViewById(R.id.tvModel);
        TextView tvPlat = view.findViewById(R.id.tvPlat);
        TextView tvFuelCost = view.findViewById(R.id.tvFuelCost);
        TextView tvServiceCost = view.findViewById(R.id.tvServiceCost);
        TextView tvTotalCost = view.findViewById(R.id.tvTotalCost);

        tvModel.setText(model);
        tvPlat.setText(plat);
        tvFuelCost.setText("Total Biaya Bensin: Rp " + fuel);
        tvServiceCost.setText("Total Biaya Servis: Rp " + service);
        tvTotalCost.setText("Total Keseluruhan: Rp " + total);

        return view;
    }
}
