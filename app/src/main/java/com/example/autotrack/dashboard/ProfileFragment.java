package com.example.autotrack.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;
import com.example.autotrack.loginactivity.MainActivity;
import com.example.autotrack.loginactivity.ResetPasswordActivity;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvFirstName, tvLastName, tvEmail;
    private Button btnLogout, tvForgotPassword;
    private DatabaseHelper databaseHelper;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvLastName = view.findViewById(R.id.tvLastName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btn_logout);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        databaseHelper = new DatabaseHelper(requireContext());

        SharedPreferences preferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        String username = preferences.getString("username", null);

        if (username != null) {
            tvUsername.setText(username);

            Cursor cursor = databaseHelper.getUserDetails(username);
            if (cursor != null && cursor.moveToFirst()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

                tvFirstName.setText(firstName);
                tvLastName.setText(lastName);
                tvEmail.setText(email);

                cursor.close();
            }
        } else {
            tvUsername.setText("Tidak Dikenal");
            Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show();
        }

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ResetPasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}

