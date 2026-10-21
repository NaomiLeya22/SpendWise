package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.analytics.AnalyticsActivity;
import com.example.spendwise.ui.adapter.TransactionAdapter;
import com.example.spendwise.ui.viewmodel.TransactionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TransactionsActivity extends AppCompatActivity {

    private RecyclerView rvAllTransactions;
    private TextView tvTxCount;
    private FloatingActionButton fabAddTx;
    private BottomNavigationView bottomNavigation;

    private TransactionAdapter adapter;
    private TransactionViewModel txViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        rvAllTransactions = findViewById(R.id.rvAllTransactions);
        tvTxCount = findViewById(R.id.tvTxCount);
        fabAddTx = findViewById(R.id.fabAddTx);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        adapter = new TransactionAdapter();
        rvAllTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvAllTransactions.setAdapter(adapter);

        txViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        txViewModel.getTransactions().observe(this, transactions -> {
            adapter.setItems(transactions);
            int count = transactions != null ? transactions.size() : 0;
            tvTxCount.setText(count + " transaction" + (count == 1 ? "" : "s") + " logged");
        });

        if (fabAddTx != null) {
            fabAddTx.setOnClickListener(v -> startActivity(new Intent(this, AddTransactionActivity.class)));
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        if (bottomNavigation == null) return;
        bottomNavigation.setSelectedItemId(R.id.nav_transactions);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_transactions) {
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
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
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
