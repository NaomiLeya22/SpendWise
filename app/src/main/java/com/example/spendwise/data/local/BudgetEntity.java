package com.example.spendwise.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "budgets")
public class BudgetEntity {
    @PrimaryKey(autoGenerate = true)
    public long budgetId;

    public long userId;
    public Integer categoryId; // nullable: null means monthly total budget
    public double amount; // budget amount
    @NonNull
    public String period; // e.g., "2026-08" (YYYY-MM)
    public long createdAt;
    public long updatedAt;
}
