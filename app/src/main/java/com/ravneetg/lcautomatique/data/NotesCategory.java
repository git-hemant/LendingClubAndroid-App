package com.ravneetg.lcautomatique.data;

import com.ravneetg.lcautomatique.db.DBContract;
import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;
import com.ravneetg.lcautomatique.utils.LoanUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HemantSingh on 1/24/2015.
 */
public class NotesCategory {
    public static final String NOTE_STATUS_CHARGED_OFF = "Charged Off";
    public static final String NOTE_STATUS_CURRENT = "Current";
    public static final String NOTE_STATUS_DEFAULT = "Default";
    public static final String NOTE_STATUS_PAID = "Fully Paid";
    public static final String NOTE_STATUS_IN_FUNDING = "In Funding";
    public static final String NOTE_STATUS_IN_GRACE = "In Grace Period";
    public static final String NOTE_STATUS_IN_REVIEW = "In Review";
    public static final String NOTE_STATUS_ISSUED = "Issued";
    public static final String NOTE_STATUS_ISSUING = "Issuing";
    // This is prefix string of multiple grace times
    public static final String NOTE_STATUS_LATE_16_30 = "Late (16-30 days)";
    public static final String NOTE_STATUS_LATE_31_120 = "Late (31-120 days)";
    public static final List<String> NOTE_CURRENT = Arrays.asList(NOTE_STATUS_CURRENT, NOTE_STATUS_ISSUED);
    public static final List<String> NOTE_NOT_ISSUED = Arrays.asList(NOTE_STATUS_IN_FUNDING, NOTE_STATUS_IN_REVIEW, NOTE_STATUS_ISSUING);
    public static final List<String> NOTE_PAID = Arrays.asList(NOTE_STATUS_PAID);
    public static final List<String> NOTE_LATE_16_30 = Arrays.asList(NOTE_STATUS_LATE_16_30);
    public static final List<String> NOTE_LATE_31_120 = Arrays.asList(NOTE_STATUS_LATE_31_120);
    public static final List<String> NOTE_GRACE = Arrays.asList(NOTE_STATUS_IN_GRACE);
    public static final List<String> NOTE_DEFAULT = Arrays.asList(NOTE_STATUS_DEFAULT);
    public static final List<String> NOTE_CHARGED_OFF = Arrays.asList(NOTE_STATUS_CHARGED_OFF);


    // This would be loans which have status of "Current" or "Issued"
    private int currentNotes;
    // This would be loans which have status of In Funding, In Review, or Issuing.
    private int notYetIssuedNotes;
    private int paidNotes;
    private int lateNotes16_30;
    private int lateNotes31_120;
    private int gracePeriodNotes;
    private int defaultNotes;
    private int chargedOffNotes;

    public NotesCategory(int currentNotes, int notYetIssuedNotes, int paidNotes, int lateNotes16_30, int lateNotes31_120, int gracePeriodNotes, int defaultNotes, int chargedOffNotes) {
        this.currentNotes = currentNotes;
        this.notYetIssuedNotes = notYetIssuedNotes;
        this.paidNotes = paidNotes;
        this.lateNotes16_30 = lateNotes16_30;
        this.lateNotes31_120 = lateNotes31_120;
        this.gracePeriodNotes = gracePeriodNotes;
        this.defaultNotes = defaultNotes;
        this.chargedOffNotes = chargedOffNotes;
    }

    public int getCurrentNotes() {
        return currentNotes;
    }

    public int getNotYetIssuedNotes() {
        return notYetIssuedNotes;
    }

    public int getLateNotes16_30() {
        return lateNotes16_30;
    }

    public int getLateNotes31_120() {
        return lateNotes31_120;
    }

    public int getGracePeriodNotes() {
        return gracePeriodNotes;
    }

    public int getPaidNotes() {
        return paidNotes;
    }

    public int getDefaultNotes() {
        return defaultNotes;
    }

    public int getChargedOffNotes() {
        return chargedOffNotes;
    }
}
