package com.ravneetg.lcautomatique.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by khenush on 8/9/2015.
 */
public class PreferencesUtil {

    public static final String INVESTOR_ID = "investor_id";
    public static final String API_KEY = "api_key";
    private static final String WELCOME_SCREEN = "welcome_screen_shown";

    public static char[] getInvestorId(Context ctx) {
        String investorId = PreferenceManager.getDefaultSharedPreferences(ctx).getString(INVESTOR_ID, null);
        if (investorId != null) {
            return investorId.toCharArray();
        }
        return null;
    }

    public static char[] getAPIKey(Context ctx) {
        String apiKey = PreferenceManager.getDefaultSharedPreferences(ctx).getString(API_KEY, null);
        if (apiKey != null) {
            return apiKey.toCharArray();
        }
        return null;
    }

    public static boolean isWelcomeScreenShown(Context ctx) {
        boolean welcomeScreenShown = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(WELCOME_SCREEN, false);
        return welcomeScreenShown;
    }

    public static void markWelcomeScreenShown(Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean(WELCOME_SCREEN, true).apply();
    }
}
