package com.example.spendwise.analytics;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class BudgetAlertHelperTest {

    @Test
    public void spendingBelow80PercentIsOk() {
        assertEquals(
                BudgetAlertHelper.Status.OK,
                BudgetAlertHelper.getStatus(
                        new BigDecimal("79.99"),
                        new BigDecimal("100.00"))
        );
    }

    @Test
    public void spendingAt80PercentIsWarning() {
        assertEquals(
                BudgetAlertHelper.Status.WARNING,
                BudgetAlertHelper.getStatus(
                        new BigDecimal("80.00"),
                        new BigDecimal("100.00"))
        );
    }

    @Test
    public void spendingAtBudgetIsExceeded() {
        assertEquals(
                BudgetAlertHelper.Status.EXCEEDED,
                BudgetAlertHelper.getStatus(
                        new BigDecimal("100.00"),
                        new BigDecimal("100.00"))
        );
    }

    @Test
    public void calculatesRemainingBudget() {
        assertEquals(
                0,
                new BigDecimal("25.00").compareTo(
                        BudgetAlertHelper.remaining(
                                new BigDecimal("75.00"),
                                new BigDecimal("100.00")))
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsZeroBudget() {
        BudgetAlertHelper.getStatus(
                new BigDecimal("0.00"),
                new BigDecimal("0.00"));
    }

}
