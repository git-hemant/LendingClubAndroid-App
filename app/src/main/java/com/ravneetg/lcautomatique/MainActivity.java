package com.ravneetg.lcautomatique;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.ravneetg.lcautomatique.activities.AboutActivity;
import com.ravneetg.lcautomatique.activities.ChangesActivity;
import com.ravneetg.lcautomatique.activities.SettingsActivity;
import com.ravneetg.lcautomatique.adapters.DrawerDataAdapter;
import com.ravneetg.lcautomatique.data.GenericCallback;
import com.ravneetg.lcautomatique.data.NotesCategory;
import com.ravneetg.lcautomatique.data.RefreshCallback;
import com.ravneetg.lcautomatique.data.ReturnRateInfo;
import com.ravneetg.lcautomatique.db.DBContract;
import com.ravneetg.lcautomatique.db.DBHelper;
import com.ravneetg.lcautomatique.db.DataManager;
import com.ravneetg.lcautomatique.request.data.response.AccountSummaryResponseData;
import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;
import com.ravneetg.lcautomatique.utils.ActivityUtil;
import com.ravneetg.lcautomatique.utils.PreferencesUtil;
import com.ravneetg.lcautomatique.utils.SimpleAsyncTask;
import com.ravneetg.lcautomatique.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

/**
 * Main activity of the application.
 */
public class MainActivity extends ActionBarActivity implements RefreshCallback {

    public static final String INPUT_EXTRA = "start_with_refresh";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private SwipeRefreshLayout swipeLayout;
    private TextView txtAvailableCash;
    private TextView txtAccountTotal;
    private TextView txtReturnRate;
    private TextView txtCurrentNotes;
    private TextView txtNotYetIssuedNotes;
    private TextView txtLateNotes16_30;
    private TextView txtLateNotes31_120;
    private TextView txtGracePeriod;
    private TextView txtDefaultNotes;
    private TextView txtChargedOffNotes;
    private TextView txtPaidNotes;
    // All data interactions should go via DataManager API.
    private DataManager dataManager;
    // Flag to keep track of whether we are refreshing data
    // this is required to avoid multiple refreshes at same time.
    private boolean mIsRefreshing;
    private boolean mRefreshOnStart;


    // This method is called when the user click
    // on note status label
    public void onClick(View v) {
        if (v == txtReturnRate) {
            if (v.getTag() instanceof ReturnRateInfo) {
                ReturnRateInfo returnRateInfo = (ReturnRateInfo) v.getTag();
                MainApplication.track(this, "Link click", "Return rate dialog: " + returnRateInfo.calculateReturnRate());
                ActivityUtil.showReturnInfoDialog(MainActivity.this, returnRateInfo);
            }
        } else {
            onNoteClick(v);
        }
    }

    // On click event for the list when user click on anywhere
    // in the card view.
    private void onNoteClick(View v) {
        int id = v.getId();
        List<String> noteStatus = null;
        String analyticsLabel = null;
        if (id == R.id.lblCurrentNotes || id == R.id.txtCurrentNotes) {
            noteStatus = NotesCategory.NOTE_CURRENT;
            analyticsLabel = "Current";
        } else if (id == R.id.lblNotYetIssuedNotes || id == R.id.txtNotYetIssuedNotes) {
            noteStatus = NotesCategory.NOTE_NOT_ISSUED;
            analyticsLabel = "Not Issued";
        } else if (id == R.id.lblPaidNotes || id == R.id.txtPaidNotes) {
            noteStatus = NotesCategory.NOTE_PAID;
            analyticsLabel = "Paid";
        } else if (id == R.id.lblGracePeriod || id == R.id.txtGracePeriod) {
            noteStatus = NotesCategory.NOTE_GRACE;
            analyticsLabel = "Grace";
        } else if (id == R.id.lblLateNotes16_30 || id == R.id.txtLateNotes16_30) {
            noteStatus = NotesCategory.NOTE_LATE_16_30;
            analyticsLabel = "Late 16 30";
        } else if (id == R.id.lblLateNotes31_120 || id == R.id.txtLateNotes31_120) {
            noteStatus = NotesCategory.NOTE_LATE_31_120;
            analyticsLabel = "Late 31 120";
        } else if (id == R.id.lblDefaultNotes || id == R.id.txtDefaultNotes) {
            noteStatus = NotesCategory.NOTE_DEFAULT;
            analyticsLabel = "Default";
        } else if (id == R.id.lblChargedOffNotes || id == R.id.txtChargedOffNotes) {
            noteStatus = NotesCategory.NOTE_CHARGED_OFF;
            analyticsLabel = "Charged-off";
        }
        if (noteStatus != null) {
            if (v instanceof TextView) {
                analyticsLabel += " count: " + ((TextView) v).getText();
            }
            MainApplication.track(this, "Link click", analyticsLabel);
            startNotesActivity(noteStatus);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.main_sync) {
            triggerRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (BuildConfig.DEBUG) {
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }

        // Check if we need to refresh in starting
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mRefreshOnStart = Boolean.TRUE.toString().equals(extras.getString(INPUT_EXTRA));
        }

        // Initialize data manager, this is lightweight initialization
        // as data manager would create DB/Network connection only on need basis.
        this.dataManager = new DataManager(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar ,  R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                syncState();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                syncState();
            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerListView = (ListView) findViewById(R.id.listView1);
        mDrawerListView.setAdapter(new DrawerDataAdapter(this, Util.getBaseDrawerCursor(this)));
        mDrawerListView.setOnItemClickListener(drawerMenuClickListener);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(refreshListener);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        txtAvailableCash = (TextView) findViewById(R.id.txtAvailableCash);
        txtAccountTotal = (TextView) findViewById(R.id.txtAccountTotal);
        txtReturnRate = (TextView) findViewById(R.id.txtReturnRate);

        txtCurrentNotes = (TextView) findViewById(R.id.txtCurrentNotes);
        txtNotYetIssuedNotes = (TextView) findViewById(R.id.txtNotYetIssuedNotes);
        txtPaidNotes = (TextView) findViewById(R.id.txtPaidNotes);
        txtGracePeriod = (TextView) findViewById(R.id.txtGracePeriod);
        txtLateNotes16_30 = (TextView) findViewById(R.id.txtLateNotes16_30);
        txtLateNotes31_120 = (TextView) findViewById(R.id.txtLateNotes31_120);
        txtDefaultNotes = (TextView) findViewById(R.id.txtDefaultNotes);
        txtChargedOffNotes = (TextView) findViewById(R.id.txtChargedOffNotes);

        // First check if the user have not configured the application
        // then redirect the application to settings screen.
        if (!investorIdAndAPIKeyEntered()) {
            Intent settingsScreen = new Intent(MainActivity.this, SettingsActivity.class);
            this.startActivity(settingsScreen);
            return;
        }

        // Add touch listener so that we can determine where exactly
        // user clicked here.
        NoteTouchListener touchListener = new NoteTouchListener();
        txtCurrentNotes.setOnTouchListener(touchListener);
        txtNotYetIssuedNotes.setOnTouchListener(touchListener);
        txtPaidNotes.setOnTouchListener(touchListener);
        txtGracePeriod.setOnTouchListener(touchListener);
        txtLateNotes16_30.setOnTouchListener(touchListener);
        txtLateNotes31_120.setOnTouchListener(touchListener);
        txtDefaultNotes.setOnTouchListener(touchListener);
        txtChargedOffNotes.setOnTouchListener(touchListener);


        // Here we will populate the account summary data, first we will check if the
        // values are already cached in the db, otherwise we will go to network
        // and bring values from the LC server.
        Util.doAsyncTask(new SimpleAsyncTask() {
            @Override
            public void doWork() {
                JSONObject summaryData = null;
                // If we have been instructed to refresh on start
                // then don't query DB to check if we have any data
                // yet or no
                if (!mRefreshOnStart) {
                    summaryData = dataManager.getDBAccountSummaryData();
                }
                boolean refresh = mRefreshOnStart || summaryData == null;

                if (refresh) {
                    triggerRefresh();
                } else {
                    updateSummaryUI(dataManager.getDBAccountSummaryData());
                    updateNotesUI(dataManager.getDBNotesOwnedData());
                    showUpdateOnSummaryAndNotes(DBHelper.getInstance(getBaseContext()).getDBLastActivity());
                }
            }
        });

        //AppRate.with(this).clearSettingsParam();
        // App rate dialog
        AppRate.with(this)
                .setInstallDays(5) // default 10, 0 means install day.
                .setLaunchTimes(14) // Show message after this many launch of application
                .setRemindInterval(14) // Dialog is launched more than N days after neutral button clicked.
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        //Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private boolean investorIdAndAPIKeyEntered() {
        char[] apiKey = PreferencesUtil.getAPIKey(getBaseContext());
        char[] investorId = PreferencesUtil.getInvestorId(getBaseContext());
        return apiKey != null && apiKey.length > 0 && investorId != null && investorId.length > 0;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    /**
     * This method is invoked right before activity is going to be visible.
     */
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void refreshData() {
        try {
            refreshData0();
        } catch (Throwable e) {
            // Since we are gracefully handling all network and auth issues, so we should analyze this exception.
            Log.e(MainApplication.TAG, null, e);
            MainApplication.reportCaughtException(e);
        }
    }

    private void refreshData0() throws Exception {
        // First check network is available to avoid common issues.
        if (!Util.isNetworkAvailable(this, true)) {
            return;
        }

        //1) First refresh the summary.
        AccountSummaryResponseData summaryData = dataManager.getRefreshedAccountSummaryData();
        if (Util.anyErrorInResponse(this, summaryData)) {
            return;
        }

        // Update the summary in UI but wait till last to save the latest summary in DB.
        updateSummaryUI(summaryData.getResponseData());

        // 2) Refresh Notes - Notes JSON stream is read and processed using Gson
        // because it can be of larger size.
        NotesOwnedResponseData notesData = dataManager.getRefreshedNotesOwnedData();
        DBHelper dbHelper = DBHelper.getInstance(getBaseContext());
        // Save the new notes in db and return notesChanges as part of it.
        JSONObject notesChanges = dbHelper.saveNotes(notesData);
        // Now update notes in the UI.
        updateNotesUI(dataManager.getDBNotesOwnedData());
        JSONObject summaryChanges = ActivityUtil.calculateActityForSummary(dbHelper, summaryData);
        // If we have any changes then save them in DB.
        if ( (summaryChanges != null && summaryChanges.length() > 0)
                || (notesChanges != null && notesChanges.length() > 0) ) {
            JSONObject changes = new JSONObject();
            if (summaryChanges != null && summaryChanges.length() > 0) {
                changes.put(ActivityUtil.SUMMARY_CHANGES, summaryChanges);
            }
            if (notesChanges != null && notesChanges.length() > 0) {
                changes.put(ActivityUtil.NOTE_CHANGES, notesChanges);
            }
            String insertSQL = DBContract.ActivityTable.insertSQL(changes.toString());
            Log.d(MainApplication.TAG, insertSQL);
            dbHelper.getWritableDatabase().execSQL(insertSQL);
            showUpdateOnSummaryAndNotes(dbHelper.getDBLastActivity());

        }
        else {
            // Avoid show this message if refresh was auto triggered.
            if (!mRefreshOnStart) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "No updates available.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // Define callback which would update return rate info with latest summary info from db.
        GenericCallback callback = new GenericCallback() {
            @Override
            public void callBack() {
                updateReturnRateInfo();
            }
        };
        // In the end update the database with latest summary.
        dataManager.updateDbInBackground(summaryData, callback);
        mRefreshOnStart = false;
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            triggerRefresh();
        }
    };

    private void triggerRefresh() {
        if (!mIsRefreshing) {
            mIsRefreshing = true;

            Util.doAsyncTask(new SimpleAsyncTask() {
                @Override
                public void doWork() {
                    try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set the refreshing to false, so that user doesn't see loading icon.
                            swipeLayout.setRefreshing(true);
                        }
                    });
                        // Don't refresh if the investor id and API key is not entered yet.
                        if (!investorIdAndAPIKeyEntered()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Please input Investor ID and API Key in settings.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        try {
                            refreshData();
                        } catch (Throwable t) {
                            Log.e(MainApplication.TAG, t.getMessage(), t);
                            MainApplication.reportCaughtException(t);
                        }
                    } finally {
                        mIsRefreshing = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set the refreshing to false, so that user doesn't see loading icon.
                                swipeLayout.setRefreshing(false);
                            }
                        });
                    }
                }
            });
        }
    }

    private AdapterView.OnItemClickListener drawerMenuClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(id == Util.ITEM_NOTES){
                startNotesActivity(null);
            } else {
                Intent intent = null;
                String trackingLabel = null;
                if (id == Util.ITEM_HOME) {
                    //mDrawerToggle.syncState();
                    mDrawerLayout.closeDrawers();
                    //intent = new Intent(MainActivity.this, MainActivity.class);
                } else if (id == Util.ITEM_SETTINGS) {
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                } else if (id == Util.ITEM_CHANGES){
                    intent = new Intent(MainActivity.this, ChangesActivity.class);
                } else if (id == Util.ITEM_ABOUT){
                    intent = new Intent(MainActivity.this, AboutActivity.class);
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        }
    };

    private void startNotesActivity(List<String> notesStatus) {
        String trackingLabel = "New Activity - Notes";
        String notes_where = null;
        if (notesStatus != null) {
            notes_where =  DBHelper.notesWhereClauseByStatus(notesStatus);
            trackingLabel += " " + notes_where;
        }
        MainApplication.track(this, "Open Drawer", "New Activity - " + trackingLabel);
        Util.startNotesActivity(this, notes_where);
    }

    /**
     * Update the account summary UI from incoming data. The incoming data
     * can come from REST call or cached data from the db.
     */
    public void updateSummaryUI(final JSONObject data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Show the available cash and total cash by removing the decimal amount to keep it simple.
                    txtAvailableCash.setText("$" + Util.formatDouble(data.getString(AccountSummaryResponseData.AVAILABLE_CASH)));
                    txtAccountTotal.setText("$" + Util.formatDouble(data.getString(AccountSummaryResponseData.ACCOUNT_TOTAL)));
                } catch (JSONException e) {
                    // TODO - show general error here as well
                }
                updateReturnRateInfo();
            }
        });
    }

    /**
     * Update the notes info UI from incoming data. The incoming data
     * can come from REST call or cached data from the db.
     */
    public void updateNotesUI(final NotesCategory notesCategory) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showUnderlineValue(txtCurrentNotes, notesCategory.getCurrentNotes());
                showUnderlineValue(txtNotYetIssuedNotes, notesCategory.getNotYetIssuedNotes());
                showUnderlineValue(txtPaidNotes, notesCategory.getPaidNotes());
                showUnderlineValue(txtGracePeriod, notesCategory.getGracePeriodNotes());
                showUnderlineValue(txtLateNotes16_30, notesCategory.getLateNotes16_30());
                showUnderlineValue(txtLateNotes31_120, notesCategory.getLateNotes31_120());
                showUnderlineValue(txtDefaultNotes, notesCategory.getDefaultNotes());
                showUnderlineValue(txtChargedOffNotes, notesCategory.getChargedOffNotes());

            }
        });
    }

    private static void showUnderlineValue(TextView view, int value) {
        view.setText(Html.fromHtml(Util.hyperlinkHtml(value)));
    }

    private void superScriptValue(TextView view, boolean showUpArrow, String superScriptValue, boolean noteView)
    {
        if (superScriptValue == null || superScriptValue.length() == 0) return;

        String existingValue = view.getText().toString();
        // Except account total and available cash all textview are shown as hyperlink.
        if (view != txtAccountTotal && view != txtAvailableCash && view != txtReturnRate) {
            existingValue = Util.hyperlinkHtml(existingValue);
        }
        String htmlValue = existingValue + "<sup style='font-size:smaller;vertical-align:super;position: relative;top: -0.5em;'><font color='"
                + (showUpArrow ? "green" : "red") + "'>"
                + (showUpArrow ? "&uarr;" : "&darr;")
                + (noteView ? "</font><font size='10' color='blue'><u>" : "")
                + superScriptValue
                + (noteView ? "</u>" : "")
                + "</font></sup>";
        view.setText(Html.fromHtml(htmlValue));
    }


    public void showUpdateOnSummaryAndNotes(final JSONObject data) {
        if (data != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (data.has(ActivityUtil.SUMMARY_CHANGES)) {
                            updateSummaryActivityUI((JSONObject) data.get(ActivityUtil.SUMMARY_CHANGES));
                        }
                        if (data.has(ActivityUtil.NOTE_CHANGES))
                            updateNotesActivityUI((JSONObject) data.get(ActivityUtil.NOTE_CHANGES));
                    } catch (JSONException e) {
                        Log.e(MainApplication.TAG, "Exception during activity UI update.", e);
                    }
                }
            });
        }
    }

    private void updateSummaryActivityUI(JSONObject summaryChanges) throws JSONException {
        // First check if we have any summary changes in the recent activity.
        if (summaryChanges == null) return;
        if (summaryChanges.has(AccountSummaryResponseData.AVAILABLE_CASH)) {
            showTextActivity(txtAvailableCash, (JSONObject) summaryChanges.get(AccountSummaryResponseData.AVAILABLE_CASH), false);
        }
        if (summaryChanges.has(AccountSummaryResponseData.ACCOUNT_TOTAL)) {
            showTextActivity(txtAccountTotal, (JSONObject) summaryChanges.get(AccountSummaryResponseData.ACCOUNT_TOTAL), false);
        }
    }

    private void updateReturnRateInfo() {
        Util.doAsyncTask(new SimpleAsyncTask() {
            @Override
            public void doWork() {
                // Get summary data.
                final ReturnRateInfo returnRate = DBHelper.getInstance(getBaseContext()).calculateReturnRate();
                if (returnRate != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String rr = Util.formatDouble(returnRate.calculateReturnRate()) + "%";
                            rr = Util.hyperlinkHtml(rr);
                            //String htmlTxt = "<a href='https://gist.github.com/git-hemant/e8138682b36044df308c'>" + rr + "</a>";
                            txtReturnRate.setText(Html.fromHtml(rr));
                            txtReturnRate.setTag(returnRate);
                        }
                    });
                }
            }
        });

    }
    /**
     * In this method we will update the value in TextView to show superscript
     * to show whether it have added or deleted notes. We will also attach
     * list with TextView which would have reference of noteIds which got
     * changed in that status.
     */
    private void updateNotesActivityUI(JSONObject notesChanges) throws JSONException {
        // First check if we have any summary changes in the recent activity.
        if (notesChanges == null) return;
        // In this UI we would handle only updates in notes and not new notes.
        if (!notesChanges.has(ActivityUtil.NOTE_CHANGES_NOTE_UPDATES)) return;

        JSONArray noteUpdates = notesChanges.getJSONArray(ActivityUtil.NOTE_CHANGES_NOTE_UPDATES);
        Map<TextView, AtomicInteger> noteStatusChanges = new HashMap<>();
        for (int i = 0; i < noteUpdates.length(); i++) {
            // In this json object first key is note id
            JSONObject noteUpdate = noteUpdates.getJSONObject(i);
            String noteId = noteUpdate.keys().next();
            JSONObject noteUpdateValues = noteUpdate.getJSONObject(noteId);
            if (noteUpdateValues.has(NoteOwnedResponseData.LOAN_STATUS)) {
                JSONObject noteStatusUpdate = noteUpdateValues.getJSONObject(NoteOwnedResponseData.LOAN_STATUS);
                String newStatus = noteStatusUpdate.getString(ActivityUtil.NEW_FIELD);
                String oldStatus = noteStatusUpdate.getString(ActivityUtil.OLD_FIELD);
                TextView textViewNewStatus = noteStatusToTextView(newStatus);
                TextView textViewOldStatus = noteStatusToTextView(oldStatus);
                // Add this note id in tag of TextView - this way
                // we will know which noteid is added in which textview/status.
                addNoteIdInTextViewTag(textViewNewStatus, noteId);
                addNoteIdInTextViewTag(textViewOldStatus, noteId);
                AtomicInteger atomicIntegerNewStatus = noteStatusChanges.get(textViewNewStatus);
                if (atomicIntegerNewStatus == null) {
                    atomicIntegerNewStatus = new AtomicInteger(1);
                    noteStatusChanges.put(textViewNewStatus, atomicIntegerNewStatus);
                } else {
                    atomicIntegerNewStatus.incrementAndGet();
                }
                AtomicInteger atomicIntegerOldStatus = noteStatusChanges.get(textViewOldStatus);
                if (atomicIntegerOldStatus == null) {
                    atomicIntegerOldStatus = new AtomicInteger(-1);
                    noteStatusChanges.put(textViewOldStatus, atomicIntegerOldStatus);
                } else {
                    atomicIntegerOldStatus.decrementAndGet();
                }
            }
        }
        // Check if we have any loan status changes
        if (noteStatusChanges.size() > 0) {
            for (TextView textView : noteStatusChanges.keySet()) {
                int diff = noteStatusChanges.get(textView).intValue();
                if (diff > 0) {
                    superScriptValue(textView, true, String.valueOf(diff), true);
                } else if (diff < 0) {
                    superScriptValue(textView, false, String.valueOf(diff), true);
                }
            }
        }
    }

    private void addNoteIdInTextViewTag(TextView textView, String noteId) {
        if (textView != null) {
            List<String> textViewTag = (List<String>) textView.getTag();
            if (textViewTag == null) {
                textViewTag = new ArrayList<>();
                textView.setTag(textViewTag);
            }
            textViewTag.add(noteId);
        }
    }

    private TextView noteStatusToTextView(String noteStatus) {
        if (NotesCategory.NOTE_CURRENT.contains(noteStatus)) return txtCurrentNotes;
        if (NotesCategory.NOTE_NOT_ISSUED.contains(noteStatus)) return txtNotYetIssuedNotes;
        if (NotesCategory.NOTE_PAID.contains(noteStatus)) return txtPaidNotes;
        if (NotesCategory.NOTE_LATE_16_30.contains(noteStatus)) return txtLateNotes16_30;
        if (NotesCategory.NOTE_LATE_31_120.contains(noteStatus)) return txtLateNotes31_120;
        if (NotesCategory.NOTE_GRACE.contains(noteStatus)) return txtGracePeriod;
        if (NotesCategory.NOTE_DEFAULT.contains(noteStatus)) return txtDefaultNotes;
        if (NotesCategory.NOTE_CHARGED_OFF.contains(noteStatus)) return txtChargedOffNotes;
        return null;
    }

    private void showTextActivity(TextView view, JSONObject delta, boolean noteView) throws  JSONException {
        int newValue = Double.valueOf(delta.getString(ActivityUtil.NEW_FIELD)).intValue();
        int oldValue = Double.valueOf(delta.getString(ActivityUtil.OLD_FIELD)).intValue();
        int diff = newValue - oldValue;
        if (diff > 0) {
            superScriptValue(view, true, String.valueOf(diff), noteView);
        } else if (diff < 0) {
            superScriptValue(view, false, String.valueOf(diff), noteView);
        }
    }

    private class NoteTouchListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                TextView textView = (TextView) v;
                Layout layout = textView.getLayout();
                int x = (int)event.getX();
                int y = (int)event.getY();
                if (layout != null){
                    // Check if we have any extra information associated
                    // with this TextView.
                    Object tag = textView.getTag();
                    List<String> noteIdsInTag = null;
                    if (tag instanceof List && ((List) tag).size() > 0) {
                        noteIdsInTag = (List<String>) tag;
                    }
                    // If we don't have any note ids which in TextView
                    // then directly send to onNoteclick which would
                    // show all notes for that status.
                    if (noteIdsInTag == null) {
                        onNoteClick(textView);
                    } else {
                        // Determine if the user have clicked on something
                        // after up/down arrow or before.
                        // If the user have clicked after then we would
                        // show only those noteIds which are part of that
                        // activity.
                        int line = layout.getLineForVertical(y);
                        int offset = layout.getOffsetForHorizontal(line, x);
                        String txt = ((TextView) v).getText().toString();
                        final String upArrow = "↑";
                        final String downArrow = "↓";
                        int indexUpArrow = txt.indexOf(upArrow);
                        int indexDownArrow = txt.indexOf(downArrow);
                        // If the user has clicked after up/down arrow
                        // then show only those note ids which are affected
                        // in that change.
                        if ((indexUpArrow > -1 && offset > indexUpArrow)
                                || (indexDownArrow > -1 && offset > indexDownArrow) ) {
                            Util.startNotesActivity(MainActivity.this, noteIdsInTag);
                        } else {
                            onNoteClick(textView);
                        }
                    }
                }
            }
            return true;
        }
    }
}
