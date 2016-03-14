package com.ravneetg.lcautomatique.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.data.NotesCategory;
import com.ravneetg.lcautomatique.data.ReturnRateInfo;
import com.ravneetg.lcautomatique.data.gson.ActivityQueryResult;
import com.ravneetg.lcautomatique.db.models.StrategyConfig;
import com.ravneetg.lcautomatique.request.InvalidResponseException;
import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;
import com.ravneetg.lcautomatique.utils.ActivityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Main utility class for all the DB operations.
 *
 * Timezone: We are storing all dates in UTC timezone which is default
 * when you call datetime() or datetime('now') in sqlite, as localtime
 * needs explicit call to datetime('localtime')
 *
 * @author ravneet
 */
public class DBHelper {
    private static DBHelper dbHelperSingleton = null;
    private static DBOpenHelper dbOpenHelper;
    private static final Object lock = new Object();

    private DBHelper(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    public static DBHelper getInstance(Context context) {
        if (dbHelperSingleton == null) {
            synchronized (lock) {
                if (dbHelperSingleton == null) {
                    dbHelperSingleton = new DBHelper(context);
                }
            }
        }

        return dbHelperSingleton;
    }

    public SQLiteDatabase getWritableDatabase() {
        return dbOpenHelper.getWritableDatabase();
    }

    // Return is calculated by adding received principal and outstanding principal
    // which we call total principal. And we will reduce charged-off principal
    // from interested received till now.
    // Now we will calculate percentage by: (interest received - lost principal)/total principal
    public ReturnRateInfo calculateReturnRate() {
        // Get summary json object.
        JSONObject summaryObject = getDataObject(DBContract.SummaryTable.TABLE_NAME);
        if (summaryObject == null) return null;
        // From this takes out outstandingPrincipal and receivedPrincipal
        // as this would be total capital on which we have received interest.
        try {
            double outstandingPrincipal = summaryObject.getDouble("outstandingPrincipal");
            double receivedPrincipal = summaryObject.getDouble("receivedPrincipal");
            double totalPrincipal = outstandingPrincipal + receivedPrincipal;
            double receivedInterest = summaryObject.getDouble("receivedInterest");
            // This is the actual principal which we have wrote off because of defaults.
            // we will reduce this from interest received.
            double defaultedPrincipal = getDefaultedPrincipal();
            ReturnRateInfo returnRateInfo = new ReturnRateInfo();
            returnRateInfo.setOutstandingPrincipal(outstandingPrincipal);
            returnRateInfo.setReceivedPrincipal(receivedPrincipal);
            returnRateInfo.setInterestEarned(receivedInterest);
            returnRateInfo.setLostPrincipal(defaultedPrincipal);
            return returnRateInfo;
        } catch (Exception e) {
            MainApplication.reportCaughtException(e);
            return null;
        }
    }

    // Select total(noteAmount) - total(paymentsReceived) from notes where loanStatus in ('Charged Off', 'Default')
    private double getDefaultedPrincipal() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String query = "Select total(noteAmount) - total(paymentsReceived) from notes where loanStatus in ('Charged Off')";
        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.getCount() == 0) {
                return 0;
            }
            cursor.moveToFirst();
            double lostAmount = cursor.getDouble(0);
            return lostAmount;
        } catch(Exception e) {
            MainApplication.reportCaughtException(e);
            return 0;
        }
        finally {
            cursor.close();
        }

    }

    /**
     * Get the type of note statuses we have for our notes.
     * @return
     */
    public List<String> getNoteStatuses() {
        String query = "SELECT " + NoteOwnedResponseData.LOAN_STATUS + " FROM " + DBContract.NotesTable.TABLE_NAME
                + " GROUP BY " + NoteOwnedResponseData.LOAN_STATUS;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<String> statuses = new ArrayList<>();
        try {
            while (cursor.moveToNext()) statuses.add(cursor.getString(0));
        } finally {
            cursor.close();
        }
        return statuses;
    }

    public JSONObject getDBLastActivity() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String query = "SELECT " + DBContract.COLUMN_NAME_DATA + " FROM " + DBContract.ActivityTable.TABLE_NAME +
                " WHERE " + DBContract.COLUMN_NAME_ACCOUNT_ID + "=?" + " ORDER BY " +
                DBContract.COLUMN_NAME_CREATED_DATE + " desc LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(DBContract.DEFAULT_ACCOUNT_ID)});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return getBlobAsJSONObject(cursor, 0);
        } finally {
            cursor.close();
        }
    }

    public String getOldestActivityDate() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String query = "SELECT " + DBContract.COLUMN_NAME_CREATED_DATE
                + " FROM " + DBContract.ActivityTable.TABLE_NAME
                + " ORDER BY " + DBContract.COLUMN_NAME_CREATED_DATE
                + " ASC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getString(0);
        } finally {
            cursor.close();
        }
    }

    /**
     * Data is returned in list in order from older to newer.
     */
    public ActivityQueryResult getDBActivityFor(int days) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String query = activityQueryForLast(days);
        Cursor cursor = db.rawQuery(query, null);
        List<JSONObject> data = new ArrayList<>();
        ActivityQueryResult queryResult = new ActivityQueryResult();
        queryResult.setData(data);
        try {
            for (int i = 0; cursor.moveToNext(); i++) {
                if (i == 0) {
                    queryResult.setOldestActivityDate(cursor.getString(1));
                }
                if (cursor.isLast()) {
                    queryResult.setNewestActivityDate(cursor.getString(1));
                }
                data.add(getBlobAsJSONObject(cursor, 0));
            }
        } finally {
            cursor.close();
        }

        return queryResult;
    }

    private static String activityQueryForLast(int days) {
        return "SELECT " + DBContract.COLUMN_NAME_DATA +
                ", " + DBContract.COLUMN_NAME_CREATED_DATE +
                " FROM " + DBContract.ActivityTable.TABLE_NAME +
                " WHERE " + DBContract.COLUMN_NAME_ACCOUNT_ID + "=" +
                DBContract.DEFAULT_ACCOUNT_ID + " AND CREATED_DATE BETWEEN datetime('now', '-" +
                days + " days') AND datetime('now') order by CREATED_DATE ASC";
    }

    public JSONObject getBlobAsJSONObject(Cursor cursor, int columnIndex) {
        JSONObject returnObject = null;
        if (cursor.getCount() > 0) {
            byte[] results = cursor.getBlob(columnIndex);
            try {
                returnObject = new JSONObject(new String(results));
            } catch (JSONException e) {
                throw new InvalidResponseException(e);
            }
        }
        return returnObject;
    }

    /**
     * Get the JSON Object from the db for the given table.
     * This method assume the given table name would have certain pre-defined
     * set of columns.
     * <p/>
     * This shouldn't be invoked in main thread but in AsyncTask or IntentActivity.
     *
     * @return JSONObject which is same as the user can expect from the REST service call.
     */
    public JSONObject getDataObject(String tableName) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName,
                new String[]{DBContract.COLUMN_NAME_DATA},
                DBContract.COLUMN_NAME_ACCOUNT_ID + "=?",
                new String[]{"" + DBContract.DEFAULT_ACCOUNT_ID},
                null, null, null);

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return getBlobAsJSONObject(cursor, 0);
        } finally {
            cursor.close();
        }
    }

    /**
     * Save the JSON Object in given db table.
     * This method assume the given table name would have certain pre-defined
     * set of columns.
     * <p/>
     * This shouldn't be invoked in main thread but in AsyncTask or IntentActivity.
     *
     * @param response JSON Object which needs to be saved.
     */
    void saveDataObject(String tableName, JSONObject response) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        // New value for one column
        ContentValues values = new ContentValues();

        values.put(DBContract.COLUMN_NAME_DATA, response.toString());
        values.put(DBContract.COLUMN_NAME_CREATED_DATE, "datetime()");

        // Which row to update, based on the ID
        String selection = DBContract.COLUMN_NAME_ACCOUNT_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(DBContract.DEFAULT_ACCOUNT_ID)};

        int count = db.update(tableName, values, selection, selectionArgs);
        // Update will not work when there is no existing record.
        if (count == 0) {
            // Create a new map of values, where column names are the keys
            values.put(DBContract.COLUMN_NAME_ACCOUNT_ID, DBContract.DEFAULT_ACCOUNT_ID);

            // Insert the new row, returning the primary key value of the new row
            db.insert(tableName, null, values);
        }
    }

    private void saveOrUpdateNote(SQLiteDatabase db, JSONObject note) {
        // New value for one column
        ContentValues values = new ContentValues();
        for (int i = 0; i < DBContract.NotesTable.COL_NAME_AND_TYPE.length; i++) {
            // Except COLUMN_NAME_ACCOUNT_ID we should have matching json object in incoming value.
            String colName = DBContract.NotesTable.COL_NAME_AND_TYPE[i][0];
            if (colName.equals(DBContract.COLUMN_NAME_ACCOUNT_ID)) {
                values.put(DBContract.COLUMN_NAME_ACCOUNT_ID, DBContract.DEFAULT_ACCOUNT_ID);
            } else {
                try {
                    // Some field wouldn't be present for e.g. issue date would be null
                    // till loan is issued.
                    if (note.has(colName)) {
                        Object value = note.get(colName);
                        if (value != null) values.put(colName, value.toString());
                    }
                } catch (JSONException e) {
                    Log.e(MainApplication.TAG, null, e);
                }
            }
        }
        db.insertWithOnConflict(DBContract.NotesTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Creates a new strategy in the DB.
     *
     * @param strategyConfig Strategy object which needs to be persisted.
     */
    public long saveNewStrategy(StrategyConfig strategyConfig) {
        long strategyId = -1;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DBContract.COLUMN_NAME_ACCOUNT_ID, DBContract.DEFAULT_ACCOUNT_ID);
        values.put(DBContract.COLUMN_NAME_CREATED_DATE, "datetime()");
        values.put(DBContract.COLUMN_STRATEGY_NAME, strategyConfig.getStrategyName());
        values.put(DBContract.StrategiesTable.COLUMN_IS_ACTIVE, strategyConfig.isActive() ? 1 : 0);
        values.put(DBContract.StrategiesTable.COLUMN_MAX_LOANS_PER_DAY, strategyConfig.getMaxLoansPerDay());
        values.put(DBContract.StrategiesTable.COLUMN_AMOUNT_PER_NOTE, strategyConfig.getAmountPerNote());
        values.put(DBContract.StrategiesTable.COLUMN_TARGET_PORTFOLIO, strategyConfig.getTargetPortfolio());
        values.put(DBContract.COLUMN_STRATEGY_FILTERS, strategyConfig.getFilter().toString());
        values.put(DBContract.COLUMN_STRATEGY_TYPE, DBContract.StrategiesTable.STRATEGY_TYPE.LOCAL.toString());

        strategyId = db.insert(DBContract.StrategiesTable.TABLE_NAME, null, values);

        return strategyId;
    }

    public Cursor getLocalStrategies() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(DBContract.StrategiesTable.TABLE_NAME,
                new String[]{"_ID", DBContract.COLUMN_STRATEGY_NAME, DBContract.COLUMN_STRATEGY_FILTERS, DBContract.COLUMN_NAME_CREATED_DATE},
                DBContract.COLUMN_STRATEGY_TYPE + "=?",
                new String[]{DBContract.StrategiesTable.STRATEGY_TYPE.LOCAL.toString()},
                null, null, DBContract.COLUMN_NAME_CREATED_DATE + " DESC");
        return cursor;
    }

    public NotesCategory getNotesCategory() {
        int currentNotes = countNoteByStatus(NotesCategory.NOTE_CURRENT);
        int notYetIssuedNotes = countNoteByStatus(NotesCategory.NOTE_NOT_ISSUED);
        int paidNotes = countNoteByStatus(NotesCategory.NOTE_PAID);
        int lateNotes16_30 = countNoteByStatus(NotesCategory.NOTE_LATE_16_30);
        int lateNotes31_120 = countNoteByStatus(NotesCategory.NOTE_LATE_31_120);
        int gracePeriodNotes = countNoteByStatus(NotesCategory.NOTE_GRACE);
        int defaultNotes = countNoteByStatus(NotesCategory.NOTE_DEFAULT);
        int chargedOffNotes = countNoteByStatus(NotesCategory.NOTE_CHARGED_OFF);
        NotesCategory notesCategory = new NotesCategory(currentNotes, notYetIssuedNotes, paidNotes, lateNotes16_30, lateNotes31_120, gracePeriodNotes, defaultNotes, chargedOffNotes);
        return notesCategory;
    }

    public Cursor getAllNotes(String where) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String[] columns = new String[]{"_ID", NoteOwnedResponseData.SUB_GRADE, NoteOwnedResponseData.NOTE_ID,
                NoteOwnedResponseData.NOTE_AMOUNT, NoteOwnedResponseData.PAYMENTS_RECEIVED,
                NoteOwnedResponseData.LOAN_STATUS,
                // Format the date time in query results and
                NoteOwnedResponseData.ORDER_DATE};
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(columns[i]);
        }
        sb.append(" from ").append(DBContract.NotesTable.TABLE_NAME);
        if (where != null) {
            sb.append(" ").append(where);
        }
        sb.append(" order by ").append(NoteOwnedResponseData.ORDER_DATE).append(" desc");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        return cursor;
    }

    // Return query string like "from NOTES where loanStatus in ('Current', Issued')"
    public static String notesWhereClauseByStatus(List<String> loanStatus) {
        String query = "where loanStatus ";
        query += "in (";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < loanStatus.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("'" + loanStatus.get(i) + "'");
        }
        query += sb.toString() + ")";
        return query;
    }

    private int countNoteByStatus(List<String> loanStatus) {
        String query = "from " + DBContract.NotesTable.TABLE_NAME + " " + notesWhereClauseByStatus(loanStatus);
        return countQuery(query, null);
    }

    private int countQuery(String query, String[] args) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        query = "select count(*) " + query;
        Cursor mCount = db.rawQuery(query, args);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    public JSONObject getNoteById(String noteId) {
        String query = "SELECT * FROM " + DBContract.NotesTable.TABLE_NAME + " where " + NoteOwnedResponseData.NOTE_ID + "='" + noteId + "'";
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor mCount = db.rawQuery(query, null);
        JSONObject loanJSONObject = new JSONObject();
        if (mCount.getCount() > 0) {
            mCount.moveToFirst();
            String[] columnNames = mCount.getColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                String value = mCount.getString(mCount.getColumnIndex(columnNames[i]));
                if (value != null) {
                    try {
                        loanJSONObject.put(columnNames[i], value);
                    } catch (JSONException e) {
                        Log.e(MainApplication.TAG, null, e);
                    }
                }
            }
        }
        mCount.close();
        return loanJSONObject.length() == 0 ? null : loanJSONObject;
    }

    /**
     *
     * @param responseData
     * @return Returns the delta with previous notes, if any.
     */
    public JSONObject saveNotes(NotesOwnedResponseData responseData) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        JSONObject deltaJson = new JSONObject();
        JSONArray newLoans = new JSONArray();
        JSONArray updatedLoans = new JSONArray();
        try {
            db.beginTransaction();
            int existingNotesCount = countQuery("from " + DBContract.NotesTable.TABLE_NAME, null);
            // In this list we will keep list of all the note id's which we are processing
            // so that in the end we can delete all the note id's which doesn't exist now
            // this can happen as loans are usually withdrawn, so we would rather not
            // keep stale data.
            // TODO - This needs to be implemented.
            List<String> noteIdsInResponse = new ArrayList<String>();
            while (responseData.hasNextNote()) {
                JSONObject noteJson = responseData.noteToJson(responseData.nextNote());
                // In certain cases we may not be able to map the note data.
                if (noteJson == null) continue;
                // Check if we have any existing loan then calculate activity else
                // skip entire activity logic.
                if (existingNotesCount > 0) {
                    String noteId = noteJson.getString(NoteOwnedResponseData.NOTE_ID);
                    JSONObject oldNote = getNoteById(noteId);
                    if (oldNote == null) {
                        // Save the noteId of new loans.
                        newLoans.put(newLoans.length(), noteId);
                    } else {
                        // Compare here existingLoan with the new one to find differences.
                        JSONObject loanDelta = ActivityUtil.calculateDelta(oldNote, noteJson);
                        if (loanDelta != null) {
                            JSONObject loanObject = new JSONObject();
                            loanObject.put(noteId, loanDelta);
                            updatedLoans.put(updatedLoans.length(), loanObject);
                        }
                    }
                }
                saveOrUpdateNote(db, noteJson);
            }
            // Check if we have any existing loan then calculate activity else
            // skip entire activity logic.
            if (existingNotesCount > 0) {
                if (newLoans.length() > 0) {
                    deltaJson.put(ActivityUtil.NOTE_CHANGES_NOTES_NEW, newLoans);
                }
                if (updatedLoans.length() > 0) {
                    deltaJson.put(ActivityUtil.NOTE_CHANGES_NOTE_UPDATES, updatedLoans);
                }
                int newNotesCount = countQuery("from " + DBContract.NotesTable.TABLE_NAME, null);
                if (newNotesCount != existingNotesCount) {
                    ActivityUtil.addValueChange(deltaJson, ActivityUtil.NOTE_CHANGES_COUNT,
                            String.valueOf(existingNotesCount), String.valueOf(newNotesCount));
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(MainApplication.TAG, "Error persisting notes.", e);
            MainApplication.reportCaughtException(e);
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e) {
                // Ignore exception
            }
            responseData.closeInputStream();
        }
        return deltaJson.length() > 0 ? deltaJson : null;
    }
}
