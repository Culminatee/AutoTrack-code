package com.example.autotrack.dashboard.registrasikendaraan;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.autotrack.R;

import android.content.SharedPreferences;
import com.example.autotrack.database.DatabaseHelper;


public class RegistrasiActivity extends Fragment {

    private Spinner spinnerTipe, spinnerMerk;
    private EditText editModel, editPlat, editBBM;
    private Button btnSimpan;
    private DatabaseHelper databaseHelper;

    public RegistrasiActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pilih_kendaraan, container, false);

        // Inisialisasi komponen
        spinnerTipe = view.findViewById(R.id.spinner_tipe);
        spinnerMerk = view.findViewById(R.id.spinner_merk);
        editModel = view.findViewById(R.id.edit_model);
        editPlat = view.findViewById(R.id.edit_plat);
        editBBM = view.findViewById(R.id.edit_bbm);
        btnSimpan = view.findViewById(R.id.btn_simpan);

        databaseHelper = new DatabaseHelper(requireContext());

        btnSimpan.setOnClickListener(v -> {
            String tipe = spinnerTipe.getSelectedItem().toString();
            String merk = spinnerMerk.getSelectedItem().toString();
            String model = editModel.getText().toString();
            String plat = editPlat.getText().toString();
            String bbmStr = editBBM.getText().toString();

            if (model.isEmpty() || plat.isEmpty() || bbmStr.isEmpty()) {
                Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            double kapasitasBBM;
            try {
                kapasitasBBM = Double.parseDouble(bbmStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Kapasitas BBM harus berupa angka (bisa desimal)", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences preferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
            String username = preferences.getString("username", null);
            if (username == null) {
                Toast.makeText(getContext(), "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = databaseHelper.getUserIdByUsername(username);
            if (userId == -1) {
                Toast.makeText(getContext(), "Pengguna tidak ditemukan di database", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = databaseHelper.insertVehicle(userId, tipe, merk, model, plat, kapasitasBBM);
            if (success) {
                Toast.makeText(getContext(), "Kendaraan berhasil disimpan", Toast.LENGTH_SHORT).show();
                clearInput();
            } else {
                Toast.makeText(getContext(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            }

            Log.d("DEBUG", "Username = " + username + ", userId = " + userId);
        });


        return view;
    }

    private void clearInput() {
        editModel.setText("");
        editPlat.setText("");
        editBBM.setText("");
        spinnerTipe.setSelection(0);
        spinnerMerk.setSelection(0);
    }
}
