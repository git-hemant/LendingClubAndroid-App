package com.ravneetg.lcautomatique.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.db.DataManager;
import com.ravneetg.lcautomatique.request.data.response.ResponseData;
import com.ravneetg.lcautomatique.utils.PreferencesUtil;
import com.ravneetg.lcautomatique.utils.SimpleAsyncTask;
import com.ravneetg.lcautomatique.utils.Util;

public class SettingsActivity extends AbstractActionBarActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToolbar(savedInstanceState);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Check if we need to show welcome screen
        // second argument is the default to use if the preference can't be found
        boolean welcomeScreenShown = PreferencesUtil.isWelcomeScreenShown(this);

        if (!welcomeScreenShown) {
            // here you can launch another activity if you like
            // the code below will display a popup

            String title = getString(R.string.Welcome);
            String text = getString(R.string.WelcomeText);
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle(title).setMessage(text).setPositiveButton(
                    "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            PreferencesUtil.markWelcomeScreenShown(this);
        }
    }

    private void progressIndicator(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference(PreferencesUtil.API_KEY));
            bindPreferenceSummaryToValue(findPreference(PreferencesUtil.INVESTOR_ID));
            Preference btnValidate = (Preference) findPreference("btnValidate");

            btnValidate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final SettingsActivity activity = (SettingsActivity) getActivity();
                    char[] apiKey = PreferencesUtil.getAPIKey(activity);
                    char[] investorId = PreferencesUtil.getInvestorId(activity);

                    if (apiKey == null && investorId == null) {
                        MainApplication.track(activity, "Validate Data", "Empty investor ID and API Key");
                        Util.showErrorAlert(activity, "Please input Investor ID and API key and then validate.", true);
                        return false;
                    }
                    if (apiKey == null) {
                        MainApplication.track(activity, "Validate Data", "Empty API Key");
                        Util.showErrorAlert(activity, "Please input API key and then validate.", true);
                        return false;
                    }
                    if (investorId == null) {
                        MainApplication.track(activity, "Validate Data", "Empty Investor ID");
                        Util.showErrorAlert(activity, "Please input Investor ID and then validate.", true);
                        return false;
                    }

                    // First check network is available to avoid common issues.
                    if (!Util.isNetworkAvailable(getActivity(), true)) {
                        return false;
                    }
                    activity.progressIndicator(true);
                    Util.doAsyncTask(new SimpleAsyncTask() {
                        @Override
                        public void doWork() {
                            try {
                                // Request available cash for validation as it have smallest payload response.
                                ResponseData responseData = new DataManager(getActivity()).getRefreshedAccountSummaryData();
                                activity.progressIndicator(false);
                                if (Util.anyErrorInResponse(getActivity(), responseData)) {
                                } else {
                                    Runnable runner = new Runnable() {
                                        @Override
                                        public void run() {
                                            MainApplication.track(activity, "Validate Data", "Successfully validated.");
                                            //Util.showErrorAlert(getActivity(), "Successfully validated your account ID and API Key.", true);
                                            Util.startMainActivity(activity, Boolean.TRUE.toString());
                                        }
                                    };
                                    activity.runOnUiThread(runner);
                                }
                            } catch (Exception e) {
                                Util.showErrorAlert(getActivity(), e.getMessage(), true);
                                MainApplication.reportCaughtException(e);
                            } finally {
                                // In-case of exception ensure progress indicator is off.
                                activity.progressIndicator(false);
                            }
                        }
                    });

                    return true;
                }
            });
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                String stringValue = value.toString();

                if (preference instanceof ListPreference) {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    // Set the summary to reflect the new value.
                    preference
                            .setSummary(index >= 0 ? listPreference.getEntries()[index]
                                    : null);

                } else {
                    // For all other preferences, set the summary to the value's
                    // simple string representation.
                    preference.setSummary(stringValue);
                }
                return true;
            }
        };

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see #sBindPreferenceSummaryToValueListener
         */
        private static void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference
                    .setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager.getDefaultSharedPreferences(
                            preference.getContext()).getString(preference.getKey(),
                            ""));
        }
    }

}
