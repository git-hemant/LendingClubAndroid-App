package com.ravneetg.lcautomatique.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;

public class AboutActivity extends AbstractActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initToolbar(savedInstanceState);
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
            addPreferencesFromResource(R.xml.fragment_about);

            String packageName = MainApplication.getAppContext().getPackageName();
            String version = "";
            try {
                version = MainApplication.getAppContext().getPackageManager().getPackageInfo(packageName, 0).versionName;
            } catch (Exception e) {

            }
            Preference versionSummary = (Preference) findPreference("version");
            versionSummary.setSummary(version);

        }
    }
}
