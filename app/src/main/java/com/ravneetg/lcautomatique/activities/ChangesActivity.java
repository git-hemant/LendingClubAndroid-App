package com.ravneetg.lcautomatique.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.adapters.ChangesViewAdapter;
import com.ravneetg.lcautomatique.data.ActivityOverPeriod;
import com.ravneetg.lcautomatique.db.DBHelper;
import com.ravneetg.lcautomatique.db.DataManager;
import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;
import com.ravneetg.lcautomatique.utils.ActivityUtil;
import com.ravneetg.lcautomatique.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChangesActivity extends AbstractActionBarActivity {
    private static final int DAY1 = 1;
    private static final int DAYS7 = 7;
    private static final int DAYS30 = 30;
    private static final int DAYS90 = 90;
    private int mSelectedDays = DAYS7;
    private ListView mChangesList;
    private ChangesViewAdapter mAdapter = null;
    private DataManager mDataManager;
    private TextView mWarningTextView;
    private View mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changes);
        initToolbar(savedInstanceState);
        mDataManager = new DataManager(this);
        mChangesList = (ListView) findViewById(R.id.changesList);
        mChangesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChangesViewAdapter.ChangeUIItem item = mAdapter.getItem(position);
                if (item.getNoteIds() != null) {
                    showNotesActivity(item.getNoteIds());
                }
            }
        });
        mWarningTextView = (TextView) findViewById(R.id.warningMessage);
        mCardView = findViewById(R.id.card_view);
        setListAdapter();
        // Remove the horizontal line which is shown between each list item.
        mChangesList.setDivider(null);
        mChangesList.setDividerHeight(0);
    }

    private void setListAdapter() {
        ActivityOverPeriod activityOverPeriod = ActivityUtil.getActivityOver(DBHelper.getInstance(this), mSelectedDays);
        mAdapter = createAdapter(activityOverPeriod);
        mChangesList.setAdapter(mAdapter);
        setTitle(getTitleForDays(mSelectedDays));
        // Show warning if we don't have any data or data available is not enough for given days.
        showWarningIfNeeded(activityOverPeriod);
    }

    private void showWarningIfNeeded(ActivityOverPeriod aop) {
        // No data available.
        String warningMessage = null;
        // If no data is available
        String day = getString(R.string.day);
        String days = getString(R.string.days);
        if (mAdapter.getCount() == 0) {
            mCardView.setVisibility(View.GONE);
            // If data is not available because we don't have any data at all in the table
            if (aop.getOverallOldestActivityDate() == null) {
                warningMessage = getString(R.string.ActivityNoData);
            } else {
                // We have some data available but maybe user need to widen the search to see results.
                warningMessage = getString(R.string.ActivityNoDataForGivenRange, mSelectedDays, mSelectedDays == 1 ? day : days);
            }
        } else {
            // We have activity data available but not enough data available.
            mCardView.setVisibility(View.VISIBLE);
            try {
                long activeDateDaysDiff = Util.daysDifference(aop.getNewestActivityDate(), aop.getOldestActivityDate());
                if (activeDateDaysDiff < mSelectedDays && aop.getOldestActivityDate().equals(aop.getOverallOldestActivityDate())) {
                    warningMessage = getString(R.string.ActivityDataNotEnoughForSelectedRange, activeDateDaysDiff, activeDateDaysDiff == 1 ? day : days);
                }
            } catch (Exception e) {
                Log.e(MainApplication.TAG, e.getMessage(), e);
                MainApplication.reportCaughtException(e);
            }
        }

        // Hide the text view if we don't have any warning available.
        if (warningMessage == null) {
            mWarningTextView.setVisibility(View.GONE);
        } else {
            mWarningTextView.setText(warningMessage);
            mWarningTextView.setVisibility(View.VISIBLE);
        }
    }

    private ChangesViewAdapter createAdapter( ActivityOverPeriod activityOverPeriod) {
        ArrayList<ChangesViewAdapter.ChangeUIItem> changeItems = getChangeItems(activityOverPeriod);
        ChangesViewAdapter viewAdapter = new ChangesViewAdapter(this, getChangeItems(activityOverPeriod));
        return viewAdapter;
    }

    private ArrayList<ChangesViewAdapter.ChangeUIItem> getChangeItems(ActivityOverPeriod activityOverPeriod) {
        ArrayList<ChangesViewAdapter.ChangeUIItem> changeItems = new ArrayList<>();
        if (activityOverPeriod != null && !activityOverPeriod.isEmpty()) {
            String lbl, txt;
            ChangesViewAdapter.ChangeUIItem changeUIItem;
            if (activityOverPeriod.getChangeAccountTotal() != 0) {
                // Add first change item.
                lbl = "Account total";
                // TODO - show this in green or red font based on number is greater than zero or less than zero
                txt = Util.formatDouble("" + activityOverPeriod.getChangeAccountTotal());
                changeUIItem = new ChangesViewAdapter.ChangeUIItem(lbl, txt);
                changeUIItem.setIsAmount(true);
                changeItems.add(changeUIItem);
            }
            if (activityOverPeriod.getChangePaymentsReceived() != 0) {
                lbl = "Payments received";
                txt = Util.formatDouble("" + activityOverPeriod.getChangePaymentsReceived());
                changeUIItem = new ChangesViewAdapter.ChangeUIItem(lbl, txt);
                changeUIItem.setIsAmount(true);
                changeItems.add(changeUIItem);
            }

            if (activityOverPeriod.getNewNotes().size() > 0) {
                lbl = "New Notes";
                txt = "" + activityOverPeriod.getNewNotes().size();
                changeUIItem = new ChangesViewAdapter.ChangeUIItem(lbl, txt);
                changeUIItem.setShowHyperLink(true);
                changeUIItem.setNoteIds(activityOverPeriod.getNewNotes());
                changeItems.add(changeUIItem);
            }

            Map<String, List<String>> noteStatusByStatus = activityOverPeriod.getNoteStatusByStatus();
            for (Iterator<String> iterator = noteStatusByStatus.keySet().iterator(); iterator.hasNext();) {
                String status = iterator.next();
                lbl = status;
                List<String> noteIds = noteStatusByStatus.get(status);
                txt = String.valueOf(noteIds.size());
                changeUIItem = new ChangesViewAdapter.ChangeUIItem(lbl, txt);
                changeUIItem.setShowHyperLink(true);
                changeUIItem.setNoteIds(noteIds);
                changeItems.add(changeUIItem);
            }
        }
        return changeItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_changes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int days = -1;
        if (id == R.id.day1) {
            days = DAY1;
        } else if (id == R.id.day7) {
            days = DAYS7;
        } else if (id == R.id.day30) {
            days = DAYS30;
        } else if (id == R.id.day90) {
            days = DAYS90;
        }
        if (days == -1) return false;
        // We already have selected days shown.
        if (days == mSelectedDays) return true;
        mSelectedDays = days;
        setListAdapter();
        return true;
    }

    // On click event for the list when user click on anywhere
    // in the card view.
    public void onClick(View v) {
        if (v instanceof TextView && v.getTag() instanceof List) {
            showNotesActivity((List<String>) v.getTag());
        }
    }

    private void showNotesActivity(List<String> noteIds) {
        if (noteIds == null || noteIds.size() == 0) return;
        Util.startNotesActivity(ChangesActivity.this, noteIds);
    }

    private String getTitleForDays(int days) {
        StringBuilder title = new StringBuilder(getResources().getString(R.string.title_activity_changes) + " - ");
        if (days == DAY1) {
            title.append(getResources().getString(R.string.changes_since_yesterday));
        } else if (days == DAYS7) {
            title.append(getResources().getString(R.string.changes_last_7_days));
        } else if (days == DAYS30) {
            title.append(getResources().getString(R.string.changes_last_30_days));
        } else if (days == DAYS90) {
            title.append(getResources().getString(R.string.changes_last_90_days));
        }
        return title.toString();
    }
}
