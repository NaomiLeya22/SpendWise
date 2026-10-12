package com.example.spendwise.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.spendwise.data.local.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(CategoryEntity c);

    @Query("SELECT * FROM categories ORDER BY name")
    List<CategoryEntity> getAll();
}
