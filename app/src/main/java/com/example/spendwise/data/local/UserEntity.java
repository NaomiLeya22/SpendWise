package com.example.spendwise.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long userId;

    @NonNull
    public String fullName;

    @NonNull
    public String email;

    @NonNull
    public String passwordHash;

    public String role; // "user"

    public long createdAt;
    public long updatedAt;

}
