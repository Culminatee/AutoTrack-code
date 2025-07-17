package com.example.autotrack.loginactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autotrack.R;
import com.example.autotrack.database.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etFirstName, etLastName, etEmail;
    Button btnSignUp;
    TextView btnKembali;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Inisialisasi komponen UI
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnKembali = findViewById(R.id.btnKembali);

        // Inisialisasi database helper
        dbHelper = new DatabaseHelper(this);

        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            //Validasi input kosong
            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show();
                return;
            }

            //Validasi email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            //Cek apakah username sudah terdaftar
            if (dbHelper.isUsernameTaken(username)) {
                Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
                return;
            }

            //Insert user ke database
            boolean inserted = dbHelper.insertUser(username, password, firstName, lastName, email);
            if (inserted) {
                Toast.makeText(this, "Pengguna berhasil dibuat. Silakan login.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
            }
        });

        //Tombol kembali ke halaman login
        btnKembali.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
