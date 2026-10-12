package com.example.spendwise.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import androidx.lifecycle.LiveData;

import com.example.spendwise.data.local.TransactionEntity;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    long insert(TransactionEntity t);

    @Update
    void update(TransactionEntity t);

    @Delete
    void delete(TransactionEntity t);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY transactionDate DESC")
    LiveData<List<TransactionEntity>> getForUser(long userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND syncStatus = 'pending'")
    List<TransactionEntity> getPendingForUser(long userId);

    @Query("UPDATE transactions SET syncStatus = :status WHERE transactionId = :id")
    void updateSyncStatus(long id, String status);
}
