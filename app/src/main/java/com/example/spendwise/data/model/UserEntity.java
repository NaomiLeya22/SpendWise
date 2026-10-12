package com.example.spendwise.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private int userId;

    @NonNull
    private String fullName;
    @NonNull
    private String email;
    @NonNull
    private String passwordHash;
    public String role;
    public long createdAt;
    public long updatedAt;

    public UserEntity(int id, String fullNamename, String email) {
        this.userId = id;
        this.fullName = fullNamename;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
