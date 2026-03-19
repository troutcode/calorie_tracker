package com.example.caltracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;
import android.content.SharedPreferences;

public class HistoryActivity extends AppCompatActivity {

    SharedPreferences sp;
    ListView lvHistory;
    ArrayAdapter<String> adapter;
    Button btnFakeWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sp = getSharedPreferences("CalorieApp", MODE_PRIVATE);
        lvHistory = findViewById(R.id.lvHistory);

        findViewById(R.id.btnFakeWeek).setOnClickListener(v -> {
            String fakeHistory =
                    "19/03/2026 | 1820 kcal | On track — great job!\n" +
                            "18/03/2026 | 1200 kcal | Slightly under target\n" +
                            "17/03/2026 | 2100 kcal | Slightly over target\n" +
                            "16/03/2026 | 950 kcal | Eating too little\n" +
                            "15/03/2026 | 1680 kcal | On track — great job!\n" +
                            "14/03/2026 | 1950 kcal | Slightly over target\n" +
                            "13/03/2026 | 1750 kcal | On track — great job!";
            sp.edit().putString("historyLog", fakeHistory).apply();
            loadHistory();
            Toast.makeText(this, "Test week loaded!", Toast.LENGTH_SHORT).show();
        });

        loadHistory();

        findViewById(R.id.btnClearHistory).setOnClickListener(v -> {
            sp.edit().putString("historyLog", "").apply();
            loadHistory();
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnBackHistory).setOnClickListener(v -> finish());
    }

    private void loadHistory() {
        String log = sp.getString("historyLog", "");
        String[] entries = log.isEmpty()
                ? new String[]{"No history yet"}
                : log.split("\n");
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, entries);
        lvHistory.setAdapter(adapter);
    }


}