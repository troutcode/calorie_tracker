package com.example.caltracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    EditText etAge, etWeight, etHeight;
    Spinner spGender, spActivity, spGoal;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp = getSharedPreferences("CalorieApp", MODE_PRIVATE);

        etAge      = findViewById(R.id.etAge);
        etWeight   = findViewById(R.id.etWeight);
        etHeight   = findViewById(R.id.etHeight);
        spGender   = findViewById(R.id.spGender);
        spActivity = findViewById(R.id.spActivity);
        spGoal     = findViewById(R.id.spGoal);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Male", "Female"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Sedentary", "Light", "Moderate", "Very Active"});
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActivity.setAdapter(activityAdapter);

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Maintain", "Lose Weight", "Gain Muscle"});
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGoal.setAdapter(goalAdapter);

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String age    = etAge.getText().toString().trim();
            String weight = etWeight.getText().toString().trim();
            String height = etHeight.getText().toString().trim();

            if (age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            sp.edit()
                    .putInt("age",      Integer.parseInt(age))
                    .putFloat("weight", Float.parseFloat(weight))
                    .putInt("height",   Integer.parseInt(height))
                    .putString("gender",   spGender.getSelectedItem().toString())
                    .putString("activity", spActivity.getSelectedItem().toString())
                    .putString("goal",     spGoal.getSelectedItem().toString())
                    .putBoolean("isProfileSaved", true)
                    .apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}