package com.example.autotrack.dashboard.notifikasi;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.autotrack.R;
import com.example.autotrack.baseactivity.BaseActivity;
import com.example.autotrack.database.DatabaseHelper;

import java.util.Calendar;

public class TambahNotifikasiActivity extends BaseActivity {

    private EditText etBeban, etDate, etCatatan;
    private Button btnSave, btnBiaya1, btnLayanan2;;
    private DatabaseHelper dbHelper;
    private int userId;
    private String selectedTipe = "Biaya";

    private static final String CHANNEL_ID = "notif_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_notifikasi);

        // Setup toolbar dari BaseActivity
        setupToolbar(R.id.topAppBar, "Tambah Pengingat", true);

        createNotificationChannel();

        etBeban = findViewById(R.id.etBeban);
        etDate = findViewById(R.id.etDate);

        etDate.setInputType(InputType.TYPE_NULL); // supaya EditText tidak bisa diketik manual, hanya lewat date picker
        etDate.setFocusable(false);
        etDate.setClickable(true);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TambahNotifikasiActivity.this,
                        (view1, year1, monthOfYear, dayOfMonth) -> {
                            // Format tanggal: DD/MM/YYYY
                            String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                            etDate.setText(selectedDate);
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        etCatatan = findViewById(R.id.etCatatan);
        String catatan = etCatatan.getText().toString().trim();

        btnSave = findViewById(R.id.btnSave);

        btnBiaya1 = findViewById(R.id.btnBiaya1);
        btnLayanan2 = findViewById(R.id.btnLayanan2);

        btnBiaya1.setOnClickListener(v -> {
            setActiveButton(btnBiaya1, btnLayanan2);
            selectedTipe = "Biaya";
        });

        btnLayanan2.setOnClickListener(v -> {
            setActiveButton(btnLayanan2, btnBiaya1);
            selectedTipe = "Layanan";
        });


        setActiveButton(btnBiaya1, btnLayanan2);

        dbHelper = new DatabaseHelper(this);

        // Ambil username dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("login_pref", MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            userId = dbHelper.getUserIdByUsername(username);
        }



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String beban = etBeban.getText().toString().trim();
                String tanggal = etDate.getText().toString().trim();
                String catatan = etCatatan.getText().toString().trim();

                if (beban.isEmpty() || tanggal.isEmpty()) {
                    Toast.makeText(TambahNotifikasiActivity.this, "Isi semua field", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userId == -1) {
                    Toast.makeText(TambahNotifikasiActivity.this, "User tidak valid", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Panggil insertNotifikasi dengan userId
                boolean inserted = dbHelper.insertNotifikasi(userId, selectedTipe, beban, tanggal, catatan);
                if (inserted) {
                    showNotification(beban, tanggal);
                    Toast.makeText(TambahNotifikasiActivity.this, "Notifikasi ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TambahNotifikasiActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setActiveButton(Button activeBtn, Button inactiveBtn) {
        activeBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
        inactiveBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
    }

    private void showNotification(String title, String date) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText("Jadwal: " + date)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Notifikasi";
            String description = "Channel untuk pengingat";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
