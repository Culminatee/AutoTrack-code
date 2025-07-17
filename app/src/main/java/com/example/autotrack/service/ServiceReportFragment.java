package com.example.autotrack.service;

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
import com.example.autotrack.fuel.SortFuelReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceLogAdapter adapter;
    private List<ServiceLog> serviceLogs;
    private DatabaseHelper dbHelper;
    private int vehicleId;
    private TextView tvPengeluaranUang;
    ImageButton btnSort;

    public static ServiceReportFragment newInstance(int vehicleId) {
        ServiceReportFragment fragment = new ServiceReportFragment();
        Bundle args = new Bundle();
        args.putInt("vehicle_id", vehicleId);
        fragment.setArguments(args);
        return fragment;
    }

    public ServiceReportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_report, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        dbHelper = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            vehicleId = getArguments().getInt("vehicle_id", -1);
        }

        tvPengeluaranUang = view.findViewById(R.id.tvPengeluaranUang);

        if (vehicleId == -1) {
            Toast.makeText(requireContext(), "ID Kendaraan tidak ditemukan", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

        serviceLogs = getAllServiceLogs();

        sortServiceLogsByDate(serviceLogs);

        adapter = new ServiceLogAdapter(requireContext(), serviceLogs, new ServiceLogAdapter.OnItemClickListener() {
            @Override
            public void onDelete(ServiceLog log) {
                showDeleteDialog(log);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        btnSort = view.findViewById(R.id.btnSort);
        btnSort.setOnClickListener(v -> {
            SortFuelReport sortDialog = new SortFuelReport(new SortFuelReport.OnYearSelectedListener() {
                @Override
                public void onYearSelected(int selectedYear) {
                    // Filter data berdasarkan tahun di sini
                    List<ServiceLog> filteredLogs = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    for (ServiceLog log : getAllServiceLogs()) {
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

                    serviceLogs.clear();
                    serviceLogs.addAll(filteredLogs);
                    sortServiceLogsByDate(serviceLogs);
                    adapter.notifyDataSetChanged();
                    calculateTotalCost(serviceLogs);
                }
            });

            sortDialog.show(getParentFragmentManager(), "SortServiceReport");
        });

        calculateTotalCost(serviceLogs);

        return view;
    }

    private void calculateTotalCost(List<ServiceLog> logs) {
        if (logs == null || logs.size() < 1) {
            tvPengeluaranUang.setText("Total Pengeluaran: -");
            return;
        }

        double totalCost = 0;
        for (ServiceLog log : logs) {
            totalCost += log.getTotalCost();
        }

        tvPengeluaranUang.setText(String.format("Total Pengeluaran: Rp %.2f", totalCost));
    }


    private List<ServiceLog> getAllServiceLogs() {
        List<ServiceLog> list = new ArrayList<>();
        Cursor cursor = dbHelper.getServiceLogsByVehicle(vehicleId);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int odometer = cursor.getInt(cursor.getColumnIndexOrThrow("last_service_odometer"));
                int nextOliMesin = cursor.getInt(cursor.getColumnIndexOrThrow("next_oli_mesin"));
                int nextOliGardan = cursor.getInt(cursor.getColumnIndexOrThrow("next_oli_gardan"));
                int nextCVT = cursor.getInt(cursor.getColumnIndexOrThrow("next_cvt"));
                int nextKampasRem = cursor.getInt(cursor.getColumnIndexOrThrow("next_kampas_rem"));
                int nextFilterUdara = cursor.getInt(cursor.getColumnIndexOrThrow("next_filter_udara"));
                int nextAirRadiator = cursor.getInt(cursor.getColumnIndexOrThrow("next_air_radiator"));
                int totalCost = cursor.getInt(cursor.getColumnIndexOrThrow("total_cost"));

                list.add(new ServiceLog(id, date, odometer, nextOliMesin, nextOliGardan, nextCVT,
                        nextKampasRem, nextFilterUdara, nextAirRadiator, totalCost));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    private void sortServiceLogsByDate(List<ServiceLog> logs) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Collections.sort(logs, new Comparator<ServiceLog>() {
            @Override
            public int compare(ServiceLog o1, ServiceLog o2) {
                try {
                    Date date1 = sdf.parse(o1.getDate());
                    Date date2 = sdf.parse(o2.getDate());
                    return date1.compareTo(date2); // urutan dari lama ke baru
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }


    private void showDeleteDialog(ServiceLog log) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Data")
                .setMessage("Apakah kamu yakin ingin menghapus data ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    dbHelper.getWritableDatabase().delete("service_logs", "id=?", new String[]{String.valueOf(log.getId())});
                    serviceLogs.remove(log);
                    adapter.notifyDataSetChanged();
                    calculateTotalCost(serviceLogs);
                    Toast.makeText(requireContext(), "Data dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
