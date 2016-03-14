package com.ravneetg.lcautomatique.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.ravneetg.lcautomatique.MainApplication;

/**
 * Created by Ravneet on 8/8/2015.
 */
public class SendFeedback extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Feedback from LCAutomatique";
        String body =
                "\n\n\n\nSystem information (Please delete following information if you don't want to share it: \n\n" + Build.DEVICE +
                        Build.BRAND + " " + Build.DEVICE + " " + Build.DISPLAY + " SDK: " + Build.VERSION.SDK_INT;

        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"hs2501@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        finish();
        startActivity(Intent.createChooser(sendIntent, "App Feedback"));
        super.onCreate(savedInstanceState);
    }
}
