package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spendwise.R;
import com.example.spendwise.analytics.AnalyticsActivity;
import com.example.spendwise.network.TokenStore;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail, tvProfileId;
    private Button btnLogout;
    private BottomNavigationView bottomNavigation;

    private TokenStore tokenStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenStore = new TokenStore(this);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileId = findViewById(R.id.tvProfileId);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        String name = tokenStore.getFullName();
        String email = tokenStore.getEmail();
        long userId = tokenStore.getServerUserId();

        if (name != null && !name.trim().isEmpty()) {
            tvProfileName.setText(name);
        } else {
            tvProfileName.setText("SpendWise User");
        }

        if (email != null && !email.trim().isEmpty()) {
            tvProfileEmail.setText(email);
        } else {
            tvProfileEmail.setText("No email saved");
        }

        tvProfileId.setText("User ID: #" + (userId > 0 ? userId : "Local"));

        btnLogout.setOnClickListener(v -> {
            tokenStore.clearSession();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        if (bottomNavigation == null) return;
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_transactions) {
                startActivity(new Intent(this, TransactionsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_budgets) {
                startActivity(new Intent(this, BudgetsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
