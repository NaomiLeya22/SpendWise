package com.example.spendwise.sync;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;


public class NetworkSyncScheduler {

    private static final String PERIODIC_WORK =
            "expense_manager_periodic_sync";

    private static final String ONE_TIME_WORK =
            "expense_manager_one_time_sync";

    private NetworkSyncScheduler() {
    }

    private static Constraints networkConstraints() {
        return new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
    }

    public static void schedule(Context context) {
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        SyncWorker.class,
                        15,
                        TimeUnit.MINUTES
                )
                        .setConstraints(networkConstraints())
                        .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(
                        PERIODIC_WORK,
                        ExistingPeriodicWorkPolicy.KEEP,
                        request
                );
    }

    public static void syncNow(Context context) {
        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(SyncWorker.class)
                        .setConstraints(networkConstraints())
                        .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniqueWork(
                        ONE_TIME_WORK,
                        ExistingWorkPolicy.KEEP,
                        request
                );
    }
}
