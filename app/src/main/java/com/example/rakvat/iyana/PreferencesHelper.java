package com.example.rakvat.iyana;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHelper {

    public static final boolean getAfterInstall(Context context) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.preferences),
                        Context.MODE_PRIVATE);
        boolean startedBefore =
                sharedPref.getBoolean(context.getString(R.string.started_before_key), false);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.started_before_key), true);
        editor.commit();
        return startedBefore;
    }

    public static final boolean getDemoMode(Context context) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.preferences),
                        Context.MODE_PRIVATE);
        return sharedPref.getBoolean(context.getString(R.string.demo_mode_key), false);
    }

    public static final void setDemoMode(Context context, boolean flag) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.preferences),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.demo_mode_key), flag);
        editor.commit();
    }
}
