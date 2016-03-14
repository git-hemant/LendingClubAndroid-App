package com.ravneetg.lcautomatique.data;

import com.ravneetg.lcautomatique.request.data.response.AccountSummaryResponseData;
import com.ravneetg.lcautomatique.request.data.response.ResponseData;
import com.ravneetg.lcautomatique.request.data.response.ResponseObject;

import org.json.JSONObject;

/**
 * Created by Hemant on 6/22/2015.
 */
public interface RefreshCallback {
    void updateSummaryUI(JSONObject data);
    void updateNotesUI(NotesCategory data);
    void showUpdateOnSummaryAndNotes(JSONObject data);
}
