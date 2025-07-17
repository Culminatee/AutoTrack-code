package com.example.autotrack.dashboard.registrasikendaraan;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PilihKendaraanFragment extends Fragment {

    private GridView gridView;
    private List<KendaraanModel> kendaraanList;
    private KendaraanAdapter adapter;
    private DatabaseHelper dbHelper;

    public PilihKendaraanFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pilih_kendaraan, container, false);

        gridView = view.findViewById(R.id.grid_kendaraan);
        dbHelper = new DatabaseHelper(requireContext());
        kendaraanList = new ArrayList<>();

        // Ambil user_id dari SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("login_pref", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        // Set welcome message
        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        tvWelcome.setText("Selamat datang, " + username + "!");

        if (username != null) {
            int userId = dbHelper.getUserIdByUsername(username);

            Cursor cursor = dbHelper.getVehiclesByUser(userId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String tipe = cursor.getString(cursor.getColumnIndexOrThrow("tipe"));
                    String merk = cursor.getString(cursor.getColumnIndexOrThrow("merk"));
                    String model = cursor.getString(cursor.getColumnIndexOrThrow("model"));
                    String plat = cursor.getString(cursor.getColumnIndexOrThrow("plat"));

                    kendaraanList.add(new KendaraanModel(tipe, merk, model, plat));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        // Tambahkan tombol "Tambah" di akhir grid
        kendaraanList.add(new KendaraanModel(true));

        adapter = new KendaraanAdapter(requireContext(), kendaraanList);
        gridView.setAdapter(adapter);

        final int userId = dbHelper.getUserIdByUsername(username);

        gridView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            KendaraanModel selected = kendaraanList.get(position);

            if (selected.isAddButton()) {
                Fragment registrasiFragment = new RegistrasiActivity();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, registrasiFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Kirim data kendaraan ke KelolaKendaraanActivity
                Intent intent = new Intent(requireContext(), KelolaKendaraanActivity.class);
                intent.putExtra("tipe", selected.getTipe());
                intent.putExtra("merk", selected.getMerk());
                intent.putExtra("model", selected.getModel());
                intent.putExtra("plat", selected.getPlat());
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });


        return view;
    }

}
