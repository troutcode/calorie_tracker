package com.example.caltracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
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
        tvGoalPill.setText(getString(R.string.label_goal) + ": " + sp.getString("goal", ""));

        targetCalories = calculateTarget();
        tvTarget.setText(targetCalories + " kcal");

        totalCalories = sp.getInt("todayCalories", 0);
        String savedLog = sp.getString("todayMealLog", "");
        if (!savedLog.isEmpty()) {
            for (String item : savedLog.split("\\|")) {
                mealList.add(item);
            }
        }

        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, foodNames);
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFood.setAdapter(foodAdapter);

        mealAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mealList);
        lvMeals.setAdapter(mealAdapter);
        lvMeals.setOnItemLongClickListener((parent, view, position, id) -> {
            String item = mealList.get(position);

            // Extract calories from the entry string e.g. "Rice — 206 kcal"
            int cal = 0;
            try {
                String[] parts = item.split("— ");
                String calPart = parts[1].replace(" kcal", "").trim();
                cal = Integer.parseInt(calPart);
            } catch (Exception e) {
                cal = 0;
            }

            final int calToRemove = cal;

            // Confirm before deleting
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Remove item?")
                    .setMessage(item)
                    .setPositiveButton("Remove", (dialog, which) -> {
                        mealList.remove(position);
                        totalCalories -= calToRemove;
                        if (totalCalories < 0) totalCalories = 0;
                        mealAdapter.notifyDataSetChanged();
                        updateCalorieDisplay();
                        saveTodayLog();
                        Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

        updateCalorieDisplay();

        // 3 dot menu
        findViewById(R.id.btnMenu).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_settings) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.menu_logout) {
                    sp.edit()
                            .remove("isLoggedIn")
                            .remove("username")
                            .remove("todayCalories")
                            .remove("todayMealLog")
                            .apply();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Tab switching
        btnTabPreset.setOnClickListener(v -> showPreset());
        btnTabCustom.setOnClickListener(v -> showCustom());

        // Add preset
        findViewById(R.id.btnAddPreset).setOnClickListener(v -> {
            int idx = spFood.getSelectedItemPosition();
            String entry = foodNames[idx] + " — " + foodCalories[idx] + " kcal";
            mealList.add(entry);
            totalCalories += foodCalories[idx];
            mealAdapter.notifyDataSetChanged();
            updateCalorieDisplay();
            saveTodayLog();
            Toast.makeText(this, foodNames[idx] + " " + getString(R.string.added), Toast.LENGTH_SHORT).show();
        });

        // Add custom
        findViewById(R.id.btnAddCustom).setOnClickListener(v -> {
            String name   = etCustomName.getText().toString().trim();
            String calStr = etCustomCal.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_enter_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (calStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_enter_cal), Toast.LENGTH_SHORT).show();
                return;
            }
            int cal = Integer.parseInt(calStr);
            if (cal <= 0) {
                Toast.makeText(this, getString(R.string.error_cal_zero), Toast.LENGTH_SHORT).show();
                return;
            }

            mealList.add(name + " — " + cal + " kcal");
            totalCalories += cal;
            mealAdapter.notifyDataSetChanged();
            updateCalorieDisplay();
            saveTodayLog();
            etCustomName.setText("");
            etCustomCal.setText("");
            Toast.makeText(this, name + " " + getString(R.string.added), Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnResult).setOnClickListener(v -> {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("totalCalories", totalCalories);
            startActivity(intent);
        });

        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        findViewById(R.id.btnTips).setOnClickListener(v ->
                startActivity(new Intent(this, TipsActivity.class)));
    }

    private void showPreset() {
        panelPreset.setVisibility(View.VISIBLE);
        panelCustom.setVisibility(View.GONE);
        btnTabPreset.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF4F46E5));
        btnTabPreset.setTextColor(0xFFFFFFFF);
        btnTabCustom.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFE0E7FF));
        btnTabCustom.setTextColor(0xFF4F46E5);
    }

    private void showCustom() {
        panelPreset.setVisibility(View.GONE);
        panelCustom.setVisibility(View.VISIBLE);
        btnTabCustom.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF4F46E5));
        btnTabCustom.setTextColor(0xFFFFFFFF);
        btnTabPreset.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFE0E7FF));
        btnTabPreset.setTextColor(0xFF4F46E5);
    }

    private int calculateTarget() {
        int age         = sp.getInt("age", 25);
        float weight    = sp.getFloat("weight", 60f);
        int height      = sp.getInt("height", 165);
        String gender   = sp.getString("gender", "Male");
        String activity = sp.getString("activity", "Sedentary");
        String goal     = sp.getString("goal", "Maintain");

        double bmr;
        if (gender.equals("Male") || gender.equals("ชาย")) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        double factor;
        if (activity.equals("Light") || activity.equals("เคลื่อนไหวเล็กน้อย")) {
            factor = 1.375;
        } else if (activity.equals("Moderate") || activity.equals("เคลื่อนไหวปานกลาง")) {
            factor = 1.55;
        } else if (activity.equals("Very Active") || activity.equals("เคลื่อนไหวมาก")) {
            factor = 1.725;
        } else {
            factor = 1.2;
        }

        double tdee = bmr * factor;

        if (goal.equals("Lose Weight") || goal.equals("ลดน้ำหนัก")) {
            return (int)(tdee - 500);
        } else if (goal.equals("Gain Muscle") || goal.equals("เพิ่มกล้ามเนื้อ")) {
            return (int)(tdee + 300);
        } else {
            return (int) tdee;
        }
    }

    private void updateCalorieDisplay() {
        tvEatenTotal.setText(totalCalories + " kcal");
        if (targetCalories > 0) {
            int percent = Math.min((totalCalories * 100) / targetCalories, 100);
            progressCalorie.setProgress(percent);
            tvProgressLabel.setText(percent + "%");
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