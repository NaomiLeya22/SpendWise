package com.example.spendwise.data.model;


public class Transaction {
    public long id;
    public long userId;
    public String category;
    public double amount;
    public String type; // "income" or "expense"
    public String description;
    public String date; // ISO yyyy-MM-dd
    public String syncStatus; // "synced" or "pending"

    public Transaction(long id, String category, double amount, String type, String description, String date, String syncStatus) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
        this.syncStatus = syncStatus;
    }
}
