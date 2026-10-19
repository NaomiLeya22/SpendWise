package com.example.spendwise;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spendwise.analytics.AnalyticsCalculator;
import com.example.spendwise.analytics.BudgetAlertHelper;
import com.example.spendwise.analytics.TransactionSummaryItem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity{

    private static final String CURRENT_MONTH = "2026-10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        TextView incomeValue = findViewById(R.id.incomeValue);
        TextView expenseValue = findViewById(R.id.expenseValue);
        TextView balanceValue = findViewById(R.id.balanceValue);
        TextView categoryBreakdown = findViewById(R.id.categoryBreakdown);
        TextView budgetStatus = findViewById(R.id.budgetStatus);


        List<TransactionSummaryItem> transactions = Arrays.asList(
                new TransactionSummaryItem(
                        new BigDecimal("5000.00"),
                        "INCOME", "Salary", "2026-10-01"),
                new TransactionSummaryItem(
                        new BigDecimal("150.00"),
                        "EXPENSE", "Food", "2026-10-02"),
                new TransactionSummaryItem(
                        new BigDecimal("200.00"),
                        "EXPENSE", "Transport", "2026-10-03")
        );

        BigDecimal income =
                AnalyticsCalculator.totalIncome(transactions, CURRENT_MONTH);
        BigDecimal expenses =
                AnalyticsCalculator.totalExpenses(transactions, CURRENT_MONTH);
        BigDecimal balance =
                AnalyticsCalculator.balance(transactions, CURRENT_MONTH);

        incomeValue.setText("Income: " + money(income));
        expenseValue.setText("Expenses: " + money(expenses));
        balanceValue.setText("Balance: " + money(balance));

        Map<String, BigDecimal> categories =
                AnalyticsCalculator.expensesByCategory(
                        transactions, CURRENT_MONTH);

        StringBuilder breakdown = new StringBuilder("Expenses by category\n");

        if (categories.isEmpty()) {
            breakdown.append("No expenses recorded this month.");
        } else {
            for (Map.Entry<String, BigDecimal> entry : categories.entrySet()) {
                breakdown.append(entry.getKey())
                        .append(": ")
                        .append(money(entry.getValue()))
                        .append("\n");
            }
        }

        categoryBreakdown.setText(breakdown.toString());

        BigDecimal sampleBudget = new BigDecimal("500.00");
        BudgetAlertHelper.Status status =
                BudgetAlertHelper.getStatus(expenses, sampleBudget);

        budgetStatus.setText(
                "Sample budget: " + money(sampleBudget)
                        + "\nRemaining: "
                        + money(BudgetAlertHelper.remaining(expenses, sampleBudget))
                        + "\nStatus: " + status
        );
    }

    private String money(BigDecimal amount) {
        return String.format(
                Locale.getDefault(), "K%,.2f", amount.doubleValue());
    }
}
