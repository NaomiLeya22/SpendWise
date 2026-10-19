package com.example.spendwise.analytics;

public class TransactionRecord {

    private final String type;
    private final String category;
    private final double amount;
    private final String transactionDate;

    public TransactionRecord(
            String type,
            String category,
            double amount,
            String transactionDate
    ) {
        this.type = type == null ? "" : type.trim();
        this.category = category == null || category.trim().isEmpty()
                ? "Uncategorized"
                : category.trim();
        this.amount = amount;
        this.transactionDate = transactionDate == null
                ? ""
                : transactionDate.trim();
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

}
