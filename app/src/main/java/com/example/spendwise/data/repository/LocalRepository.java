package com.example.spendwise.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.example.spendwise.dao.BudgetDao;
import com.example.spendwise.dao.CategoryDao;
import com.example.spendwise.dao.TransactionDao;
import com.example.spendwise.dao.UserDao;
import com.example.spendwise.data.local.SpendWiseDatabase;
import com.example.spendwise.data.local.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalRepository {
    private final SpendWiseDatabase db;
    private final TransactionDao transactionDao;
    private final CategoryDao categoryDao;
    private final BudgetDao budgetDao;
    private final UserDao userDao;
    private final ExecutorService io;

    public LocalRepository(Context ctx) {
        db = SpendWiseDatabase.getInstance(ctx);
        transactionDao = db.transactionDao();
        categoryDao = db.categoryDao();
        budgetDao = db.budgetDao();
        userDao = db.userDao();
        io = Executors.newSingleThreadExecutor();
    }

    // Transactions
    public LiveData<List<TransactionEntity>> getTransactionsLive(long userId) {
        return transactionDao.getForUser(userId);
    }

    public void addTransaction(TransactionEntity t, Callback<Long> cb) {
        io.execute(() -> {
            long id = transactionDao.insert(t);
            if (cb != null) cb.onComplete(id);
        });
    }

    public void updateTransaction(TransactionEntity t) {
        io.execute(() -> transactionDao.update(t));
    }

    public void deleteTransaction(TransactionEntity t) {
        io.execute(() -> transactionDao.delete(t));
    }

    public List<TransactionEntity> getPendingTransactions(long userId) {
        return transactionDao.getPendingForUser(userId);
    }

    public void markSynced(long transactionId) {
        io.execute(() -> transactionDao.updateSyncStatus(transactionId, "synced"));
    }

    // Categories
    public List<CategoryEntity> getCategories() {
        return categoryDao.getAll();
    }

    // Budgets
    public LiveData<List<BudgetEntity>> getBudgetsLive(long userId, String period) {
        return budgetDao.getLiveForUserAndPeriod(userId, period);
    }

    public void addOrUpdateBudget(BudgetEntity b) {
        io.execute(() -> {
            if (b.budgetId == 0) budgetDao.insert(b);
            else budgetDao.update(b);
        });
    }

    // Simple callback interface
    public interface Callback<T> { void onComplete(T result); }
}
