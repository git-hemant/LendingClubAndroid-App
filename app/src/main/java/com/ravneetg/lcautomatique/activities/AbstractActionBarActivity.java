package com.ravneetg.lcautomatique.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;

/**
 * Created by khenush on 7/9/2015.
 */
public abstract class AbstractActionBarActivity extends ActionBarActivity {

    protected final void initToolbar(final Bundle savedInstanceState) {
        Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbstractActionBarActivity.this.finish();
            }
        });
    }

    @Override
    // This method is invoked right before activity is going to be visible.
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}
