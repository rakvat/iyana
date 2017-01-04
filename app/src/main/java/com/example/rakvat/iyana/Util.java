package com.example.rakvat.iyana;

import android.support.v7.app.AppCompatActivity;

public class Util {
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void setTitleBar(AppCompatActivity context, int string_id) {
        context.getSupportActionBar().
                setTitle(context.getString(R.string.app_name) + ": " + context.getString(string_id));
    }
}
