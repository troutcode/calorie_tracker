package com.example.caltracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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

        ((TextView) findViewById(R.id.tvTipsGoalLabel)).setText(
                getString(R.string.tips_goal_prefix) + goal);

        TextView tvTip1 = findViewById(R.id.tvTip1);
        TextView tvTip2 = findViewById(R.id.tvTip2);
        TextView tvTip3 = findViewById(R.id.tvTip3);
        TextView tvTip4 = findViewById(R.id.tvTip4);
        TextView tvTip5 = findViewById(R.id.tvTip5);

        if (goal.equals("Lose Weight")) {
            tvTip1.setText(R.string.tip_lose_1);
            tvTip2.setText(R.string.tip_lose_2);
            tvTip3.setText(R.string.tip_lose_3);
            tvTip4.setText(R.string.tip_lose_4);
            tvTip5.setText(R.string.tip_lose_5);
        } else if (goal.equals("Gain Muscle")) {
            tvTip1.setText(R.string.tip_gain_1);
            tvTip2.setText(R.string.tip_gain_2);
            tvTip3.setText(R.string.tip_gain_3);
            tvTip4.setText(R.string.tip_gain_4);
            tvTip5.setText(R.string.tip_gain_5);
        } else {
            tvTip1.setText(R.string.tip_maintain_1);
            tvTip2.setText(R.string.tip_maintain_2);
            tvTip3.setText(R.string.tip_maintain_3);
            tvTip4.setText(R.string.tip_maintain_4);
            tvTip5.setText(R.string.tip_maintain_5);
        }

        findViewById(R.id.btnBackTips).setOnClickListener(v -> finish());
    }
}