package com.example.autotrack.dashboard.notifikasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;

import java.util.List;

public class NotifikasiAdapter extends RecyclerView.Adapter<NotifikasiAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClicked(Notifikasi item, int position);
    }

    public static class Notifikasi {
        public int id;
        public String tipe;
        public String beban;
        public String tanggal;
        public String catatan;

        public Notifikasi(int id, String tipe, String beban, String tanggal, String catatan) {
            this.id = id;
            this.tipe = tipe;
            this.beban = beban;
            this.tanggal = tanggal;
            this.catatan = catatan;
        }
    }

    private final List<Notifikasi> notifikasiList;
    private final LayoutInflater inflater;
    private final DatabaseHelper dbHelper;
    private final OnDeleteClickListener listener;

    public NotifikasiAdapter(Context context, List<Notifikasi> data, OnDeleteClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.notifikasiList = data;
        this.dbHelper = new DatabaseHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_notifikasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifikasi item = notifikasiList.get(position);
        holder.tvTipeBeban.setText("[" + item.tipe + "] " + item.beban);
        holder.tvTanggal.setText(item.tanggal);
        holder.tvCatatan.setText(item.catatan);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifikasiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipeBeban, tvTanggal, tvCatatan;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipeBeban = itemView.findViewById(R.id.tvTipeBeban);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvCatatan = itemView.findViewById(R.id.tvCatatan);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
