package com.example.caltracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("CalorieApp", MODE_PRIVATE);

        if (sp.getBoolean("isLoggedIn", false)) {
            if (sp.getBoolean("isProfileSaved", false)) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 4) {
                Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            sp.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("username", user)
                    .apply();

            if (sp.getBoolean("isProfileSaved", false)) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            finish();
        });
    }
}