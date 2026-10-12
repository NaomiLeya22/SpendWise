package com.example.spendwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.spendwise.data.repository.MockRepository;
import com.example.spendwise.ui.adapter.TransactionAdapter;
import com.example.spendwise.ui.viewmodel.DashboardViewModel;
import com.example.spendwise.ui.viewmodel.TransactionViewModel;

public class DashboardActivity extends AppCompatActivity {
        TextView tvGreeting, tvBudgetAmount, tvRemaining;
        RecyclerView rvTransactions;
        FloatingActionButton fabAdd;
        TransactionAdapter adapter;
        TransactionViewModel txViewModel;
        DashboardViewModel dashboardViewModel;

        @Override protected void onCreate(Bundle s) {
            super.onCreate(s);
            setContentView(R.layout.activity_dashboard);

            tvGreeting = findViewById(R.id.tvGreeting);
            tvBudgetAmount = findViewById(R.id.tvBudget);
            tvRemaining = findViewById(R.id.tvRemaining);
            rvTransactions = findViewById(R.id.rvTransactions);


            txViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);


            txViewModel.getTransactions().observe(this, transactions -> {

                adapter.setItems(transactions);
            });


            dashboardViewModel.remainingBudget.observe(this, remaining -> {
                if (remaining == null) {
                    tvRemaining.setText("Remaining: $0.00");
                } else {
                    tvRemaining.setText(String.format("Remaining: $%.2f", remaining));
                }
            });

            fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddTransactionActivity.class)));
        }

        @Override protected void onResume() {
            super.onResume();

            rvTransactions.getAdapter().notifyDataSetChanged();
        }
    }


