package com.example.caltracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.*;
import android.content.SharedPreferences;

public class HistoryActivity extends AppCompatActivity {

    SharedPreferences sp;
    ListView lvHistory;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sp = getSharedPreferences("CalorieApp", MODE_PRIVATE);
        lvHistory = findViewById(R.id.lvHistory);

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