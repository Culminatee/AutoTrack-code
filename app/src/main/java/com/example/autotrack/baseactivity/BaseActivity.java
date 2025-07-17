package com.example.autotrack.baseactivity;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class BaseActivity extends AppCompatActivity {

    protected MaterialToolbar toolbar;

    protected void setupToolbar(int toolbarId, String title, boolean showBackButton) {
        toolbar = findViewById(toolbarId);
        if (toolbar != null) {
            toolbar.setTitle(title);
            toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
            toolbar.setBackgroundColor(Color.parseColor("#2196F3"));

            setSupportActionBar(toolbar);

            if (showBackButton) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(v -> onBackPressed());

                toolbar.setNavigationIconTint(ContextCompat.getColor(this, android.R.color.white));
            }
        }
    }
}

