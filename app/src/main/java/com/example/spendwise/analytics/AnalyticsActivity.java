package com.example.spendwise.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spendwise.R;
import com.example.spendwise.data.local.TransactionEntity;
import com.example.spendwise.data.repository.LocalRepository;
import com.example.spendwise.network.TokenStore;
import com.example.spendwise.ui.BudgetsActivity;
import com.example.spendwise.ui.DashboardActivity;
import com.example.spendwise.ui.ProfileActivity;
import com.example.spendwise.ui.TransactionsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView incomeText;
    private TextView expensesText;
    private TextView balanceText;
    private PieChart categoryPieChart;
    private Button exportButton;
    private BottomNavigationView bottomNavigation;

    private LocalRepository repository;
    private TokenStore tokenStore;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<TransactionRecord> records = new ArrayList<>();

    private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-ZM"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        tokenStore = new TokenStore(this);
        repository = new LocalRepository(getApplication());

        incomeText = findViewById(R.id.incomeValue);
        expensesText = findViewById(R.id.expenseValue);
        balanceText = findViewById(R.id.balanceValue);
        categoryPieChart = findViewById(R.id.categoryPieChart);
        exportButton = findViewById(R.id.exportReportButton);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        configureChart();

        exportButton.setOnClickListener(view -> exportReport());
        setupBottomNavigation();

        long userId = tokenStore.getServerUserId() > 0 ? tokenStore.getServerUserId() : 1L;
        repository.getTransactionsLive(userId).observe(this, this::loadFromEntities);
    }

    private void loadFromEntities(List<TransactionEntity> entities) {
        List<TransactionRecord> list = new ArrayList<>();
        if (entities != null) {
            for (TransactionEntity e : entities) {
                list.add(new TransactionRecord(
                        e.type,
                        e.category != null ? e.category : "Uncategorized",
                        e.amount,
                        e.transactionDate
                ));
            }
        }
        displayAnalytics(list);
    }

    private void configureChart() {
        if (categoryPieChart == null) return;
        categoryPieChart.setUsePercentValues(true);
        categoryPieChart.getDescription().setEnabled(false);
        categoryPieChart.setDrawEntryLabels(true);
        categoryPieChart.setEntryLabelTextSize(12f);
        categoryPieChart.setNoDataText("Add expenses to see your spending breakdown.");
        categoryPieChart.getLegend().setEnabled(true);
    }

    public void displayAnalytics(List<TransactionRecord> data) {
        records = data == null ? new ArrayList<>() : new ArrayList<>(data);

        if (incomeText != null) {
            incomeText.setText(String.format("Total income: %s", money.format(AnalyticsCalculator.getTotalIncome(records))));
        }

        if (expensesText != null) {
            expensesText.setText(String.format("Total expenses: %s", money.format(AnalyticsCalculator.getTotalExpenses(records))));
        }

        if (balanceText != null) {
            balanceText.setText(String.format("Balance: %s", money.format(AnalyticsCalculator.getBalance(records))));
        }

        if (categoryPieChart == null) return;

        Map<String, Double> categoryTotals = AnalyticsCalculator.getExpensesByCategory(records);
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }
        }

        if (entries.isEmpty()) {
            categoryPieChart.clear();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(categoryPieChart));

        categoryPieChart.setData(pieData);
        categoryPieChart.invalidate();
    }

    private void exportReport() {
        if (exportButton != null) exportButton.setEnabled(false);

        executor.execute(() -> {
            try {
                File report = PdfReportExporter.export(getApplicationContext(), records);

                runOnUiThread(() -> {
                    if (exportButton != null) exportButton.setEnabled(true);
                    Toast.makeText(
                            this,
                            "PDF report saved: " + (report != null ? report.getName() : "PDF"),
                            Toast.LENGTH_LONG
                    ).show();
                });

            } catch (IOException exception) {
                runOnUiThread(() -> {
                    if (exportButton != null) exportButton.setEnabled(true);
                    Toast.makeText(this, "Could not generate report.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setupBottomNavigation() {
        if (bottomNavigation == null) return;
        bottomNavigation.setSelectedItemId(R.id.nav_reports);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_reports) {
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
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }
}
