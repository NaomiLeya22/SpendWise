package com.example.spendwise.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.spendwise.dao.UserDao;
import com.example.spendwise.dao.CategoryDao;
import com.example.spendwise.dao.TransactionDao;
import com.example.spendwise.dao.BudgetDao;

@Database(entities = {UserEntity.class, CategoryEntity.class, TransactionEntity.class, BudgetEntity.class}, version = 1)
public abstract class SpendWiseDatabase extends RoomDatabase {
    private static volatile SpendWiseDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();

    public static SpendWiseDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SpendWiseDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SpendWiseDatabase.class, "spendwise.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
