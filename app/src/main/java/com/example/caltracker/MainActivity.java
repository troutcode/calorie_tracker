package com.example.caltracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    ListView lvMeals;
    TextView tvEatenTotal, tvTarget, tvProgressLabel, tvName, tvGoalPill;
    ProgressBar progressCalorie;
    Spinner spFood;
    EditText etCustomName, etCustomCal;
    LinearLayout panelPreset, panelCustom;
    Button btnTabPreset, btnTabCustom;

    ArrayList<String> mealList = new ArrayList<>();
    ArrayAdapter<String> mealAdapter;
    int totalCalories = 0;
    int targetCalories = 0;

    String[] foodNames = {
            "Rice (1 cup)", "Pad Thai (1 plate)", "Tom Yum soup", "Egg (1 pc)",
            "Chicken breast (100g)", "Banana", "Orange", "Milk (1 glass)",
            "Bread (1 slice)", "Fried rice (1 plate)", "Som Tum", "Mango sticky rice",
            "Green curry (1 bowl)", "Yogurt (1 cup)", "Boiled pork (100g)"
    };

    int[] foodCalories = {
            206, 282, 100, 78,
            165, 89, 62, 122,
            79, 350, 98, 420,
            180, 100, 215
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("CalorieApp", MODE_PRIVATE);

        tvName          = findViewById(R.id.tvName);
        tvGoalPill      = findViewById(R.id.tvGoalPill);
        tvEatenTotal    = findViewById(R.id.tvEatenTotal);
        tvTarget        = findViewById(R.id.tvTarget);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        progressCalorie = findViewById(R.id.progressCalorie);
        spFood          = findViewById(R.id.spFood);
        lvMeals         = findViewById(R.id.lvMeals);
        etCustomName    = findViewById(R.id.etCustomName);
        etCustomCal     = findViewById(R.id.etCustomCal);
        panelPreset     = findViewById(R.id.panelPreset);
        panelCustom     = findViewById(R.id.panelCustom);
        btnTabPreset    = findViewById(R.id.btnTabPreset);
        btnTabCustom    = findViewById(R.id.btnTabCustom);

        tvName.setText(sp.getString("username", "User"));
        tvGoalPill.setText("Goal: " + sp.getString("goal", "Maintain"));

        targetCalories = calculateTarget();
        tvTarget.setText(targetCalories + " kcal");

        // Restore today's log from SP
        totalCalories = sp.getInt("todayCalories", 0);
        String savedLog = sp.getString("todayMealLog", "");
        if (!savedLog.isEmpty()) {
            for (String item : savedLog.split("\\|")) {
                mealList.add(item);
            }
        }

        // Food spinner
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, foodNames);
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFood.setAdapter(foodAdapter);

        // Meal list
        mealAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mealList);
        lvMeals.setAdapter(mealAdapter);

        updateCalorieDisplay();

        // Tab switching
        btnTabPreset.setOnClickListener(v -> {
            panelPreset.setVisibility(View.VISIBLE);
            panelCustom.setVisibility(View.GONE);
            btnTabPreset.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF4F46E5));
            btnTabPreset.setTextColor(0xFFFFFFFF);
            btnTabCustom.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFE0E7FF));
            btnTabCustom.setTextColor(0xFF4F46E5);
        });

        btnTabCustom.setOnClickListener(v -> {
            panelPreset.setVisibility(View.GONE);
            panelCustom.setVisibility(View.VISIBLE);
            btnTabCustom.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF4F46E5));
            btnTabCustom.setTextColor(0xFFFFFFFF);
            btnTabPreset.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFE0E7FF));
            btnTabPreset.setTextColor(0xFF4F46E5);
        });

        // Add preset food
        findViewById(R.id.btnAddPreset).setOnClickListener(v -> {
            int idx = spFood.getSelectedItemPosition();
            String entry = foodNames[idx] + " — " + foodCalories[idx] + " kcal";
            mealList.add(entry);
            totalCalories += foodCalories[idx];
            mealAdapter.notifyDataSetChanged();
            updateCalorieDisplay();
            saveTodayLog();
            Toast.makeText(this, foodNames[idx] + " added!", Toast.LENGTH_SHORT).show();
        });

        // Add custom food
        findViewById(R.id.btnAddCustom).setOnClickListener(v -> {
            String name = etCustomName.getText().toString().trim();
            String calStr = etCustomCal.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a food name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (calStr.isEmpty()) {
                Toast.makeText(this, "Please enter calories", Toast.LENGTH_SHORT).show();
                return;
            }

            int cal = Integer.parseInt(calStr);
            if (cal <= 0) {
                Toast.makeText(this, "Calories must be more than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            String entry = name + " — " + cal + " kcal";
            mealList.add(entry);
            totalCalories += cal;
            mealAdapter.notifyDataSetChanged();
            updateCalorieDisplay();
            saveTodayLog();

            // Clear fields after adding
            etCustomName.setText("");
            etCustomCal.setText("");
            Toast.makeText(this, name + " added!", Toast.LENGTH_SHORT).show();
        });

        // See Result
        findViewById(R.id.btnResult).setOnClickListener(v -> {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("totalCalories", totalCalories);
            startActivity(intent);
        });

        // History
        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        // Tips
        findViewById(R.id.btnTips).setOnClickListener(v ->
                startActivity(new Intent(this, TipsActivity.class)));

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sp.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private int calculateTarget() {
        int age         = sp.getInt("age", 25);
        float weight    = sp.getFloat("weight", 60f);
        int height      = sp.getInt("height", 165);
        String gender   = sp.getString("gender", "Male");
        String activity = sp.getString("activity", "Sedentary");
        String goal     = sp.getString("goal", "Maintain");

        double bmr;
        if (gender.equals("Male")) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        double factor;
        switch (activity) {
            case "Light":       factor = 1.375; break;
            case "Moderate":    factor = 1.55;  break;
            case "Very Active": factor = 1.725; break;
            default:            factor = 1.2;   break;
        }

        double tdee = bmr * factor;

        switch (goal) {
            case "Lose Weight":  return (int)(tdee - 500);
            case "Gain Muscle":  return (int)(tdee + 300);
            default:             return (int) tdee;
        }
    }

    private void updateCalorieDisplay() {
        tvEatenTotal.setText(totalCalories + " kcal");
        if (targetCalories > 0) {
            int percent = Math.min((totalCalories * 100) / targetCalories, 100);
            progressCalorie.setProgress(percent);
            tvProgressLabel.setText(percent + "% of target");
        }
    }

    private void saveTodayLog() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mealList.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(mealList.get(i));
        }
        sp.edit()
                .putInt("todayCalories", totalCalories)
                .putString("todayMealLog", sb.toString())
                .apply();
    }
}