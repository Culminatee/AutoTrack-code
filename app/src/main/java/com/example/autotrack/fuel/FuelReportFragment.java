package com.example.autotrack.fuel;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FuelReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private FuelLogAdapter adapter;
    private List<FuelLog> fuelLogs;
    private int vehicleId;
    private TextView tvRataRataUang, tvRataRataPemakaian;
    ImageButton btnSort;

    public FuelReportFragment() {

    }

    public static FuelReportFragment newInstance(int vehicleId) {
        FuelReportFragment fragment = new FuelReportFragment();
        Bundle args = new Bundle();
        args.putInt("vehicle_id", vehicleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_report, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvRataRataUang = view.findViewById(R.id.tvRataRataUang);
        tvRataRataPemakaian = view.findViewById(R.id.tvRataRataPemakaian);
        btnSort = view.findViewById(R.id.btnSort);
        dbHelper = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            vehicleId = getArguments().getInt("vehicle_id", -1);
        }

        if (vehicleId == -1) {
            Toast.makeText(getContext(), "Data kendaraan tidak ditemukan", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

        fuelLogs = getAllFuelLogs();

        sortFuelLogsByDate(fuelLogs);

        calculateTotalCost(fuelLogs);

        adapter = new FuelLogAdapter(requireContext(), fuelLogs, new FuelLogAdapter.OnItemClickListener() {
            @Override
            public void onDelete(FuelLog log) {
                showDeleteDialog(log);
            }
        });

        btnSort.setOnClickListener(v -> {
            SortFuelReport sortDialog = new SortFuelReport(new SortFuelReport.OnYearSelectedListener() {
                @Override
                public void onYearSelected(int selectedYear) {
                    // Filter fuelLogs berdasarkan tahun
                    List<FuelLog> filteredLogs = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    for (FuelLog log : getAllFuelLogs()) {
                        try {
                            Date date = sdf.parse(log.getDate());
                            if (date != null) {
                                int year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(date));
                                if (year == selectedYear) {
                                    filteredLogs.add(log);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    fuelLogs.clear();
                    fuelLogs.addAll(filteredLogs);
                    sortFuelLogsByDate(fuelLogs);
                    adapter.notifyDataSetChanged();
                    calculateTotalCost(fuelLogs);
                }
            });

            sortDialog.show(getParentFragmentManager(), "SortFuelReport");
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<FuelLog> getAllFuelLogs() {
        List<FuelLog> list = new ArrayList<>();
        Cursor cursor = dbHelper.getFuelLogsByVehicleId(vehicleId);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double pricePerLiter = cursor.getDouble(cursor.getColumnIndexOrThrow("price_per_liter"));
                double totalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("total_cost"));
                double liters = cursor.getDouble(cursor.getColumnIndexOrThrow("liters"));
                double odometer = cursor.getDouble(cursor.getColumnIndexOrThrow("odometer"));

                list.add(new FuelLog(id, date, pricePerLiter, totalCost, liters, odometer));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    private void calculateTotalCost(List<FuelLog> logs) {
        if (logs.size() < 1) {
            tvRataRataUang.setText("Total Pengeluaran: -");
            tvRataRataPemakaian.setText("Efisiensi Bensin: -");
            return;
        }

        double totalCost = 0;
        double totalConsumption = 0;
        int validSegments = 0;

        // Jumlahkan semua totalPay
        for (FuelLog log : logs) {
            totalCost += log.getTotalPay();
        }

        // Hitung efisiensi dari segmen jarak antar fuel logs
        for (int i = 1; i < logs.size(); i++) {
            FuelLog previous = logs.get(i - 1);
            FuelLog current = logs.get(i);

            double distance = current.getOdometer() - previous.getOdometer();
            double liters = current.getLiters(); // diasumsikan full tank

            if (distance > 0 && liters > 0) {
                totalConsumption += distance / liters;
                validSegments++;
            }
        }

        double averageConsumption = validSegments > 0 ? totalConsumption / validSegments : 0;

        tvRataRataUang.setText(String.format("Total Pengeluaran: Rp %.2f", totalCost));
        tvRataRataPemakaian.setText(String.format("Penggunaan Bensin: %.2f KM/L", averageConsumption));
    }


    private void sortFuelLogsByDate(List<FuelLog> logs) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Collections.sort(logs, new Comparator<FuelLog>() {
            @Override
            public int compare(FuelLog o1, FuelLog o2) {
                try {
                    Date date1 = sdf.parse(o1.getDate());
                    Date date2 = sdf.parse(o2.getDate());
                    // Urutan ascending: tanggal terlama ke terbaru
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    private void showDeleteDialog(FuelLog log) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Data")
                .setMessage("Apakah kamu yakin ingin menghapus data ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    dbHelper.getWritableDatabase().delete("fuel_logs", "id=?", new String[]{String.valueOf(log.getId())});
                    fuelLogs.remove(log);
                    adapter.notifyDataSetChanged();
                    calculateTotalCost(fuelLogs);
                    Toast.makeText(requireContext(), "Data dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
