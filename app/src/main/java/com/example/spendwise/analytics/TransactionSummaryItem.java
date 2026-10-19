package com.example.spendwise.analytics;

import java.math.BigDecimal;

public class TransactionSummaryItem {

    private final BigDecimal amount;
    private final String type;
    private final String category;
    private final String date;

    public TransactionSummaryItem(
            BigDecimal amount,
            String type,
            String category,
            String date) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

}
