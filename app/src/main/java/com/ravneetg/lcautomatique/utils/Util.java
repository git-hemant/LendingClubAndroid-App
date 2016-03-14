package com.ravneetg.lcautomatique.utils;

/**
 * Created by Ravneet on 1/16/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;

import com.ravneetg.lcautomatique.MainActivity;
import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.activities.NotesActivity;
import com.ravneetg.lcautomatique.activities.SettingsActivity;
import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.ResponseData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Util {

    // This is the date format in which dates are coming from LC APIs and stored in SQLITE.
    public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final SimpleDateFormat SDF_ISO_8601 = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
    public final static int COL_ID = 0;
    public final static int COL_TYPE = 1;
    public final static int COL_VALUE = 2;
    public final static int TYPE_UNKNOWN = 0;
    public final static int TYPE_BIG_HEADER = 1;
    public final static int TYPE_HEADER = 2;
    public final static int TYPE_ITEM = 3;
    public static final int ITEM_HOME = 2;
    public static final int ITEM_NOTES = 3;
    public static final int ITEM_CHANGES = 4;
    public static final int ITEM_SETTINGS = 5;
    public static final int ITEM_ABOUT = 6;
    static String[] DaysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
            "Sat"};
    static String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static boolean isNetworkAvailable(Activity activity, boolean showErrorMessage) {
        boolean isNetworkAvailable = isNetworkAvailable(activity);
        if (!isNetworkAvailable && showErrorMessage) {
            showErrorAlert(activity, "Network connection not found.", true);
        }
        return isNetworkAvailable;
    }

    /**
     * Return difference in date in given 2 dates.
     * It assumes date format is UTC yyyy-mm-dd hh:mm:ss which is how
     * default Android stored date in sqlite.
     */
    public static long daysDifference(String newDate, String oldDate) throws ParseException {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date dOld = simpleDateFormat.parse(oldDate);
        Date dNew = simpleDateFormat.parse(newDate);
        long different = dNew.getTime() - dOld.getTime();
        return TimeUnit.DAYS.convert(different, TimeUnit.MILLISECONDS);
    }

    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean anyErrorInResponse(Activity activity, ResponseData responseData) {
        if (responseData.getResponseCode() == -1 && responseData.getException() != null) {
            Throwable exception = responseData.getException();
            if (exception.getCause() != null) {
                exception = exception.getCause();
            }

            MainApplication.track(activity, "LC Request", "Failed with " + exception.getLocalizedMessage());

            // Show the message from exception and send Google analytics event.
            // as we have already tested network connectivity.
            Util.showErrorAlert(activity, exception.getLocalizedMessage(), true);
            return true;
        }
        if (responseData.getResponseCode() == 401) {
            MainApplication.track(activity, "LC Request", "Failed with 401.");
            Util.showErrorAlert(activity, "Authentication failed with Lending Club.\nPlease validate your Account ID and API Key (case sensitive) is correct.", false);
            return true;
        } else if (responseData.getResponseCode() != 200) {
            MainApplication.track(activity, "LC Request", "Failed with " + responseData.getResponseCode());
            Util.showErrorAlert(activity, "Failed to get valid data from Lending Club", true);
            return true;
        }
        return false;
    }

    public static void showErrorAlert(final Activity activity, String message, boolean showOkButton) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(message);
        if (!showOkButton) {
            builder1.setCancelable(true);
        }
        builder1.setPositiveButton(!showOkButton ? "Cancel" : "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        if (!showOkButton)
            builder1.setNegativeButton("Open Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent settingsScreen = new Intent(activity, SettingsActivity.class);
                            activity.startActivity(settingsScreen);
                            dialog.cancel();
                        }
                    });

        Runnable runner = new Runnable() {
            @Override
            public void run() {
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        };
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            // We are in UI thread so directly call run
            runner.run();
        }
        activity.runOnUiThread(runner);
    }

    public static void doAsyncTask(SimpleAsyncTask task) {
        new UtilAsyncTask().execute(task);
    }

    public static String hyperlinkHtml(Object value) {
        return "<font size='10' color='blue'><u>" + value + "</u></font>";
    }

    public static void startNotesActivity(Activity sourceActivity, List<String> noteIds) {
        // Show these specific note id in newly launch note activity.
        StringBuilder where_clause = new StringBuilder(" WHERE " + NoteOwnedResponseData.NOTE_ID + " in (");

        int i = 0;
        for (String noteId : noteIds) {
            if (i > 0) {
                where_clause.append(",");
            }
            where_clause.append("'").append(noteId).append("'");
            i++;
        }
        where_clause.append(")");
        Util.startNotesActivity(sourceActivity, where_clause.toString());
    }

    public static void startNotesActivity(Activity sourceActivity, String where_clause) {
        Intent notes = new Intent(sourceActivity, NotesActivity.class);
        if (where_clause != null && where_clause.length() > 0) {
            notes.putExtra(NotesActivity.INPUT_EXTRA, where_clause);
        }
        sourceActivity.startActivity(notes);
    }

    public static void startMainActivity(Activity sourceActivity, String inputExtra) {
        Intent notes = new Intent(sourceActivity, MainActivity.class);
        if (inputExtra != null && inputExtra.length() > 0) {
            notes.putExtra(MainActivity.INPUT_EXTRA, inputExtra);
        }
        sourceActivity.startActivity(notes);
    }

    public static boolean IsDateToday(String strDate) {
        TimeZone tz = TimeZone.getDefault();
        SDF_ISO_8601.setTimeZone(tz);
        Date TodaysDate = new Date();

        Calendar cNow = new GregorianCalendar(Locale.getDefault());
        cNow.setTimeZone(tz);
        cNow.setTime(TodaysDate);

        try {
            Date dStored = SDF_ISO_8601.parse(strDate); // database datetime is in GMT
            Calendar cStored = new GregorianCalendar(Locale.getDefault());
            cStored.setTimeZone(tz);

            cStored.setTime(dStored);

            if (cNow.get(Calendar.DATE) == cStored.get(Calendar.DATE)
                    && cNow.get(Calendar.MONTH) == cStored.get(Calendar.MONTH)
                    && cNow.get(Calendar.YEAR) == cStored.get(Calendar.YEAR)) {
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    public static boolean IsDateYesterday(String strDate) {
        TimeZone tz = TimeZone.getDefault();
        SDF_ISO_8601.setTimeZone(tz);
        Date TodaysDate = new Date();

        Calendar cNow = new GregorianCalendar(Locale.getDefault());
        cNow.setTimeZone(tz);
        cNow.setTime(TodaysDate);

        try {
            Date dStored = SDF_ISO_8601.parse(strDate);//DB date time is in GMT.
            Calendar cStored = new GregorianCalendar(Locale.getDefault());
            cStored.setTimeZone(tz);
            cStored.setTime(dStored);
            boolean sameYear = cStored.get(Calendar.YEAR) == cNow.get(Calendar.YEAR);
            if (sameYear && cStored.get(Calendar.DAY_OF_YEAR) + 1 == cNow.get(Calendar.DAY_OF_YEAR) ) {
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean IsDateWithinThisWeek(String strDate) {
        TimeZone tz = TimeZone.getDefault();

        SDF_ISO_8601.setTimeZone(tz);
        Date TodaysDate = new Date();

        Calendar cNow = new GregorianCalendar(Locale.getDefault());
        cNow.setTimeZone(tz);
        cNow.setTime(TodaysDate);

        try {
            Date dStored = SDF_ISO_8601.parse(strDate); // database datetime is
            // in GMT
            Calendar cStored = new GregorianCalendar(Locale.getDefault());
            cStored.setTimeZone(tz);
            cStored.setTime(dStored);
            boolean sameYear = cStored.get(Calendar.YEAR) == cNow.get(Calendar.YEAR);
            if (sameYear && cNow.get(Calendar.WEEK_OF_YEAR) == cStored
                    .get(Calendar.WEEK_OF_YEAR))
                return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    // This API assume source date format is SDF_ISO_8601
    public static String formatDate(String strDate) {
        return formatDate(strDate, false);
    }

    public static String formatDouble(double d) {
        return formatDouble(String.valueOf(d));
    }
    public static String formatDouble(String str) {
        try {
            double d = Double.parseDouble(str);
            DecimalFormat formatter = new DecimalFormat("#,###,###.##");
            str = formatter.format(d);
        } catch (Exception e) {
            // Ignore it.
        }
        return str;
    }

    /**
     * Follow this: If the date matches today's date, return Today at xx:xx xM
     * OR XX hours ago If the date is 1 less than todays date, return YEsterday
     * at xx:xx XM if the date is in past 1 week but before yesterday, return
     * MON xx:xx XM beyond this just return Month/Date xx:xx XM
     * This API assume source date format is SDF_ISO_8601
     *
     * @param strDate
     * @param TimeOnly
     * @return
     */
    public static String formatDate(String strDate, boolean TimeOnly) {
        Date date = new Date();
        String finalDate = "";
        try {
            date = SDF_ISO_8601.parse(strDate);
            Calendar c = new GregorianCalendar(Locale.getDefault());
            TimeZone tzGMT = TimeZone.getTimeZone("GMT");
            // c.setTimeZone(tzGMT);
            c.setTimeInMillis(date.getTime());

            if (IsDateToday(strDate)) {
                finalDate = String.format(
                        "Today at %02d:%02d %s",
                        c.get(Calendar.HOUR_OF_DAY) > 12 ? c
                                .get(Calendar.HOUR_OF_DAY) % 12 : c
                                .get(Calendar.HOUR_OF_DAY), c
                                .get(Calendar.MINUTE),
                        c.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
            } else if (IsDateYesterday(strDate)) {
                finalDate = String.format(
                        "Yesterday at %02d:%02d%s",
                        c.get(Calendar.HOUR_OF_DAY) > 12 ? c
                                .get(Calendar.HOUR_OF_DAY) % 12 : c
                                .get(Calendar.HOUR_OF_DAY), c
                                .get(Calendar.MINUTE),
                        c.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
            } else if (IsDateWithinThisWeek(strDate)) {
                finalDate = String.format(
                        "%s %02d:%02d %s",
                        DaysOfWeek[c.get(Calendar.DAY_OF_WEEK) - 1],
                        c.get(Calendar.HOUR_OF_DAY) > 12 ? c
                                .get(Calendar.HOUR_OF_DAY) % 12 : c
                                .get(Calendar.HOUR_OF_DAY), c
                                .get(Calendar.MINUTE),
                        c.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
            } else {
                finalDate = String.format(
                        "%s %s %s at %02d:%02d %s",
                        Months[c.get(Calendar.MONTH)],
                        c.get(Calendar.DATE),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.HOUR_OF_DAY) > 12 ? c
                                .get(Calendar.HOUR_OF_DAY) % 12 : c
                                .get(Calendar.HOUR_OF_DAY), c
                                .get(Calendar.MINUTE),
                        c.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return finalDate;

    }

    public static Cursor getBaseDrawerCursor(Activity activity) {
        MatrixCursor drawerCursor = new MatrixCursor(new String[]{"_id", "TYPE", "VALUE"});
        drawerCursor.newRow().add(1).add(TYPE_BIG_HEADER).add("BigHeader");
        drawerCursor.newRow().add(ITEM_HOME).add(TYPE_ITEM).add(activity.getString(R.string.home));
        drawerCursor.newRow().add(ITEM_NOTES).add(TYPE_ITEM).add(activity.getString(R.string.title_activity_notes));
        drawerCursor.newRow().add(ITEM_CHANGES).add(TYPE_ITEM).add(activity.getString(R.string.title_activity_changes));
        drawerCursor.newRow().add(ITEM_SETTINGS).add(TYPE_ITEM).add(activity.getString(R.string.title_activity_settings));
        drawerCursor.newRow().add(ITEM_ABOUT).add(TYPE_ITEM).add(activity.getString(R.string.title_activity_about));
        return drawerCursor;
    }

    private static class UtilAsyncTask extends AsyncTask<SimpleAsyncTask, Void, Void> {
        @Override
        protected Void doInBackground(SimpleAsyncTask... params) {
            params[0].doWork();
            return null;
        }
    }
}

