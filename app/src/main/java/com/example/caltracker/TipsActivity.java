package com.example.caltracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.widget.TextView;

public class TipsActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        sp = getSharedPreferences("CalorieApp", MODE_PRIVATE);
        String goal = sp.getString("goal", "Maintain");

        ((TextView) findViewById(R.id.tvTipsGoalLabel)).setText("Tips for your goal: " + goal);

        TextView tvTip1 = findViewById(R.id.tvTip1);
        TextView tvTip2 = findViewById(R.id.tvTip2);
        TextView tvTip3 = findViewById(R.id.tvTip3);
        TextView tvTip4 = findViewById(R.id.tvTip4);
        TextView tvTip5 = findViewById(R.id.tvTip5);

        if (goal.equals("Lose Weight")) {
            tvTip1.setText("Eat at a 500 kcal deficit daily. This creates roughly 0.5 kg of fat loss per week without harming your metabolism.");
            tvTip2.setText("Prioritize protein in every meal. Protein keeps you full longer and prevents muscle loss while in a calorie deficit.");
            tvTip3.setText("Avoid liquid calories — sugary drinks, juice, and alcohol add up fast without making you feel full.");
            tvTip4.setText("Meal timing: Eat your largest meal before 2pm. Evening meals should be lighter — mostly protein and vegetables.");
            tvTip5.setText("Hydration: Drink a full glass of water before each meal. It reduces hunger and helps you eat less naturally.");
        } else if (goal.equals("Gain Muscle")) {
            tvTip1.setText("Eat at a 300 kcal surplus daily. More than this leads to unnecessary fat gain. Slow and steady builds lean muscle.");
            tvTip2.setText("Aim for 1.6–2.2g of protein per kg of bodyweight. For a 65kg person that is 104–143g of protein per day.");
            tvTip3.setText("Carbohydrates are your friend. They fuel your workouts and replenish glycogen. Do not cut them when bulking.");
            tvTip4.setText("Meal timing: Have a protein and carb meal within 1 hour after training. This is your most important meal of the day.");
            tvTip5.setText("Hydration: Aim for 3 litres of water daily. Muscles are 75% water — dehydration directly reduces strength.");
        } else {
            tvTip1.setText("Eat at your TDEE — not above, not below. Use the app daily to stay consistent and aware of your intake.");
            tvTip2.setText("Balance your plate: half vegetables, a quarter protein, a quarter carbohydrates. This ratio works for most people.");
            tvTip3.setText("Do not skip meals. Skipping leads to overeating later. Three balanced meals and one small snack is ideal.");
            tvTip4.setText("Meal timing: Breakfast within 1 hour of waking keeps your metabolism active throughout the day.");
            tvTip5.setText("Hydration: Drink at least 2 litres of water daily. Thirst is often mistaken for hunger — drink first, then decide.");
        }

        findViewById(R.id.btnBackTips).setOnClickListener(v -> finish());
    }
}