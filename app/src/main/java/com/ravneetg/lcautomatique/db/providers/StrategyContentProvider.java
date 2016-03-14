package com.ravneetg.lcautomatique.db.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.ravneetg.lcautomatique.db.DBHelper;

/**
 * Created by Ravneet on 1/24/2015.
 */
public class StrategyContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "lcautomatique.strategy";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/strategies");
    private static final UriMatcher uriMatcher ;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"strategies",1);
    }
    @Override
    public boolean onCreate() {

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(uriMatcher.match(uri) == 1){
            return DBHelper.getInstance(getContext()).getLocalStrategies();
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
