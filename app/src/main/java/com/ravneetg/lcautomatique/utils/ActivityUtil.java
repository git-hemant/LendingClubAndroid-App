package com.ravneetg.lcautomatique.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.data.ActivityOverPeriod;
import com.ravneetg.lcautomatique.data.NotesCategory;
import com.ravneetg.lcautomatique.data.ReturnRateInfo;
import com.ravneetg.lcautomatique.data.gson.ActivityQueryResult;
import com.ravneetg.lcautomatique.db.DBContract;
import com.ravneetg.lcautomatique.db.DBHelper;
import com.ravneetg.lcautomatique.request.data.response.AccountSummaryResponseData;
import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by khenush on 7/7/2015.
 */
public class ActivityUtil {

    public static final String NEW_FIELD = "n";
    public static final String OLD_FIELD = "o";
    public static final String SUMMARY_CHANGES = "summary";
    public static final String NOTE_CHANGES = "notes";
    public static final String NOTE_CHANGES_NOTES_NEW = "new";
    public static final String NOTE_CHANGES_NOTE_UPDATES = "updates";
    public static final String NOTE_CHANGES_COUNT = "count";

    // This map would list all the fields which we want to calculate and monitor
    // for any differences between 2 loans or between summary requests.
    private static Map<String, Boolean> monitoredFields = new HashMap<>();
    static {
        monitoredFields.put(AccountSummaryResponseData.ACCOUNT_TOTAL, Boolean.TRUE);
        monitoredFields.put(AccountSummaryResponseData.AVAILABLE_CASH, Boolean.TRUE);
        monitoredFields.put(NoteOwnedResponseData.LOAN_STATUS, Boolean.TRUE);
        monitoredFields.put(NoteOwnedResponseData.LOAN_LENGTH, Boolean.TRUE);
        monitoredFields.put(NoteOwnedResponseData.NOTE_AMOUNT, Boolean.TRUE);
        monitoredFields.put(NoteOwnedResponseData.PAYMENTS_RECEIVED, Boolean.TRUE);
    }
    private ActivityUtil() {
        throw new RuntimeException("Unsupported");
    }

    public static void showReturnInfoDialog(final Activity activity, ReturnRateInfo rr) {
        // We will show dialog like this
        /*
        Principal: Received ($) + Outstanding($) = $
        Earned: Interest($) - Losses ($) = $
        Return Rate: ()Earned() / Principal) = N%
        OK   "More Info" - click on more info will open following link.
         https://gist.github.com/git-hemant/e8138682b36044df308c
        */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        StringBuilder sb = new StringBuilder();
        String totalPrincipal = Util.formatDouble(rr.getReceivedPrincipal() + rr.getOutstandingPrincipal());
        sb.append("Principal Received: $" + Util.formatDouble(rr.getReceivedPrincipal()));
        sb.append("\n");
        sb.append("Principal Outstanding: $" + Util.formatDouble(rr.getOutstandingPrincipal()));
        sb.append("\n");
        sb.append("Total Pricipal: $" + totalPrincipal);
        sb.append("\n\n");
        String totalEarned = Util.formatDouble(rr.getInterestEarned() - rr.getLostPrincipal());
        sb.append("Interest received: $" + Util.formatDouble(rr.getInterestEarned()));
        sb.append("\n");
        sb.append("Charged Off: $" + Util.formatDouble(rr.getLostPrincipal()));
        sb.append("\n");
        sb.append("Total profit: $" + totalEarned);
        sb.append("\n\n");
        sb.append("$" + totalEarned + "/" + "$" + totalPrincipal + " X 100=" + Util.formatDouble(rr.calculateReturnRate()) + "%");
        sb.append("\n");
        sb.append("Return Rate: " + Util.formatDouble(rr.calculateReturnRate()) + "%");
                //sb.append("Return Rate: " + Util.formatDouble(rr.calculateReturnRate()) + "%");
                alertDialogBuilder.setMessage(sb.toString());
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing.
            }
        });
        alertDialogBuilder.setNegativeButton("More Info", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String url = "https://gist.github.com/git-hemant/e8138682b36044df308c";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                activity.startActivity(i);
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static JSONObject calculateActityForSummary(DBHelper dbHelper, AccountSummaryResponseData responseData) {
        // For calculating difference between existing summary and new summary we
        // need to read existing summary.
        JSONObject oldSummary = dbHelper.getDataObject(DBContract.SummaryTable.TABLE_NAME);
        // We don't have any baseline to compare so skip rest of execution.
        if (oldSummary == null) {
            return null;
        }
        if (responseData.getException() == null && responseData.getResponseData() != null) {
            JSONObject newSummary = responseData.getResponseData();
            return calculateDelta(oldSummary, newSummary);
        }
        return null;
    }

    public static JSONObject calculateDelta(JSONObject oldData, JSONObject newData) {
        Iterator<String> newSummaryKeys = newData.keys();
        JSONObject deltaObject = new JSONObject();
        while (newSummaryKeys.hasNext()) {
            String key = newSummaryKeys.next();
            // Ignore difference between any of the key which we don't
            // want to track.
            if (monitoredFields.containsKey(key)) {
                try {
                    String newValue = newData.get(key).toString();
                    String oldValue = oldData.get(key).toString();
                    if (NoteOwnedResponseData.LOAN_STATUS.equals(key)) {
                        // Certain loan status changes we will skip and not record.
                        if (!loanStatusChangeNeedsTracking(newValue, oldValue)) continue;
                    }
                    if (!newValue.equals(oldValue)) {
                        // Check if the string values are not same but they are actually
                        // same number for e.g. 21 or 21.0
                        // Even though exceptions are expensive it should be okay here
                        // as we will do this only when strings are not equal which
                        // shouldn't be too many.
                        try {
                            double dNew = Double.valueOf(newValue);
                            double dOld = Double.valueOf(oldValue);
                            if (dNew == dOld) continue;
                        } catch (RuntimeException e) {

                        }
                        addValueChange(deltaObject, key, oldValue, newValue);
                    }
                } catch (JSONException e) {
                    Log.e(MainApplication.TAG, null, e);
                }
            }
        }
        return (deltaObject.length() > 0) ? deltaObject : null;
    }

    private static boolean loanStatusChangeNeedsTracking(String newStatus, String oldStatus) {
        if (NotesCategory.NOTE_CURRENT.contains(oldStatus) && NotesCategory.NOTE_CURRENT.contains(newStatus)) return false;
        if (NotesCategory.NOTE_NOT_ISSUED.contains(oldStatus) && NotesCategory.NOTE_NOT_ISSUED.contains(newStatus)) return false;
        return true;
    }

    public static void addValueChange(JSONObject jsonObject, String key, String oldValue, String newValue) {
        JSONObject valueChanges = new JSONObject();
        try {
            valueChanges.put(OLD_FIELD, oldValue);
            valueChanges.put(NEW_FIELD, newValue);
            jsonObject.put(key, valueChanges);
        } catch (JSONException e) {
            Log.e(MainApplication.TAG, null, e);
        }
    }

    /**
     * This method is used in ChangesActivity to crunch activity for given number of days.
     */
    public static ActivityOverPeriod getActivityOver(DBHelper dbHelper, int days) {
        // TODO - It might be more efficient to use Cursor here and process
        // individual updates.
        ActivityQueryResult queryResult = dbHelper.getDBActivityFor(days);
        List<JSONObject> allActivityUpdates = queryResult.getData();
        ActivityOverPeriod activityOverPeriod = new ActivityOverPeriod();
        if (allActivityUpdates.size() == 0) {
            // Before returning ensure we set the oldest activity date as we are
            // showing different message based on any activity present or not in db.
            activityOverPeriod.setOverallOldestActivityDate(dbHelper.getOldestActivityDate());
            return activityOverPeriod;
        }
        activityOverPeriod.setNewestActivityDate(queryResult.getNewestActivityDate());
        activityOverPeriod.setOldestActivityDate(queryResult.getOldestActivityDate());
        // Since we are iterating from last to newest activity
        // we will keep on overriding status, as this way we will
        // have latest status.
        Map<String, String> noteStatusTracker = new HashMap<>();

        // Assumption List of JSONObject is in list from oldest to newest
        // Iterate through list from top to down and get first accountTotal value with "n" attribute.
        double latestAccountTotal = getPropertyFromJsonList(allActivityUpdates, AccountSummaryResponseData.ACCOUNT_TOTAL, true, false, true);
        // Iterate through list from down to top and get first accountTotal value with "o" attribute.
        double oldestAccountTotal = getPropertyFromJsonList(allActivityUpdates, AccountSummaryResponseData.ACCOUNT_TOTAL, true, true, false);
        // Set the different in account total as change for this period.
        activityOverPeriod.setChangeAccountTotal(latestAccountTotal - oldestAccountTotal);

        // If we didn't found any search results and
        // if we did found some activity in the system then we also want to know what is the
        // oldest activity in the database (not in the given time period).
        // This information would help giving user proper warning message
        // like broadening search query may help user or not.
        if (activityOverPeriod.getNewestActivityDate() != null) {
            activityOverPeriod.setOverallOldestActivityDate(dbHelper.getOldestActivityDate());
        }

        // Now iterate over all activity json object
        /*
        - Iterate through updates starting from oldest and collect loanStatus changes for late(all), "default", "charged-off" and "paid"
            ensure we are using last status for the given loanId, this we can do by first building Map from all updates where key is noteId and value is status
            and if we process from oldest to newest we will have right status.
        - Collect paymentsReceived by doing n-o for each entry.
        */
        // Key note id and value amount of payment received for this note.
        Map<String, Double> notePayments = new HashMap<>();
        // Key note id and value last note status - as we are doing from oldest to latest processing.
        Map<String, String> noteStatus = new HashMap<>();
        List<String> newNotes = new ArrayList<>();

        double paymentReceived = 0;
        for (int i = 0; i < allActivityUpdates.size(); i++) {
            JSONObject activityData = allActivityUpdates.get(i);

            // Check if "notes" data is there as that's the only thing we are interested here.
            if (!activityData.has("notes")) continue;
            try {
                JSONObject noteChanges = activityData.getJSONObject("notes");
                if (noteChanges.has("updates")) {
                    processNoteUpdates(noteChanges.getJSONArray("updates"), notePayments, noteStatus);
                }
                if (noteChanges.has("new")) {
                    JSONArray newArray = noteChanges.getJSONArray("new");
                    for (int j =0; j < newArray.length(); j++) {
                        String newNoteId = newArray.getString(j);
                        newNotes.add(newNoteId);
                    }
                }
            } catch (JSONException e) {
                Log.d(MainApplication.TAG, e.getMessage(), e);
                MainApplication.reportCaughtException(e);
            }
        }
        activityOverPeriod.setChangePaymentsReceived(calculatePaymentsReceived(notePayments));

        // noteStatus is having key as note id and value as status but now
        // we want to build another Map where key is status and value is list
        // of all notes, so that we can group the notes by changes.
        Map<String, List<String>> noteStatusByStatus = organizeNotesByStatus(noteStatus);
        activityOverPeriod.setNewNotes(newNotes);
        activityOverPeriod.setNoteStatusByStatus(noteStatusByStatus);

        return activityOverPeriod;
    }

    /**
     * Calculate total payment received based on the given input map.
     * @param notePayments Key is note id and value is payment received for that note.
     */
    private static double calculatePaymentsReceived(Map<String, Double> notePayments) {
        double paymentsReceived = 0;
        for (Iterator<String> iterator = notePayments.keySet().iterator(); iterator.hasNext();) {
            paymentsReceived += notePayments.get(iterator.next()).doubleValue();
        }
        return paymentsReceived;
    }

    /**
     * Organize the given input so that we have key as note status and value as list of note ids.
     *
     * @param noteStatus Key is noteId and value is status
     */
    private static Map<String, List<String>> organizeNotesByStatus(Map<String, String> noteStatus) {
        // noteStatus is having key as note id and value as status but now
        // we want to build another Map where key is status and value is list
        // of all notes, so that we can group the notes by changes.
        Map<String, List<String>> noteStatusByStatus = new HashMap<>();
        if (noteStatus.size() > 0) {
            Iterator<String> iterator = noteStatus.keySet().iterator();
            while (iterator.hasNext()) {
                String noteId = iterator.next();
                String noteSt = noteStatus.get(noteId);
                List<String> notesWithSameStatus = noteStatusByStatus.get(noteSt);
                if (notesWithSameStatus == null) {
                    notesWithSameStatus = new ArrayList<>();
                    noteStatusByStatus.put(noteSt, notesWithSameStatus);
                }
                notesWithSameStatus.add(noteId);
            }
        }
        return noteStatusByStatus;
    }

    // Process all the note updates which was recorded for single refresh.
    //    "notes": {
    //        "updates": [{
    //            "56754539": {
    //                "paymentsReceived": {
    //                    "n": "6.38",
    //                    "o": "5.74"
    //                }
    //            }
    //        },
    //        {
    //            "42907008": {
    //            "paymentsReceived": {
    //                "n": "12.54",
    //                "o": "11.71"
    //            }
    //        }
    //    }
    private static void processNoteUpdates(JSONArray updates, Map<String, Double> notePayments, Map<String, String> noteStatus) throws JSONException {
        for (int i = 0; i < updates.length(); i++) {
            if (!(updates.get(i) instanceof JSONObject)) continue;
            JSONObject jsonObject = (JSONObject) updates.get(i);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                Object objNoteId = iterator.next();
                if (!(objNoteId instanceof String)) continue;
                try {
                    Object objNoteUpdate = jsonObject.get((String) objNoteId);
                    if (objNoteUpdate instanceof JSONObject) {
                        JSONObject noteUpdate = (JSONObject) objNoteUpdate;
                        // Following shouldn't be if-else as we can have multiple updates
                        // together.
                        if (noteUpdate.has("loanStatus")) {
                            String newStatus = noteUpdate.getJSONObject("loanStatus").getString("n");
                            noteStatus.put( (String) objNoteId, newStatus);
                        }
                        if (noteUpdate.has("paymentsReceived")) {
                            JSONObject jsonPaymentReceived = noteUpdate.getJSONObject("paymentsReceived");
                            if (jsonPaymentReceived.has("n") && jsonPaymentReceived.has("o")) {
                                try {
                                    double n = jsonPaymentReceived.getDouble("n");
                                    double o = jsonPaymentReceived.getDouble("o");
                                    // Rare condition when we have 2 loans for same note.
                                    double value = n - o;
                                    if (notePayments.containsKey(objNoteId)) {
                                        Double existingValue = notePayments.get(objNoteId);
                                        value = existingValue + value;
                                    }
                                    notePayments.put((String) objNoteId, value);
                                } catch (NumberFormatException e) {
                                    Log.d(MainApplication.TAG, e.getMessage(), e);
                                    MainApplication.reportCaughtException(e);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.d(MainApplication.TAG, e.getMessage(), e);
                    MainApplication.reportCaughtException(e);
                }
            }

        }
    }

    /**
     *
     * Iterate in the json object and look for the property in notes or in summary
     * e.g. json
     *
     "notes:" {
        "count": {
        "n": 500,
        "o": 420
        }
     }
     "summary": {
         "availableCash": {
             "n": "250.23",
             "o": "227.89"
         },
         "accountTotal": {
             "n": "40415.64",
             "o": "40387.32"
         }
     }
     */
    private static double getPropertyFromJsonList(List<JSONObject> activityList, String prop, boolean inSummary, boolean startFromTop, boolean newAttribute) {
        double returnValue = 0;
        for (int i = startFromTop ? 0 : (activityList.size() - 1); true; ) {
            // If we are iterating from top and reached end then end loop.
            if (startFromTop && i >= activityList.size()) break;
            // If we are iterating from bottom and reached less than zero then terminate.
            if (!startFromTop && i < 0) break;
            JSONObject activityData = activityList.get(i);
            try {
                String topLevelElement = inSummary ? "summary" : "notes";
                if (activityData.has(topLevelElement)) {
                    JSONObject topLevelJson = activityData.getJSONObject(topLevelElement);
                    if (topLevelJson.has(prop)) {
                        JSONObject propJson = topLevelJson.getJSONObject(prop);
                        String attr = newAttribute ? "n" : "o";
                        if (propJson.has(attr)) {
                            returnValue = propJson.getDouble(attr);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                MainApplication.reportCaughtException(e);
            }
            if (startFromTop) i++; else i--;
        }
        return returnValue;
    }
}
