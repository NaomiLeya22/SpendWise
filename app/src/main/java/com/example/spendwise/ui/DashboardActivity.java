package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.analytics.AnalyticsActivity;
import com.example.spendwise.data.local.TransactionEntity;
import com.example.spendwise.network.TokenStore;
import com.example.spendwise.ui.adapter.TransactionAdapter;
import com.example.spendwise.ui.viewmodel.DashboardViewModel;
import com.example.spendwise.ui.viewmodel.TransactionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvGreeting, tvBalance, tvIncome, tvExpenses, tvBudgetAmount, tvRemaining;
    private ProgressBar budgetProgress;
    private RecyclerView rvTransactions;
    private FloatingActionButton fabAdd;
    private BottomNavigationView bottomNavigation;

    private TransactionAdapter adapter;
    private TransactionViewModel txViewModel;
    private DashboardViewModel dashboardViewModel;
    private TokenStore tokenStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tokenStore = new TokenStore(this);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvBalance = findViewById(R.id.tvBalance);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpenses = findViewById(R.id.tvExpenses);
        tvBudgetAmount = findViewById(R.id.tvBudget);
        tvRemaining = findViewById(R.id.tvRemaining);
        budgetProgress = findViewById(R.id.budgetProgress);
        rvTransactions = findViewById(R.id.rvTransactions);
        fabAdd = findViewById(R.id.fabAdd);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        String fullName = tokenStore.getFullName();
        if (fullName != null && !fullName.trim().isEmpty()) {
            tvGreeting.setText(String.format("Hello, %s 👋", fullName));
        }

        adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        txViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        txViewModel.getTransactions().observe(this, transactions -> {
            adapter.setItems(transactions);
            updateSummaryCards(transactions);
        });

        dashboardViewModel.remainingBudget.observe(this, remaining -> {
            if (remaining == null) {
                tvRemaining.setText("Remaining: ZMW 0.00");
            } else {
                tvRemaining.setText(String.format(Locale.getDefault(), "Remaining: ZMW %.2f", remaining));
            }
        });

        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddTransactionActivity.class)));
        }

        setupBottomNavigation();
    }

    private void updateSummaryCards(List<TransactionEntity> transactions) {
        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        if (transactions != null) {
            for (TransactionEntity t : transactions) {
                if ("income".equalsIgnoreCase(t.type)) {
                    totalIncome += t.amount;
                } else if ("expense".equalsIgnoreCase(t.type)) {
                    totalExpenses += t.amount;
                }
            }
        }

        double balance = totalIncome - totalExpenses;

        tvBalance.setText(String.format(Locale.getDefault(), "ZMW %,.2f", balance));
        tvIncome.setText(String.format(Locale.getDefault(), "ZMW %,.2f", totalIncome));
        tvExpenses.setText(String.format(Locale.getDefault(), "ZMW %,.2f", totalExpenses));

        double defaultMonthlyBudget = 5000.0;
        double remaining = defaultMonthlyBudget - totalExpenses;
        int progress = defaultMonthlyBudget > 0 ? (int) Math.min(100, (totalExpenses / defaultMonthlyBudget) * 100) : 0;

        tvBudgetAmount.setText(String.format(Locale.getDefault(), "ZMW %,.2f / ZMW %,.2f", totalExpenses, defaultMonthlyBudget));
        budgetProgress.setProgress(progress);
        tvRemaining.setText(String.format(Locale.getDefault(), "Remaining: ZMW %,.2f", remaining));
    }

    private void setupBottomNavigation() {
        if (bottomNavigation == null) return;
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                return true;
            } else if (itemId == R.id.nav_transactions) {
                startActivity(new Intent(this, TransactionsActivity.class));
                return true;
            } else if (itemId == R.id.nav_budgets) {
                startActivity(new Intent(this, BudgetsActivity.class));
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
