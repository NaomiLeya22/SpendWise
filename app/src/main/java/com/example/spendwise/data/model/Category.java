package com.example.spendwise.data.model;


public class Category {
    public int id;
    public String name;
    public String type; // "income" or "expense"

    public Category(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}

