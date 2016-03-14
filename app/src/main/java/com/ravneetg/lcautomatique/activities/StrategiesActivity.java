package com.ravneetg.lcautomatique.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.db.DBContract;
import com.ravneetg.lcautomatique.db.providers.StrategyContentProvider;

public class StrategiesActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FloatingActionsMenu mFloatingActionsMenu;
    private ListView mLstStrategies;
    private SimpleCursorAdapter mCursorAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrategiesActivity.this.finish();
            }
        });

        mFloatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        mLstStrategies = (ListView) findViewById(R.id.lstStrategies);

        mCursorAdapter = new SimpleCursorAdapter(StrategiesActivity.this,
                R.layout.row_strategy,
                null,
                new String[]{DBContract.COLUMN_STRATEGY_NAME, DBContract.COLUMN_NAME_CREATED_DATE},
                new int[]{R.id.name, R.id.CREATED_DATE},
                0);
        mLstStrategies.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(0, null, this);

        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_new_strategy);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingActionsMenu.collapse();
                Intent newStrategy = new Intent(StrategiesActivity.this, NewStrategyActivity.class);
                startActivityForResult(newStrategy, MainApplication.LC_ACTIVITY_START_NEW_STRATEGY);
            }
        });

        mLstStrategies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idIndex = mCursorAdapter.getCursor().getColumnIndex("NAME");
                String strategyName = mCursorAdapter.getCursor().getString(idIndex);
                Toast.makeText(StrategiesActivity.this, "Selected item: " + position + " strategy: " + strategyName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case(MainApplication.LC_ACTIVITY_START_NEW_STRATEGY) : {
                if(resultCode == Activity.RESULT_OK){
                    //refresh the list
                    getSupportLoaderManager().restartLoader(0, null, this);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_strategies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = StrategyContentProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null){
            int count = data.getCount();
            mCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mCursorAdapter.swapCursor(null);
    }
}
