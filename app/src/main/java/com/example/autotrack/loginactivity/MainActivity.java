package com.example.autotrack.loginactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autotrack.R;
import com.example.autotrack.dashboard.DashboardActivity;
import com.example.autotrack.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    CheckBox cbRememberMe;
    TextView btnSignUp;
    Button btnLogin;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "login_pref";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Jika remember aktif, langsung ke Home
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Harap isi username dan password", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = dbHelper.checkUser(username, password);
                if (isValid) {
                    int userId = dbHelper.getUserIdByUsername(username);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_REMEMBER, cbRememberMe.isChecked());
                    editor.putString(KEY_USERNAME, username);
                    editor.putInt("user_id", userId);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Login Gagal: Username atau password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        });

    }
}
