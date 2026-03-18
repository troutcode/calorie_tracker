package com.example.caltracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        sp = getSharedPreferences("CalorieApp", MODE_PRIVATE);

        int eaten       = getIntent().getIntExtra("totalCalories", 0);
        int age         = sp.getInt("age", 25);
        float weight    = sp.getFloat("weight", 60f);
        int height      = sp.getInt("height", 165);
        String gender   = sp.getString("gender", "Male");
        String activity = sp.getString("activity", "Sedentary");
        String goal     = sp.getString("goal", "Maintain");

        // BMR
        double bmr;
        if (gender.equals("Male")) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        // TDEE
        double factor;
        switch (activity) {
            case "Light":       factor = 1.375; break;
            case "Moderate":    factor = 1.55;  break;
            case "Very Active": factor = 1.725; break;
            default:            factor = 1.2;   break;
        }
        double tdee = bmr * factor;

        // Target
        double target;
        switch (goal) {
            case "Lose Weight":  target = tdee - 500; break;
            case "Gain Muscle":  target = tdee + 300; break;
            default:             target = tdee;        break;
        }

        // Macros
        int proteinG = (int)((target * 0.25) / 4);
        int carbsG   = (int)((target * 0.50) / 4);
        int fatG     = (int)((target * 0.25) / 9);

        // BMI
        double heightM = height / 100.0;
        double bmi = weight / (heightM * heightM);
        String bmiCategory;
        int bmiColor;
        if (bmi < 18.5) {
            bmiCategory = "Underweight";   bmiColor = 0xFF2563EB;
        } else if (bmi < 25) {
            bmiCategory = "Normal weight"; bmiColor = 0xFF16A34A;
        } else if (bmi < 30) {
            bmiCategory = "Overweight";    bmiColor = 0xFFD97706;
        } else {
            bmiCategory = "Obese";         bmiColor = 0xFFDC2626;
        }

        // Score
        double ratio = (target > 0) ? eaten / target : 0;
        int score;
        String status;
        if (ratio <= 0.5) {
            score = (int)(ratio * 60);
            status = "Eating too little";
        } else if (ratio <= 0.85) {
            score = (int)(50 + ratio * 40);
            status = "Slightly under target";
        } else if (ratio <= 1.10) {
            score = (int)(85 + (1 - Math.abs(1 - ratio)) * 15);
            status = "On track — great job!";
        } else if (ratio <= 1.30) {
            score = (int)(70 - (ratio - 1.1) * 100);
            status = "Slightly over target";
        } else {
            score = Math.max(20, (int)(50 - (ratio - 1.3) * 100));
            status = "Significantly over target";
        }
        score = Math.max(0, Math.min(100, score));

        // Advice
        String advice;
        if (ratio < 0.5) {
            advice = "You're eating far too little. Add a balanced meal — rice + protein + vegetables.";
        } else if (ratio < 0.85) {
            advice = "You still have " + (int)(target - eaten) + " kcal left. Try a snack: banana + peanut butter, or Greek yogurt.";
        } else if (ratio <= 1.10) {
            advice = "Excellent balance! Keep this up. Drink at least 8 glasses of water and prioritize sleep.";
        } else if (ratio <= 1.30) {
            advice = "Slightly over target. Skip heavy snacks tonight and go for a 20-minute walk.";
        } else {
            advice = "You exceeded your goal. Tomorrow start with a protein-rich breakfast and reduce carbs.";
        }
        if (goal.equals("Gain Muscle") && ratio < 1.0) {
            advice += "\n\nTip: You need a surplus to build muscle. Add a post-workout meal — rice + chicken.";
        }
        if (goal.equals("Lose Weight") && ratio < 0.8) {
            advice += "\n\nTip: For fat loss, keep protein high to preserve muscle while in a deficit.";
        }

        // Save to history
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String entry = date + " | " + eaten + " kcal | " + status;
        String existing = sp.getString("historyLog", "");
        String newHistory = entry + (existing.isEmpty() ? "" : "\n" + existing);
        String[] lines = newHistory.split("\n");
        StringBuilder trimmed = new StringBuilder();
        for (int i = 0; i < Math.min(7, lines.length); i++) {
            if (i > 0) trimmed.append("\n");
            trimmed.append(lines[i]);
        }
        sp.edit().putString("historyLog", trimmed.toString()).apply();

        // Bind views
        ((TextView) findViewById(R.id.tvScore)).setText(score + "/100");
        ((TextView) findViewById(R.id.tvScoreStatus)).setText(status);

        TextView tvBMI = findViewById(R.id.tvBMI);
        TextView tvBMICat = findViewById(R.id.tvBMICategory);
        tvBMI.setText(String.format(Locale.getDefault(), "%.1f", bmi));
        tvBMICat.setText(bmiCategory);
        tvBMICat.setTextColor(bmiColor);

        ((TextView) findViewById(R.id.tvCalorieSummary)).setText(
                "BMR: " + (int)bmr + " kcal\n" +
                        "TDEE: " + (int)tdee + " kcal\n" +
                        "Target: " + (int)target + " kcal\n" +
                        "Eaten: " + eaten + " kcal\n" +
                        "Remaining: " + (int)(target - eaten) + " kcal"
        );

        int proteinEaten = (int)(eaten * 0.25 / 4);
        int carbsEaten   = (int)(eaten * 0.50 / 4);
        int fatEaten     = (int)(eaten * 0.25 / 9);

        ((ProgressBar) findViewById(R.id.pbProtein)).setProgress(
                proteinG > 0 ? Math.min(proteinEaten * 100 / proteinG, 100) : 0);
        ((TextView) findViewById(R.id.tvProtein)).setText(proteinEaten + " / " + proteinG + " g");

        ((ProgressBar) findViewById(R.id.pbCarbs)).setProgress(
                carbsG > 0 ? Math.min(carbsEaten * 100 / carbsG, 100) : 0);
        ((TextView) findViewById(R.id.tvCarbs)).setText(carbsEaten + " / " + carbsG + " g");

        ((ProgressBar) findViewById(R.id.pbFat)).setProgress(
                fatG > 0 ? Math.min(fatEaten * 100 / fatG, 100) : 0);
        ((TextView) findViewById(R.id.tvFat)).setText(fatEaten + " / " + fatG + " g");

        ((TextView) findViewById(R.id.tvAdvice)).setText("Recommendation:\n\n" + advice);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}