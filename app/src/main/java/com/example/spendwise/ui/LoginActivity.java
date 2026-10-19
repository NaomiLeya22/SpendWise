package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spendwise.R;
import com.example.spendwise.dto.AuthDtos;
import com.example.spendwise.network.ApiAuthSession;
import com.example.spendwise.sync.NetworkSyncScheduler;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginButton.setEnabled(false);

            ApiAuthSession.login(this, email, password,
                    new ApiAuthSession.ResultCallback() {
                        @Override
                        public void onSuccess(AuthDtos.UserDto user) {
                            runOnUiThread(() -> {
                                loginButton.setEnabled(true);
                                NetworkSyncScheduler.schedule(LoginActivity.this);
                                startActivity(new Intent(
                                        LoginActivity.this, DashboardActivity.class
                                ));
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> {
                                loginButton.setEnabled(true);
                                Toast.makeText(LoginActivity.this, message,
                                        Toast.LENGTH_LONG).show();
                            });
                        }
                    });
        });

        tvRegister.setOnClickListener(v -> {
            if (emailEditText != null) emailEditText.setText("");
            if (passwordEditText != null) passwordEditText.setText("");
        });
    }
}
