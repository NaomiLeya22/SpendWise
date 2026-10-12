package com.example.spendwise.data.repository;

import com.example.spendwise.data.local.CategoryEntity;
import com.example.spendwise.data.local.TransactionEntity;

import java.util.ArrayList;
import java.util.List;

public class MockRepository {

    private static MockRepository instance;
    private List<TransactionEntity> transactionEntities;
    private List<CategoryEntity> categories;

    private MockRepository() {
        categories = new ArrayList<>();
        CategoryEntity c1 = new CategoryEntity();
        c1.categoryId = 1;
        c1.name = "Salary";
        c1.type = "income";
        categories.add(c1);

        CategoryEntity c2 = new CategoryEntity();
        c2.categoryId = 2;
        c2.name = "Groceries";
        c2.type = "expense";
        categories.add(c2);

        CategoryEntity c3 = new CategoryEntity();
        c3.categoryId = 3;
        c3.name = "Transport";
        c3.type = "expense";
        categories.add(c3);

        transactionEntities = new ArrayList<>();
        TransactionEntity t1 = new TransactionEntity();
        t1.transactionId = 1;
        t1.userId = 1;
        t1.categoryId = 1;
        t1.amount = 1200.00;
        t1.type = "income";
        t1.description = "August salary";
        t1.transactionDate = "2026-08-01";
        t1.syncStatus = "synced";
        transactionEntities.add(t1);

        TransactionEntity t2 = new TransactionEntity();
        t2.transactionId = 2;
        t2.userId = 1;
        t2.categoryId = 2;
        t2.amount = 45.50;
        t2.type = "expense";
        t2.description = "Supermarket";
        t2.transactionDate = "2026-08-02";
        t2.syncStatus = "synced";
        transactionEntities.add(t2);

        TransactionEntity t3 = new TransactionEntity();
        t3.transactionId = 3;
        t3.userId = 1;
        t3.categoryId = 3;
        t3.amount = 10.00;
        t3.type = "expense";
        t3.description = "Taxi";
        t3.transactionDate = "2026-08-03";
        t3.syncStatus = "pending";
        transactionEntities.add(t3);
    }

    public static MockRepository getInstance() {
        if (instance == null) instance = new MockRepository();
        return instance;
    }

    public List<TransactionEntity> getTransactions() { return transactionEntities; }
    public List<CategoryEntity> getCategories() { return categories; }

    public void addTransaction(TransactionEntity t) { transactionEntities.add(0, t); }
}
