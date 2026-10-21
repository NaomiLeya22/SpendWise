package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spendwise.R;
import com.example.spendwise.data.local.UserEntity;
import com.example.spendwise.data.repository.LocalRepository;
import com.example.spendwise.dto.AuthDtos;
import com.example.spendwise.network.ApiAuthSession;
import com.example.spendwise.network.TokenStore;
import com.example.spendwise.sync.NetworkSyncScheduler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilFullName;
    private TextInputEditText etFullName, etEmail, etPassword;
    private Button btnLogin;
    private TextView tvWelcome, tvLoginSubtitle, tvForgotPassword, tvNoAccount, tvRegister;

    private boolean isRegisterMode = false;
    private LocalRepository repository;
    private TokenStore tokenStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = new LocalRepository(this);
        tokenStore = new TokenStore(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvLoginSubtitle = findViewById(R.id.tvLoginSubtitle);
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvNoAccount = findViewById(R.id.tvNoAccount);
        tvRegister = findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(v -> toggleAuthMode());

        btnLogin.setOnClickListener(v -> {
            if (isRegisterMode) {
                handleRegister();
            } else {
                handleLogin();
            }
        });
    }

    private void toggleAuthMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            tvWelcome.setText("Create Account");
            tvLoginSubtitle.setText("Sign up to track expenses and set budgets.");
            tilFullName.setVisibility(View.VISIBLE);
            tvForgotPassword.setVisibility(View.GONE);
            btnLogin.setText("REGISTER");
            tvNoAccount.setText("Already have an account?");
            tvRegister.setText("Login");
        } else {
            tvWelcome.setText("Welcome Back");
            tvLoginSubtitle.setText("Login to manage your expenses and budget.");
            tilFullName.setVisibility(View.GONE);
            tvForgotPassword.setVisibility(View.VISIBLE);
            btnLogin.setText("LOGIN");
            tvNoAccount.setText("Don't have an account?");
            tvRegister.setText("Register");
        }
    }

    private void handleLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        ApiAuthSession.login(this, email, password, new ApiAuthSession.ResultCallback() {
            @Override
            public void onSuccess(AuthDtos.UserDto user) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    NetworkSyncScheduler.schedule(LoginActivity.this);
                    onAuthSuccess(user.id, user.fullName != null ? user.fullName : email, user.email);
                });
            }

            @Override
            public void onError(String message) {
                repository.getUserByEmail(email, localUser -> runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    if (localUser != null) {
                        onAuthSuccess(localUser.userId, localUser.fullName, localUser.email);
                        Toast.makeText(LoginActivity.this, "Logged in (offline mode)", Toast.LENGTH_SHORT).show();
                    } else {
                        UserEntity newUser = new UserEntity();
                        newUser.fullName = email.contains("@") ? email.split("@")[0] : email;
                        newUser.email = email;
                        newUser.passwordHash = password;
                        newUser.role = "user";
                        newUser.createdAt = System.currentTimeMillis();
                        newUser.updatedAt = newUser.createdAt;

                        repository.registerUser(newUser, id -> runOnUiThread(() -> {
                            onAuthSuccess(id != null ? id : 1L, newUser.fullName, newUser.email);
                            Toast.makeText(LoginActivity.this, "Account created & logged in", Toast.LENGTH_SHORT).show();
                        }));
                    }
                }));
            }
        });
    }

    private void handleRegister() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all registration fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        ApiAuthSession.register(this, fullName, email, password, new ApiAuthSession.ResultCallback() {
            @Override
            public void onSuccess(AuthDtos.UserDto user) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    NetworkSyncScheduler.schedule(LoginActivity.this);
                    onAuthSuccess(user.id, user.fullName, user.email);
                });
            }

            @Override
            public void onError(String message) {
                UserEntity newUser = new UserEntity();
                newUser.fullName = fullName;
                newUser.email = email;
                newUser.passwordHash = password;
                newUser.role = "user";
                newUser.createdAt = System.currentTimeMillis();
                newUser.updatedAt = newUser.createdAt;

                repository.registerUser(newUser, id -> runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    long userId = (id != null && id > 0) ? id : System.currentTimeMillis() % 10000;
                    onAuthSuccess(userId, fullName, email);
                    Toast.makeText(LoginActivity.this, "Registered successfully (offline mode)", Toast.LENGTH_SHORT).show();
                }));
            }
        });
    }

    private void onAuthSuccess(long userId, String name, String email) {
        String token = tokenStore.getToken();
        if (token == null || token.isEmpty()) {
            token = "local_token_" + System.currentTimeMillis();
        }
        tokenStore.saveSession(token, userId, name, email);

        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }
}
