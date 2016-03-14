package com.ravneetg.lcautomatique;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

public class MainApplication extends Application {
    private static Context context;
    /**
     * The Analytics singleton. The field is set in onCreate method override when the application
     * class is initially created.
     */
    private static GoogleAnalytics analytics;

    /**
     * The default app getTracker. The field is from onCreate callback when the application is
     * initially created.
     */
    private static Tracker tracker;

    /**
     * Access to the global Analytics singleton. If this method returns null you forgot to either
     * set android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.analytics field in onCreate method override.
     */
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    /**
     * The default app getTracker. If this method returns null you forgot to either set
     * android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.getTracker field in onCreate method override.
     */
    public static Tracker getTracker() {
        return tracker;
    }

    public static final String PREFERENCE_FILE_NAME = "lcautomatique.xml";
    public static boolean _HasPremium_sku = false;
    public static String TAG = "LCAutomatique";

    //Activity result stuff
    public static final int LC_ACTIVITY_START_NEW_STRATEGY = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
        initAnalytics();
    }

    private void initAnalytics() {
        analytics = GoogleAnalytics.getInstance(this);

        tracker = analytics.newTracker("UA-38489799-2");

        // Provide unhandled exceptions reports. Do that first after creating the getTracker
        tracker.enableExceptionReporting(true);

        // Enable Remarketing, Demographics & Interests reports
        // https://developers.google.com/analytics/devguides/collection/android/display-features
        tracker.enableAdvertisingIdCollection(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);
    }

    public static void reportCaughtException(Throwable e) {
        if (tracker == null) return;
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(
                        new StandardExceptionParser(getAppContext(), null)
                                .getDescription(Thread.currentThread().getName(), e))
                .setFatal(false)
                .build());
    }

    public static void track(Activity activity, String action, String label) {
        MainApplication.getTracker().send(new HitBuilders.EventBuilder(activity.getLocalClassName(), action)
                .setLabel(label)
                .build());

    }

    public static Context getAppContext() {
        return MainApplication.context;
    }
}
