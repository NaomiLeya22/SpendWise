package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.spendwise.R;
import com.example.spendwise.analytics.AnalyticsActivity;
import com.example.spendwise.data.local.BudgetEntity;
import com.example.spendwise.data.local.TransactionEntity;
import com.example.spendwise.data.repository.LocalRepository;
import com.example.spendwise.network.TokenStore;
import com.example.spendwise.ui.viewmodel.TransactionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetsActivity extends AppCompatActivity {

    private TextView tvCurrentPeriod, tvBudgetSpentTotal, tvBudgetRemaining;
    private ProgressBar pbBudget;
    private TextInputEditText etBudgetAmount;
    private Button btnSaveBudget;
    private BottomNavigationView bottomNavigation;

    private LocalRepository repository;
    private TokenStore tokenStore;
    private TransactionViewModel txViewModel;

    private double currentBudgetLimit = 5000.0;
    private double currentTotalExpenses = 0.0;
    private BudgetEntity activeBudgetEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);

        repository = new LocalRepository(this);
        tokenStore = new TokenStore(this);
        txViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        tvCurrentPeriod = findViewById(R.id.tvCurrentPeriod);
        tvBudgetSpentTotal = findViewById(R.id.tvBudgetSpentTotal);
        tvBudgetRemaining = findViewById(R.id.tvBudgetRemaining);
        pbBudget = findViewById(R.id.pbBudget);
        etBudgetAmount = findViewById(R.id.etBudgetAmount);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        String currentPeriod = getCurrentPeriod();
        tvCurrentPeriod.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()));

        long userId = tokenStore.getServerUserId() > 0 ? tokenStore.getServerUserId() : 1L;

        repository.getBudgetsLive(userId, currentPeriod).observe(this, budgets -> {
            if (budgets != null && !budgets.isEmpty()) {
                activeBudgetEntity = budgets.get(0);
                currentBudgetLimit = activeBudgetEntity.amount;
            }
            updateBudgetUI();
        });

        txViewModel.getTransactions().observe(this, transactions -> {
            currentTotalExpenses = 0.0;
            if (transactions != null) {
                for (TransactionEntity t : transactions) {
                    if ("expense".equalsIgnoreCase(t.type)) {
                        currentTotalExpenses += t.amount;
                    }
                }
            }
            updateBudgetUI();
        });

        btnSaveBudget.setOnClickListener(v -> {
            String input = etBudgetAmount.getText() != null ? etBudgetAmount.getText().toString().trim() : "";
            if (TextUtils.isEmpty(input)) {
                etBudgetAmount.setError("Enter amount");
                return;
            }
            try {
                double newLimit = Double.parseDouble(input);
                if (newLimit <= 0) {
                    etBudgetAmount.setError("Amount must be > 0");
                    return;
                }
                saveNewBudget(newLimit, currentPeriod, userId);
            } catch (NumberFormatException e) {
                etBudgetAmount.setError("Invalid number");
            }
        });

        setupBottomNavigation();
    }

    private String getCurrentPeriod() {
        return new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
    }

    private void updateBudgetUI() {
        double remaining = currentBudgetLimit - currentTotalExpenses;
        int progress = currentBudgetLimit > 0 ? (int) Math.min(100, (currentTotalExpenses / currentBudgetLimit) * 100) : 0;

        tvBudgetSpentTotal.setText(String.format(Locale.getDefault(), "Spent: ZMW %,.2f / ZMW %,.2f", currentTotalExpenses, currentBudgetLimit));
        pbBudget.setProgress(progress);
        tvBudgetRemaining.setText(String.format(Locale.getDefault(), "Remaining: ZMW %,.2f", remaining));
        etBudgetAmount.setText(String.format(Locale.getDefault(), "%.2f", currentBudgetLimit));
    }

    private void saveNewBudget(double newLimit, String period, long userId) {
        if (activeBudgetEntity == null) {
            activeBudgetEntity = new BudgetEntity();
            activeBudgetEntity.userId = userId;
            activeBudgetEntity.period = period;
            activeBudgetEntity.createdAt = System.currentTimeMillis();
        }
        activeBudgetEntity.amount = newLimit;
        activeBudgetEntity.updatedAt = System.currentTimeMillis();

        repository.addOrUpdateBudget(activeBudgetEntity);
        currentBudgetLimit = newLimit;
        updateBudgetUI();
        Toast.makeText(this, "Monthly budget updated", Toast.LENGTH_SHORT).show();
    }

    private void setupBottomNavigation() {
        if (bottomNavigation == null) return;
        bottomNavigation.setSelectedItemId(R.id.nav_budgets);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_budgets) {
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_transactions) {
                startActivity(new Intent(this, TransactionsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
