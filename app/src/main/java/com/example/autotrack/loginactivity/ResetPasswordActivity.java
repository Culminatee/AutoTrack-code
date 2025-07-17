package com.example.autotrack.loginactivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.autotrack.R;
import com.example.autotrack.baseactivity.BaseActivity;
import com.example.autotrack.database.DatabaseHelper;

public class ResetPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setupToolbar(R.id.topAppBar, "Ganti Kata Sandi", true);

        EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnSubmit = findViewById(R.id.btnSubmitPassword);

        btnSubmit.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences preferences = getSharedPreferences("login_pref", MODE_PRIVATE);
            String username = preferences.getString("username", null);

            if (username != null) {
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                boolean updated = dbHelper.updatePassword(username, newPassword);

                if (updated) {
                    Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                    finish(); // kembali ke halaman sebelumnya
                } else {
                    Toast.makeText(this, "Gagal mengubah password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
