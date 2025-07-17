package com.example.autotrack.fuel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.autotrack.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class SortFuelReport extends BottomSheetDialogFragment {

    public interface OnYearSelectedListener {
        void onYearSelected(int year);
    }

    private OnYearSelectedListener listener;

    public SortFuelReport(OnYearSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fuel_report_sort, null);

        ListView listView = view.findViewById(R.id.listViewYears);

        ArrayList<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 5; i++) {
            years.add(String.valueOf(currentYear - i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, years);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            int selectedYear = Integer.parseInt(years.get(position));
            listener.onYearSelected(selectedYear);
            dismiss();
        });

        dialog.setContentView(view);
        return dialog;
    }
}

