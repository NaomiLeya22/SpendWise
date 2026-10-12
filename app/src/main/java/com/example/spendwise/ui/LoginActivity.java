package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.example.spendwise.R;

 public class LoginActivity extends AppCompatActivity {
        TextInputEditText etEmail, etPassword;
        Button btnLogin;
        TextView tvRegister;

        @Override protected void onCreate(Bundle s) {
            super.onCreate(s);
            setContentView(R.layout.activity_login);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            tvRegister = findViewById(R.id.tvRegister);

            btnLogin.setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
                if (TextUtils.isEmpty(pass)) { etPassword.setError("Required"); return; }
                // Mock successful login
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            });

            tvRegister.setOnClickListener(v -> {
                // For Week 3, reuse Login as register placeholder
                etEmail.setText("");
                etPassword.setText("");
            });
        }
    }


