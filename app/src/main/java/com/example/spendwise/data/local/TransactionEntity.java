package com.example.spendwise.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(tableName = "transactions",
        indices = {@Index("userId"), @Index("categoryId")})
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    public long transactionId;

    public long userId;
    public int categoryId;

    public double amount;

    @NonNull
    public String type; // "income" or "expense"

    public String description;

    @NonNull
    public String transactionDate; // ISO yyyy-MM-dd

    @NonNull
    public String syncStatus; // "synced" or "pending"
    public long createdAt;
    public long updatedAt;
    public String category;

    public String clientUuid;
    public long remoteId;
}
