package com.example.spendwise.dto;

public class BudgetDto {

    public long id;
    public String category;
    public double amount;
    public String budgetMonth;

    public BudgetDto() {
    }

    public BudgetDto(String category, double amount, String budgetMonth) {
        this.category = category;
        this.amount = amount;
        this.budgetMonth = budgetMonth;
    }
}
