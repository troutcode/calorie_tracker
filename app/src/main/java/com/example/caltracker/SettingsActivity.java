package com.example.caltracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.widget.*;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sp;
    Switch switchDarkMode;
    Spinner spLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp             = getSharedPreferences("CalorieApp", MODE_PRIVATE);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        spLanguage     = findViewById(R.id.spLanguage);

        // Load current settings
        boolean isDark   = sp.getBoolean("darkMode", false);
        String language  = sp.getString("language", "en");

        switchDarkMode.setChecked(isDark);
        spLanguage.setSelection(language.equals("th") ? 1 : 0);

        // Apply button
        findViewById(R.id.btnApplySettings).setOnClickListener(v -> {
            boolean darkMode = switchDarkMode.isChecked();
            String lang = spLanguage.getSelectedItemPosition() == 1 ? "th" : "en";

            // Save to SP
            sp.edit()
                    .putBoolean("darkMode", darkMode)
                    .putString("language", lang)
                    .apply();

            // Apply dark mode immediately
            if (darkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Apply language
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = getResources().getConfiguration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            // Restart app from Login so language applies to all screens
            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        findViewById(R.id.btnBackSettings).setOnClickListener(v -> finish());
    }
}