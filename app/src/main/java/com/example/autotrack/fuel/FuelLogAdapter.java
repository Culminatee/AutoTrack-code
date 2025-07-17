package com.example.autotrack.fuel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.autotrack.R;

import java.util.List;

public class FuelLogAdapter extends RecyclerView.Adapter<FuelLogAdapter.ViewHolder> {

    private Context context;
    private List<FuelLog> fuelLogList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(FuelLog log);
    }

    public FuelLogAdapter(Context context, List<FuelLog> fuelLogList, OnItemClickListener listener) {
        this.context = context;
        this.fuelLogList = fuelLogList;
        this.listener = listener;
    }

    public void updateFuelLogList(List<FuelLog> newList) {
        this.fuelLogList = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogId, tvDate, tvPricePerLiter, tvTotalPay, tvLiters, tvOdometer;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLogId = itemView.findViewById(R.id.tvLogId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPricePerLiter = itemView.findViewById(R.id.tvPricePerLiter);
            tvTotalPay = itemView.findViewById(R.id.tvTotalPay);
            tvLiters = itemView.findViewById(R.id.tvLiters);
            tvOdometer = itemView.findViewById(R.id.tvOdometer);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(FuelLog log, int position) {
            tvLogId.setText("No: " + (position + 1));
            tvDate.setText("Tanggal: " + log.getDate());
            tvPricePerLiter.setText("Harga per Liter: Rp " + log.getPricePerLiter());
            tvTotalPay.setText("Total Bayar: Rp " + log.getTotalPay());

            String formattedLiters = String.format("%.2f", log.getLiters());
            tvLiters.setText("Jumlah Liter: " + formattedLiters + " L");

            tvOdometer.setText("Odometer: " + log.getOdometer() + " km");

            btnDelete.setOnClickListener(v -> listener.onDelete(log));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fuel_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(fuelLogList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return fuelLogList == null ? 0 : fuelLogList.size();
    }
}
