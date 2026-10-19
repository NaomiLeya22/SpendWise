package com.example.spendwise.analytics;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AnalyticsCalculatorTest {

    private List<TransactionSummaryItem> sampleTransactions() {
        return Arrays.asList(
                new TransactionSummaryItem(
                        new BigDecimal("5000.00"), "INCOME",
                        "Salary", "2026-10-01"),
                new TransactionSummaryItem(
                        new BigDecimal("150.00"), "EXPENSE",
                        "Food", "2026-10-02"),
                new TransactionSummaryItem(
                        new BigDecimal("200.00"), "EXPENSE",
                        "Transport", "2026-10-03"),
                new TransactionSummaryItem(
                        new BigDecimal("700.00"), "EXPENSE",
                        "Food", "2026-09-29")
        );
    }

    @Test
    public void calculatesOctoberIncome() {
        assertEquals(
                0,
                new BigDecimal("5000.00").compareTo(
                        AnalyticsCalculator.totalIncome(
                                sampleTransactions(), "2026-10"))
        );
    }

    @Test
    public void calculatesOctoberExpensesOnly() {
        assertEquals(
                0,
                new BigDecimal("350.00").compareTo(
                        AnalyticsCalculator.totalExpenses(
                                sampleTransactions(), "2026-10"))
        );
    }

    @Test
    public void calculatesBalance() {
        assertEquals(
                0,
                new BigDecimal("4650.00").compareTo(
                        AnalyticsCalculator.balance(
                                sampleTransactions(), "2026-10"))
        );
    }

    @Test
    public void groupsExpensesByCategory() {
        Map<String, BigDecimal> result =
                AnalyticsCalculator.expensesByCategory(
                        sampleTransactions(), "2026-10");

        assertEquals(
                0,
                new BigDecimal("150.00").compareTo(result.get("Food"))
        );

        assertEquals(
                0,
                new BigDecimal("200.00").compareTo(result.get("Transport"))
        );
    }

}
