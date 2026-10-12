package com.example.spendwise.dao;

import androidx.room.Dao;
import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.spendwise.data.local.BudgetEntity;
import java.util.List;

@Dao
public interface BudgetDao {
    @Insert
    long insert(BudgetEntity budget);
    @Update
    void update(BudgetEntity budget);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND period = :period")
    List<BudgetEntity> getForUserAndPeriod(long userId, String period);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND period = :period")
    LiveData<List<BudgetEntity>> getLiveForUserAndPeriod(
            long userId,
            String period
    );
}
