package com.example.spendwise.analytics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public class AnalyticsCalculator {

    private AnalyticsCalculator() {
    }

    public static BigDecimal totalIncome(
            List<TransactionSummaryItem> transactions,
            String month) {
        return totalByType(transactions, month, "INCOME");
    }

    public static BigDecimal totalExpenses(
            List<TransactionSummaryItem> transactions,
            String month) {
        return totalByType(transactions, month, "EXPENSE");
    }

    public static BigDecimal balance(
            List<TransactionSummaryItem> transactions,
            String month) {
        return totalIncome(transactions, month)
                .subtract(totalExpenses(transactions, month));
    }

    private static BigDecimal totalByType(
            List<TransactionSummaryItem> transactions,
            String month,
            String type) {
        BigDecimal total = BigDecimal.ZERO;

        for (TransactionSummaryItem item : transactions) {
            if (item.getDate() != null
                    && item.getDate().startsWith(month)
                    && type.equalsIgnoreCase(item.getType())) {
                total = total.add(item.getAmount());
            }
        }

        return total;
    }

    public static Map<String, BigDecimal> expensesByCategory(
            List<TransactionSummaryItem> transactions,
            String month) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();

        for (TransactionSummaryItem item : transactions) {
            if (item.getDate() != null
                    && item.getDate().startsWith(month)
                    && "EXPENSE".equalsIgnoreCase(item.getType())) {

                String category = item.getCategory();

                if (category == null || category.trim().isEmpty()) {
                    category = "Uncategorized";
                }

                BigDecimal current =
                        result.getOrDefault(category, BigDecimal.ZERO);

                result.put(category, current.add(item.getAmount()));
            }
        }

        return result;
    }

    public static double getTotalIncome(List<TransactionRecord> records) {
        double total = 0.0;
        if (records == null) return total;
        for (TransactionRecord record : records) {
            if ("INCOME".equalsIgnoreCase(record.getType())) {
                total += record.getAmount();
            }
        }
        return total;
    }

    public static double getTotalExpenses(List<TransactionRecord> records) {
        double total = 0.0;
        if (records == null) return total;
        for (TransactionRecord record : records) {
            if ("EXPENSE".equalsIgnoreCase(record.getType())) {
                total += record.getAmount();
            }
        }
        return total;
    }

    public static double getBalance(List<TransactionRecord> records) {
        return getTotalIncome(records) - getTotalExpenses(records);
    }

    public static Map<String, Double> getExpensesByCategory(List<TransactionRecord> records) {
        Map<String, Double> result = new LinkedHashMap<>();
        if (records == null) return result;
        for (TransactionRecord record : records) {
            if ("EXPENSE".equalsIgnoreCase(record.getType())) {
                String category = record.getCategory();
                if (category == null || category.trim().isEmpty()) {
                    category = "Uncategorized";
                }
                double current = result.getOrDefault(category, 0.0);
                result.put(category, current + record.getAmount());
            }
        }
        return result;
    }

}