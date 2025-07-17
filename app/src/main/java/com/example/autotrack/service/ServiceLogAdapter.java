package com.example.autotrack.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autotrack.R;

import java.util.List;

public class ServiceLogAdapter extends RecyclerView.Adapter<ServiceLogAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onDelete(ServiceLog log);
    }

    private Context context;
    private List<ServiceLog> logs;
    private OnItemClickListener listener;

    public ServiceLogAdapter(Context context, List<ServiceLog> logs, OnItemClickListener listener) {
        this.context = context;
        this.logs = logs;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvOdometer, tvCost, tvOliMesin, tvOliGardan, tvCVT, tvKampasRem, tvFilterUdara, tvAirRadiator;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvOdometer = itemView.findViewById(R.id.tvOdometer);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvOliMesin = itemView.findViewById(R.id.tvOliMesin);
            tvOliGardan = itemView.findViewById(R.id.tvOliGardan);
            tvCVT = itemView.findViewById(R.id.tvCVT);
            tvKampasRem = itemView.findViewById(R.id.tvKampasRem);
            tvFilterUdara = itemView.findViewById(R.id.tvFilterUdara);
            tvAirRadiator = itemView.findViewById(R.id.tvAirRadiator);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceLog log = logs.get(position);

        holder.tvDate.setText("Tanggal: " + log.getDate());
        holder.tvOdometer.setText("Odometer: " + log.getOdometer() + " km");

        holder.tvCost.setText("Biaya Servis: Rp " + log.getTotalCost());

        holder.tvOliMesin.setText   ("Oli Mesin : " + log.getNextOliMesin() + " km");
        holder.tvOliGardan.setText  ("Oli Gardan : " + log.getNextOliGardan() + " km");
        holder.tvCVT.setText        ("CVT : " + log.getNextCVT() + " km");
        holder.tvKampasRem.setText  ("Kampas Rem : " + log.getNextKampasRem() + " km");
        holder.tvFilterUdara.setText("Filter Udara : " + log.getNextFilterUdara() + " km");
        holder.tvAirRadiator.setText("Air Radiator: " + log.getNextAirRadiator() + " km");

        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(log);
            return true;
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onDelete(log);
        });
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }
}
