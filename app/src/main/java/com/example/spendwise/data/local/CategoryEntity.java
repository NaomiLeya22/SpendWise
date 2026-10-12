package com.example.spendwise.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "categories")
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int categoryId;

    @NonNull
    public String name;

    @NonNull
    public String type; // "income" or "expense"
}
