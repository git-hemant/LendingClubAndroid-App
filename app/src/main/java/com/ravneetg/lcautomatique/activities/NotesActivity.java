package com.ravneetg.lcautomatique.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.db.DBHelper;
import com.ravneetg.lcautomatique.db.DataManager;
import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;
import com.ravneetg.lcautomatique.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Hemant on 6/25/2015.
 */
public class NotesActivity extends AbstractActionBarActivity {

    public static final String INPUT_EXTRA = "notes_where_clause";
    private ListView mNotesList;
    private SimpleCursorAdapter mCursorAdapter = null;
    private PopupWindow notePopupWindow;
    private View notePopupWindowView;
    private Map<String, Integer> popupWindowValues;
    private boolean showAllNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This would be null if we need to show all notes.
        String notesWhereClause;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            notesWhereClause = null;
            showAllNotes = true;
        } else {
            notesWhereClause = extras.getString(INPUT_EXTRA);
        }
        setContentView(R.layout.activity_notes);
        initToolbar(savedInstanceState);

        mNotesList = (ListView) findViewById(R.id.notesList);
        mNotesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idIndex = mCursorAdapter.getCursor().getColumnIndex(NoteOwnedResponseData.NOTE_ID);
                String noteId = mCursorAdapter.getCursor().getString(idIndex);
                if (notePopupWindow == null) {
                    initPopupWindow();
                }
                updatePopupValues(noteId);
                notePopupWindow.showAtLocation(NotesActivity.this.getCurrentFocus(), Gravity.CENTER, 10, 10);
            }
        });

        Cursor cursor = DBHelper.getInstance(getBaseContext()).getAllNotes(notesWhereClause);
        mCursorAdapter = new SimpleCursorAdapter(NotesActivity.this,
                R.layout.activity_notes_row,
                cursor,
                new String[]{NoteOwnedResponseData.SUB_GRADE, NoteOwnedResponseData.NOTE_ID, NoteOwnedResponseData.NOTE_AMOUNT, NoteOwnedResponseData.PAYMENTS_RECEIVED, NoteOwnedResponseData.LOAN_STATUS, NoteOwnedResponseData.ORDER_DATE},
                new int[]{R.id.subGrade, R.id.noteId, R.id.noteAmount, R.id.paymentReceived, R.id.loanStatus, R.id.CREATED_DATE},
                0);
        mCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                if (aColumnIndex == 4) {
                    String paymentReceived = aCursor.getString(aColumnIndex);
                    if (paymentReceived != null && paymentReceived.length() > 1) {
                        ((TextView) aView).setText(Util.formatDouble(paymentReceived));
                        return true;
                    }
                }
                // Format ORDER_DATE
                else if (aColumnIndex == 6) {
                    try {
                        String createDate = aCursor.getString(aColumnIndex);
                        TextView textView = (TextView) aView;
                        if (createDate != null && createDate.length() > 0) {
                            textView.setText(Util.formatDate(createDate, false));
                            return true;
                        }
                    } catch (Exception e) {
                        MainApplication.reportCaughtException(e);
                    }
                }

                return false;
            }
        });
        mNotesList.setAdapter(mCursorAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCursorAdapter != null) {
            try {
                mCursorAdapter.getCursor().close();
            } catch (Exception e) {}
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CharSequence title = item.getTitle();
        String notesWhereClause = " WHERE " + NoteOwnedResponseData.LOAN_STATUS +
                "='" + title + "'";
        Cursor cursor = DBHelper.getInstance(getBaseContext()).getAllNotes(notesWhereClause);
        // It should never happen as we are populating only those menu items
        // for which we have notes.
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }
        mCursorAdapter.changeCursor(cursor);
        mCursorAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean returnValue = super.onCreateOptionsMenu(menu);
        if (showAllNotes) {
            List<String> status = new DataManager(this).getNoteStatuses();
            menu.addSubMenu("All");
            for (String st : status) menu.addSubMenu(st);
        }
        return returnValue;
    }

    private void initPopupWindow() {
        notePopupWindow = new PopupWindow(NotesActivity.this);
        notePopupWindowView = LayoutInflater.from(NotesActivity.this.getBaseContext()).inflate(R.layout.popup_note, null);
        notePopupWindowView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = notePopupWindowView.getMeasuredHeight();
        int width = notePopupWindowView.getMeasuredWidth();
        notePopupWindow.setHeight(height);
        notePopupWindow.setWidth(width);
        notePopupWindow.setContentView(notePopupWindowView);
        // Closes the popup window when touch outside of it - when looses focus
        notePopupWindow.setOutsideTouchable(true);
        notePopupWindow.setFocusable(true);
        // Removes default black background
        //notePopupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindowValues = new HashMap<>();
        popupWindowValues.put(NoteOwnedResponseData.NOTE_ID, R.id.noteId);
        popupWindowValues.put(NoteOwnedResponseData.LOAN_ID, R.id.loanId);
        popupWindowValues.put(NoteOwnedResponseData.LOAN_AMOUNT, R.id.loanAmount);
        popupWindowValues.put(NoteOwnedResponseData.SUB_GRADE, R.id.subGrade);
        popupWindowValues.put(NoteOwnedResponseData.LOAN_STATUS, R.id.loanStatus);
        popupWindowValues.put(NoteOwnedResponseData.PAYMENTS_RECEIVED, R.id.paymentReceived);
        popupWindowValues.put(NoteOwnedResponseData.ORDER_DATE, R.id.orderDate);
        popupWindowValues.put(NoteOwnedResponseData.LOAN_STATUS_DATE, R.id.loanStatusDate);
    }

    private void updatePopupValues(String noteId) {
        JSONObject noteData = DBHelper.getInstance(getBaseContext()).getNoteById(noteId);
        Iterator<String> keys = popupWindowValues.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (noteData.has(key)) {
                TextView textView = (TextView) notePopupWindowView.findViewById(popupWindowValues.get(key));
                try {
                    String value = noteData.getString(key);

                    if (value != null) {
                        // Format some special fields
                        if (NoteOwnedResponseData.PAYMENTS_RECEIVED.equals(key)) {
                            value = Util.formatDouble(value);
                        } else if (NoteOwnedResponseData.LOAN_STATUS_DATE.equals(key) || NoteOwnedResponseData.ORDER_DATE.equals(key)) {
                            value = Util.formatDate(value);
                        }
                        // End format.

                        if (NoteOwnedResponseData.LOAN_ID.equals(key)) {
                            value = "<a href='https://www.lendingclub.com/account/loanDetail.action?loan_id=" + value + "' >" + value + "</a>";
                            textView.setText(Html.fromHtml(value));
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            textView.setLinksClickable(true);
                            textView.setFocusable(true);
                            textView.setClickable(true);
                            textView.setLongClickable(true);
                        } else {
                            textView.setText(value);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(MainApplication.TAG, "Exception during populating note popupup window.", e);
                }
            }
        }
    }
}
