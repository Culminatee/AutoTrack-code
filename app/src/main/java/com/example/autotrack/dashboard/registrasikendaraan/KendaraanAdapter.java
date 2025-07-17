package com.example.autotrack.dashboard.registrasikendaraan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;

import java.util.List;

public class KendaraanAdapter extends BaseAdapter {
    private Context context;
    private List<KendaraanModel> list;
    private DatabaseHelper dbHelper;

    public KendaraanAdapter(Context context, List<KendaraanModel> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        KendaraanModel kendaraan = list.get(i);
        view = LayoutInflater.from(context).inflate(R.layout.item_kendaraan, viewGroup, false);

        TextView tvMerk = view.findViewById(R.id.tv_merk);
        TextView tvTipe = view.findViewById(R.id.tv_tipe);
        TextView tvModel = view.findViewById(R.id.tv_model);
        TextView tvPlat = view.findViewById(R.id.tv_plat);
        View btnDelete = view.findViewById(R.id.btnDelete);

        if (kendaraan.isAddButton()) {
            tvMerk.setText("+");
            tvTipe.setText("Tambah");
            tvModel.setText("");
            tvPlat.setText("");
            btnDelete.setVisibility(View.GONE);
        } else {
            tvMerk.setText(kendaraan.getMerk());
            tvTipe.setText(kendaraan.getTipe());
            tvModel.setText(kendaraan.getModel());
            tvPlat.setText(kendaraan.getPlat());
            btnDelete.setVisibility(View.VISIBLE);

            btnDelete.setOnClickListener(v -> showDeleteDialog(i, kendaraan));
        }

        return view;
    }

    private void showDeleteDialog(int position, KendaraanModel kendaraan) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Kendaraan")
                .setMessage("Apakah kamu yakin ingin menghapus kendaraan ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    SharedPreferences prefs = context.getSharedPreferences("login_pref", Context.MODE_PRIVATE);
                    String username = prefs.getString("username", null);

                    if (username != null) {
                        int userId = dbHelper.getUserIdByUsername(username);
                        int vehicleId = dbHelper.getVehicleIdByPlat(userId, kendaraan.getPlat());

                        if (vehicleId != -1 && dbHelper.deleteVehicleById(vehicleId)) {
                            list.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Kendaraan dihapus", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Gagal menghapus kendaraan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
