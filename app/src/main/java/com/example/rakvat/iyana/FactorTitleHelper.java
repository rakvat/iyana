package com.example.rakvat.iyana;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.message;

public class FactorTitleHelper {

    public static final int MAX_FACTORS = 10;

    public static List<String> getFactorTitles(Activity context) {
        List<String> titles = new ArrayList<String>();
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.factor_storage_key),
                        Context.MODE_PRIVATE);
        for (int i = 0; i < MAX_FACTORS; i++) {
            titles.add(sharedPref.getString(context.getString(R.string.factor_storage_key) + i, ""));
        }
        return titles;
    }

    public static int[] getFactorTitlesDiffNumbers(Activity context, List<String> titles) {
        int newTitles = 0;
        int editedTitles = 0;
        int deletedTitles = 0;
        List<String> oldTitles = getFactorTitles(context);
        for (int i = 0; i < MAX_FACTORS; i++) {
            if (titles.get(i) != null && titles.get(i).length() > 0) {
                if (oldTitles.get(i) == null || oldTitles.get(i).length() == 0) {
                    newTitles += 1;
                } else if (!titles.get(i).equals(oldTitles.get(i))) {
                    editedTitles += 1;
                }
            } else if (oldTitles.get(i) != null && oldTitles.get(i).length() > 0) {
                deletedTitles += 1;
            }
        }
        int[] diffNumbers = { newTitles, editedTitles, deletedTitles };
        return diffNumbers;
    }

    public static void setFactorTitles(Activity context, List<String> titles) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.factor_storage_key),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int i = 0; i < MAX_FACTORS; i++) {
            if (titles.get(i) != null) {
                editor.putString(context.getString(R.string.factor_storage_key) + i, titles.get(i));
            }
        }
        editor.commit();
    }
}
