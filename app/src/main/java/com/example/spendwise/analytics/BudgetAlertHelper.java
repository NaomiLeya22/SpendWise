package com.example.spendwise.analytics;

import java.math.BigDecimal;

public class BudgetAlertHelper {

    private BudgetAlertHelper() {
    }

    public enum Status {
        OK,
        WARNING,
        EXCEEDED
    }

    public static Status getStatus(
            BigDecimal spent,
            BigDecimal budget) {

        if (spent == null || budget == null
                || spent.signum() < 0
                || budget.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Spent must be non-negative and budget must be positive");
        }

        if (spent.compareTo(budget) >= 0) {
            return Status.EXCEEDED;
        }

        BigDecimal warningThreshold =
                budget.multiply(new BigDecimal("0.80"));

        if (spent.compareTo(warningThreshold) >= 0) {
            return Status.WARNING;
        }

        return Status.OK;
    }

    public static BigDecimal remaining(
            BigDecimal spent,
            BigDecimal budget) {
        if (spent == null || budget == null) {
            throw new IllegalArgumentException(
                    "Spent and budget are required");
        }

        return budget.subtract(spent);
    }

}
