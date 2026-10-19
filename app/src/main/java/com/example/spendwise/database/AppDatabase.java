package com.example.spendwise.database;

import android.content.Context;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.spendwise.dao.TransactionDao;
import com.example.spendwise.data.local.SpendWiseDatabase;

public class AppDatabase {

    public static final Migration MIGRATION_1_2 =
            new Migration(1, 2) {
                @Override
                public void migrate(SupportSQLiteDatabase database) {
                    database.execSQL(
                            "ALTER TABLE transactions " +
                                    "ADD COLUMN clientUuid TEXT NOT NULL DEFAULT ''"
                    );

                    database.execSQL(
                            "ALTER TABLE transactions " +
                                    "ADD COLUMN remoteId INTEGER NOT NULL DEFAULT 0"
                    );

                    database.execSQL(
                            "ALTER TABLE transactions " +
                                    "ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'PENDING'"
                    );

                    database.execSQL(
                            "UPDATE transactions " +
                                    "SET clientUuid = lower(hex(randomblob(16))) " +
                                    "WHERE clientUuid = ''"
                    );
                }
            };
    private static AppDatabase INSTANCE;
    private final SpendWiseDatabase db;

    private AppDatabase(Context context) {
        this.db = SpendWiseDatabase.getInstance(context);
    }

    public static synchronized AppDatabase getInstance(Context applicationContext) {
        if (INSTANCE == null) {
            INSTANCE = new AppDatabase(applicationContext);
        }
        return INSTANCE;
    }

    public TransactionDao transactionDao() {
        return db.transactionDao();
    }
}


