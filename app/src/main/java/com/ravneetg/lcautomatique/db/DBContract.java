package com.ravneetg.lcautomatique.db;

import android.provider.BaseColumns;

import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;

/**
 * Contains constants for the data types, table names and static queries.
 *
 * Created by HemantSingh on 1/21/2015.
 */
public final class DBContract {

    public static final String DB_NAME = "lc.db";
    public static final int DB_VERSION_INITIAL = 1;
    public static final String COLUMN_NAME_ACCOUNT_ID = "ACCOUNT_ID";
    public static final String DEFAULT_ACCOUNT_NAME = "Default";
    public static final int DEFAULT_ACCOUNT_ID = 1;
    public static final String COLUMN_NAME_DATA = "DATA";
    public static final String COLUMN_NAME_CREATED_DATE = "CREATED_DATE";
    private static final String COMMA_SEP = ",";
    private static final String BLOB_TYPE = " BLOB";
    private static final String DATE_TYPE = " DATETIME";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String TEXT_TYPE = " TEXT";
    private static final String PRIMARY_KEY = BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_STRATEGY_NAME = "NAME";
    public static final String COLUMN_STRATEGY_FILTERS = "FILTERS";
    public static final String COLUMN_STRATEGY_TYPE = "STRATEGY_TYPE";

    private static String genericTableSQL(String tableName) {
        return "CREATE TABLE " + tableName + " (" +
                PRIMARY_KEY + "," +
                COLUMN_NAME_ACCOUNT_ID + INTEGER_TYPE + "," +
                COLUMN_NAME_CREATED_DATE + DATE_TYPE + "," +
                COLUMN_NAME_DATA + BLOB_TYPE +
                " )";
    }

    //account_name, created_date, modified_date) values (?,datetime(),datetime()
    public static abstract class AccountTable implements BaseColumns {
        public static final String TABLE_NAME = "ACCOUNT";
        public static final String COLUMN_NAME_ACCOUNT_NAME = "ACCOUNT_NAME";
        public static final String COLUMN_NAME_MODIFIED_DATE = "MODIFIED_DATE";

        public static final String SQL_DEFAULT_ACCOUNT_INSERT = "INSERT INTO "
                + DBContract.AccountTable.TABLE_NAME +
                " (" + COLUMN_NAME_ACCOUNT_ID + ", " + COLUMN_NAME_ACCOUNT_NAME + ", " +
                COLUMN_NAME_CREATED_DATE + ", " + COLUMN_NAME_MODIFIED_DATE + "" +
                ") values (" + DEFAULT_ACCOUNT_ID + ", '" + DEFAULT_ACCOUNT_NAME + "' ,datetime(),datetime())";

        public static final String SQL_CREATE_ACCOUNT =
                "CREATE TABLE " + DBContract.AccountTable.TABLE_NAME + " (" +
                        PRIMARY_KEY + "," +
                        COLUMN_NAME_ACCOUNT_ID + INTEGER_TYPE + "," +
                        COLUMN_NAME_ACCOUNT_NAME + TEXT_TYPE + "," +
                        COLUMN_NAME_CREATED_DATE + " DATETIME ," +
                        COLUMN_NAME_MODIFIED_DATE + " DATETIME" +
                " )";

    }

    public static abstract class StrategiesTable implements BaseColumns {
        public static final String TABLE_NAME = "STRATEGIES";
        public static final String COLUMN_IS_ACTIVE = "IS_ACTIVE";
        public static final String COLUMN_MAX_LOANS_PER_DAY = "MAX_LOANS_PER_DAY";
        public static final String COLUMN_AMOUNT_PER_NOTE = "AMOUNT_PER_NOTE";
        public static final String COLUMN_TARGET_PORTFOLIO = "TARGET_PORTFOLIO";

        public static enum STRATEGY_TYPE{
            LOCAL,
            SOCIAL
        }
        // SQL For creating table Strategy
        public static final String SQL_CREATE_STRATEGY =
                "CREATE TABLE " + StrategiesTable.TABLE_NAME + " (" +
                        PRIMARY_KEY + "," +
                        COLUMN_NAME_ACCOUNT_ID + INTEGER_TYPE + "," +
                        COLUMN_NAME_CREATED_DATE + DATE_TYPE + "," +
                        COLUMN_STRATEGY_NAME + TEXT_TYPE + "," +
                        // Value of 1 means true and 0 means false
                        COLUMN_IS_ACTIVE + INTEGER_TYPE + " DEFAULT 1," +
                        COLUMN_MAX_LOANS_PER_DAY + INTEGER_TYPE + "," +
                        COLUMN_AMOUNT_PER_NOTE + INTEGER_TYPE + "," +
                        COLUMN_TARGET_PORTFOLIO + TEXT_TYPE + "," +
                        COLUMN_STRATEGY_FILTERS + BLOB_TYPE + "," +
                        COLUMN_STRATEGY_TYPE + TEXT_TYPE + " DEFAULT LOCAL" +
                        " )";
    }

    public static abstract class SummaryTable implements BaseColumns {
        public static final String TABLE_NAME = "SUMMARY";

        public static final String SQL_SELECT_ACCOUNT_SUMMARY =
                "SELECT " + COLUMN_NAME_DATA +
                        " FROM " + TABLE_NAME +
                        " WHERE " + COLUMN_NAME_ACCOUNT_ID + "= '" + DEFAULT_ACCOUNT_ID + "'";

        // SQL For creating table Summary
        public static String createTableSQL() {
            return genericTableSQL(TABLE_NAME);
        }
    }

    // TODO Rename this table to LOANS and add _id column for content provider
    public static abstract class NotesTable implements BaseColumns {
        public static final String TABLE_NAME = "NOTES";
        public static String[][] COL_NAME_AND_TYPE = new String[][]{
                {COLUMN_NAME_ACCOUNT_ID, INTEGER_TYPE},
                {NoteOwnedResponseData.LOAN_AMOUNT, INTEGER_TYPE},
                {NoteOwnedResponseData.ORDER_DATE, DATE_TYPE},
                {NoteOwnedResponseData.ISSUE_DATE, DATE_TYPE},
                {NoteOwnedResponseData.GRADE, TEXT_TYPE},
                {NoteOwnedResponseData.SUB_GRADE, TEXT_TYPE},
                {NoteOwnedResponseData.NOTE_ID, INTEGER_TYPE + " UNIQUE"},
                {NoteOwnedResponseData.LOAN_LENGTH, INTEGER_TYPE},
                {NoteOwnedResponseData.INTEREST_RATE, REAL_TYPE},
                {NoteOwnedResponseData.NOTE_AMOUNT, INTEGER_TYPE},
                {NoteOwnedResponseData.LOAN_STATUS, TEXT_TYPE},
                {NoteOwnedResponseData.ORDER_ID, TEXT_TYPE},
                {NoteOwnedResponseData.LOAN_STATUS_DATE, DATE_TYPE},
                {NoteOwnedResponseData.PAYMENTS_RECEIVED, REAL_TYPE},
                {NoteOwnedResponseData.LOAN_ID, INTEGER_TYPE}
        };

        public static String createTableSQL() {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE " + DBContract.NotesTable.TABLE_NAME + " (");
            sb.append(PRIMARY_KEY);
            for (int i = 0; i < COL_NAME_AND_TYPE.length; i++) {
                sb.append(",");
                sb.append(COL_NAME_AND_TYPE[i][0]);
                sb.append(COL_NAME_AND_TYPE[i][1]);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static abstract class ActivityTable {
        public static final String TABLE_NAME = "ACTIVITY";
        public static final String SQL_CREATE_ACTIVITY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        PRIMARY_KEY + "," +
                        COLUMN_NAME_ACCOUNT_ID + INTEGER_TYPE + "," +
                        COLUMN_NAME_CREATED_DATE + DATE_TYPE + "," +
                        COLUMN_NAME_DATA + TEXT_TYPE + ")";

        public static String insertSQL(String data) {
            return "INSERT INTO "
                    + TABLE_NAME +
                    " (" + COLUMN_NAME_ACCOUNT_ID + ", " + COLUMN_NAME_CREATED_DATE + ", " +
                    COLUMN_NAME_DATA +
                    ") values (" + DEFAULT_ACCOUNT_ID + ", datetime(), '" + data + "')";
        }

    }

    public static abstract class PortfoliosTable implements BaseColumns {
        public static final String TABLE_NAME = "PORTFOLIOS";
        // SQL For creating table Notes
        public static String createTableSQL() {
            return genericTableSQL(TABLE_NAME);
        }
    }
}
