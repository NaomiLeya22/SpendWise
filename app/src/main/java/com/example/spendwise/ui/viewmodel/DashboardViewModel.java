package com.example.spendwise.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.spendwise.data.local.TransactionEntity;
import com.example.spendwise.data.local.BudgetEntity;
import com.example.spendwise.data.repository.LocalRepository;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    private final LocalRepository repo;
    private final long currentUserId = 1L; // demo
    public final MediatorLiveData<Double> remainingBudget = new MediatorLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repo = new LocalRepository(application);
        LiveData<List<BudgetEntity>> budgets = repo.getBudgetsLive(currentUserId, getCurrentPeriod());
        LiveData<List<TransactionEntity>> txs = repo.getTransactionsLive(currentUserId);

        remainingBudget.addSource(budgets, bList -> recalc(bList, txs.getValue()));
        remainingBudget.addSource(txs, tList -> recalc(budgets.getValue(), tList));
    }

    private String getCurrentPeriod() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        return sdf.format(new java.util.Date());
    }

    private void recalc(List<BudgetEntity> budgets, List<TransactionEntity> txs) {
        double totalBudget = 0.0;
        if (budgets != null) {
            for (BudgetEntity b : budgets) totalBudget += b.amount;
        }
        double spent = 0.0;
        if (txs != null) {
            for (TransactionEntity t : txs) {
                if ("expense".equals(t.type)) spent += t.amount;
            }
        }
        remainingBudget.setValue(totalBudget - spent);
    }
}