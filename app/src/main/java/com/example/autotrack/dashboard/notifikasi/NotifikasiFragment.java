package com.example.autotrack.dashboard.notifikasi;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NotifikasiFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private NotifikasiAdapter adapter;
    private List<NotifikasiAdapter.Notifikasi> notifikasiList;
    private int userId;

    public NotifikasiFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifikasi, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerNotifikasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Wajib: buat notification channel saat pertama kali
        createNotificationChannel();

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), TambahNotifikasiActivity.class));
        });

        SharedPreferences prefs = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username != null) {
            userId = dbHelper.getUserIdByUsername(username);
        }

        notifikasiList = new ArrayList<>();
        adapter = new NotifikasiAdapter(getContext(), notifikasiList, (item, position) -> {
            showDeleteDialog(requireContext(), item, position);
        });
        recyclerView.setAdapter(adapter);

        Log.d("NotifikasiFragment", "User ID: " + userId);

        loadNotifikasi();

        return view;
    }

    private void loadNotifikasi() {
        notifikasiList.clear();

        Cursor cursor = dbHelper.getAllNotifikasiByUser(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int tipeIndex = cursor.getColumnIndex("tipe");
                int bebanIndex = cursor.getColumnIndex("beban");
                int tanggalIndex = cursor.getColumnIndex("tanggal");
                int catatanIndex = cursor.getColumnIndex("catatan");

                int id = idIndex != -1 ? cursor.getInt(idIndex) : -1;
                String tipe = tipeIndex != -1 ? cursor.getString(tipeIndex) : "";
                String beban = bebanIndex != -1 ? cursor.getString(bebanIndex) : "";
                String tanggal = tanggalIndex != -1 ? cursor.getString(tanggalIndex) : "";
                String catatan = catatanIndex != -1 ? cursor.getString(catatanIndex) : "";

                notifikasiList.add(new NotifikasiAdapter.Notifikasi(id, tipe, beban, tanggal, catatan));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Sort berdasarkan tanggal terbaru
        Collections.sort(notifikasiList, new Comparator<NotifikasiAdapter.Notifikasi>() {
            @Override
            public int compare(NotifikasiAdapter.Notifikasi o1, NotifikasiAdapter.Notifikasi o2) {
                try {
                    Date d1 = sdf.parse(o1.tanggal);
                    Date d2 = sdf.parse(o2.tanggal);
                    return d2.compareTo(d1);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        adapter.notifyDataSetChanged();

        // Cek apakah ada notifikasi untuk hari ini
        try {
            String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            for (int i = 0; i < notifikasiList.size(); i++) {
                NotifikasiAdapter.Notifikasi notif = notifikasiList.get(i);
                if (notif.tanggal.equals(today)) {
                    showNotification(i, "[" + notif.tipe + "] " + notif.beban, notif.catatan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikasi AutoTrack";
            String description = "Channel untuk notifikasi AutoTrack";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_autotrack", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(int id, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "channel_autotrack")
                .setSmallIcon(R.drawable.logo2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(id, builder.build());
    }


    @Override
    public void onResume() {
        super.onResume();
        loadNotifikasi(); // reload data saat fragment ditampilkan kembali
    }

    private void showDeleteDialog(Context context, NotifikasiAdapter.Notifikasi notif, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Notifikasi")
                .setMessage("Apakah kamu yakin ingin menghapus notifikasi ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (dbHelper.deleteNotifikasi(notif.id)) {
                        notifikasiList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
