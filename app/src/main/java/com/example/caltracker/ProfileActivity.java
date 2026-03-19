package com.example.caltracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    EditText etAge, etWeight, etHeight;
    Spinner spGender, spActivity, spGoal;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp         = getSharedPreferences("CalorieApp", MODE_PRIVATE);
        etAge      = findViewById(R.id.etAge);
        etWeight   = findViewById(R.id.etWeight);
        etHeight   = findViewById(R.id.etHeight);
        spGender   = findViewById(R.id.spGender);
        spActivity = findViewById(R.id.spActivity);
        spGoal     = findViewById(R.id.spGoal);

        // Spinners use @array/gender_options etc from XML already
        // so no adapter needed here — Android loads them automatically

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String age    = etAge.getText().toString().trim();
            String weight = etWeight.getText().toString().trim();
            String height = etHeight.getText().toString().trim();

            if (age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_fill_all), Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the English key values regardless of language
            // so calculateTarget() always works correctly
            String[] genderKeys   = {"Male", "Female"};
            String[] activityKeys = {"Sedentary", "Light", "Moderate", "Very Active"};
            String[] goalKeys     = {"Maintain", "Lose Weight", "Gain Muscle"};

            sp.edit()
                    .putInt("age",      Integer.parseInt(age))
                    .putFloat("weight", Float.parseFloat(weight))
                    .putInt("height",   Integer.parseInt(height))
                    .putString("gender",   genderKeys[spGender.getSelectedItemPosition()])
                    .putString("activity", activityKeys[spActivity.getSelectedItemPosition()])
                    .putString("goal",     goalKeys[spGoal.getSelectedItemPosition()])
                    .putBoolean("isProfileSaved", true)
                    .apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}