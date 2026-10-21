package com.example.spendwise.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.spendwise.data.local.TransactionEntity;
import com.example.spendwise.data.model.Transaction;
import com.example.spendwise.data.repository.LocalRepository;
import com.example.spendwise.network.TokenStore;
import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private final LocalRepository repo;
    private LiveData<List<TransactionEntity>> transactionsLive;
    private final long currentUserId;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repo = new LocalRepository(application);
        TokenStore tokenStore = new TokenStore(application);
        long serverId = tokenStore.getServerUserId();
        currentUserId = serverId > 0 ? serverId : 1L;
        transactionsLive = repo.getTransactionsLive(currentUserId);
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactionsLive;
    }

    public void addTransaction(TransactionEntity t, LocalRepository.Callback<Long> cb) {
        // Basic validation
        if (t.amount <= 0) {
            if (cb != null) cb.onComplete(-1L);
            return;
        }
        t.userId = currentUserId;
        t.createdAt = System.currentTimeMillis();
        t.updatedAt = t.createdAt;
        t.syncStatus = "pending";
        repo.addTransaction(t, cb);
    }

    public void deleteTransaction(TransactionEntity t) {
        repo.deleteTransaction(t);
    }

    public void updateTransaction(TransactionEntity t) {
        t.updatedAt = System.currentTimeMillis();
        repo.updateTransaction(t);
    }

}
