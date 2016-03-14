package com.ravneetg.lcautomatique.db;

import android.content.Context;

import com.ravneetg.lcautomatique.data.GenericCallback;
import com.ravneetg.lcautomatique.data.NotesCategory;
import com.ravneetg.lcautomatique.request.RequestUtil;
import com.ravneetg.lcautomatique.request.data.response.AccountSummaryResponseData;
import com.ravneetg.lcautomatique.request.data.response.AvailableCashResponseData;
import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.PortfoliosOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.ResponseData;
import com.ravneetg.lcautomatique.utils.SimpleAsyncTask;
import com.ravneetg.lcautomatique.utils.Util;

import org.json.JSONObject;

import java.util.List;

/**
 * This class is used to abstract Data access such that data can come from database
 * or REST service. Though API user would have option to select if they want to pull
 * latest data from REST service or get existing data from the database, which might
 * be stale but would be quick to access.
 *
 * All methods in this class should be invoked from thread other than UI (main) thread.
 *
 * Created by HemantSingh on 1/25/2015.
 */
public class DataManager {

    private Context ctx;
    public DataManager(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Get the Account Summary data from the DB or REST service.
     */
    public JSONObject getDBAccountSummaryData() {
        return DBHelper.getInstance(ctx).getDataObject(DBContract.SummaryTable.TABLE_NAME);
    }

    public AccountSummaryResponseData getRefreshedAccountSummaryData() {
        return RequestUtil.requestAccountSummary(ctx);
    }

    /**
     * Get the Notes Owned data from the DB or REST service.
     */
    public NotesCategory getDBNotesOwnedData() {
        return DBHelper.getInstance(ctx).getNotesCategory();
    }

    public NotesOwnedResponseData getRefreshedNotesOwnedData() {
        return RequestUtil.requestNotesOwned(ctx);
    }

    public AvailableCashResponseData getRefreshedAvailableCashData() {
        return RequestUtil.requestAvailableCash(ctx);
    }

    public JSONObject getDBPortfoliosOwnedData() {
        return DBHelper.getInstance(ctx).getDataObject(DBContract.NotesTable.TABLE_NAME);
    }

    public PortfoliosOwnedResponseData getRefreshedPortfoliosOwnedData() {
        return RequestUtil.requestPortfoliosOwned(ctx);
    }

    public void updateDbInBackground(final ResponseData responseData, final GenericCallback callback) {
        Util.doAsyncTask(new SimpleAsyncTask() {
            @Override
            public void doWork() {
                updateDb(responseData);
                callback.callBack();
            }
        });
    }

    /**
     * Return all the type of note status we have for the notes in our system.
     * @return
     */
    public List<String> getNoteStatuses() {
        return DBHelper.getInstance(ctx).getNoteStatuses();
    }

    public void updateDb(final ResponseData responseData) {
        if (responseData.getException() == null) {
            if (responseData instanceof AccountSummaryResponseData) {
                DBHelper.getInstance(ctx).saveDataObject(DBContract.SummaryTable.TABLE_NAME, responseData.getResponseData());
            } else if (responseData instanceof PortfoliosOwnedResponseData) {
                DBHelper.getInstance(ctx).saveDataObject(DBContract.PortfoliosTable.TABLE_NAME, responseData.getResponseData());
            }
        }
    }
}