package com.example.spendwise.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.spendwise.data.local.TransactionEntity;
import com.example.spendwise.database.AppDatabase;
import com.example.spendwise.dto.ApiEnvelope;
import com.example.spendwise.dto.TransactionDto;
import com.example.spendwise.network.ApiClient;
import com.example.spendwise.network.TokenStore;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class SyncWorker extends Worker{

    public SyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        TokenStore tokenStore = new TokenStore(getApplicationContext());

        if (!tokenStore.isLoggedIn()) {
            return Result.failure();
        }

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        try {
            List<TransactionEntity> pending =
                    db.transactionDao().getPendingTransactions();

            for (TransactionEntity local : pending) {
                if (isStopped()) {
                    return Result.retry();
                }


                if (local.userId != tokenStore.getServerUserId()) {
                    continue;
                }

                TransactionDto dto = new TransactionDto(
                        local.clientUuid,
                        local.type,
                        local.category,
                        local.amount,
                        local.description == null ? "" : local.description,
                        local.transactionDate
                );

                Response<ApiEnvelope<TransactionDto>> response =
                        ApiClient.getApi(getApplicationContext())
                                .createTransaction(dto)
                                .execute();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {

                    long serverId = response.body().getData().id;

                    db.transactionDao().markSynced(
                            local.transactionId,
                            serverId
                    );

                } else if (response.code() == 401) {

                    return Result.failure();

                } else if (response.code() >= 500) {
                    return Result.retry();

                } else {

                    return Result.failure();
                }
            }

            return Result.success();

        } catch (IOException e) {

            return Result.retry();

        } catch (Exception e) {
            return Result.failure();
        }
    }
}
