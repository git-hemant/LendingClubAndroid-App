package com.ravneetg.lcautomatique.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ravneetg.lcautomatique.MainApplication;

/**
 * This class shouldn't be used outside the current package.
 * Created by HemantSingh on 1/22/2015.
 */
class DBOpenHelper extends SQLiteOpenHelper {

    // For debug purpose only and in production it should
    // be always false.
    private static final boolean DELETE_DB = false;

    public DBOpenHelper(Context context) {
        super(context, DBContract.DB_NAME, null, DBContract.DB_VERSION_INITIAL);
        // The following line is only for testing, as we do want to re-create
        // the DB structure whenever the DB structure is updated.
        if (DELETE_DB) {
            context.deleteDatabase(DBContract.DB_NAME);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MainApplication.TAG, DBContract.AccountTable.SQL_CREATE_ACCOUNT);
        Log.d(MainApplication.TAG, DBContract.StrategiesTable.SQL_CREATE_STRATEGY);
        Log.d(MainApplication.TAG, DBContract.SummaryTable.createTableSQL());
        Log.d(MainApplication.TAG, DBContract.NotesTable.createTableSQL());
        Log.d(MainApplication.TAG, DBContract.ActivityTable.SQL_CREATE_ACTIVITY);
        Log.d(MainApplication.TAG, DBContract.PortfoliosTable.createTableSQL());
        Log.d(MainApplication.TAG, DBContract.AccountTable.SQL_DEFAULT_ACCOUNT_INSERT);

        db.execSQL(DBContract.AccountTable.SQL_CREATE_ACCOUNT);
        db.execSQL(DBContract.StrategiesTable.SQL_CREATE_STRATEGY);
        db.execSQL(DBContract.SummaryTable.createTableSQL());
        db.execSQL(DBContract.NotesTable.createTableSQL());
        db.execSQL(DBContract.ActivityTable.SQL_CREATE_ACTIVITY);
        db.execSQL(DBContract.PortfoliosTable.createTableSQL());
        // After populating the tables add the default account.
        db.execSQL(DBContract.AccountTable.SQL_DEFAULT_ACCOUNT_INSERT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // if old version is 1 and newVersion is 2 then move the entire data to Note_Details table
        // and drop the older table.

    }
}