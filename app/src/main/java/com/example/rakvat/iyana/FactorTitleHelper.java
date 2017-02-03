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

    public static ArrayList<ArrayList<String>> getEditedAndDeletedFactors(Activity context, List<String> titles) {
        ArrayList<ArrayList<String>> editedAndDeleted = new ArrayList<ArrayList<String>>();
        ArrayList<String> editedFactors = new ArrayList<String>();
        ArrayList<String> deletedFactors = new ArrayList<String>();
        List<String> oldTitles = getFactorTitles(context);
        for (int i = 0; i < MAX_FACTORS; i++) {
            if (titles.get(i) != null && titles.get(i).length() > 0) {
                if (oldTitles.get(i) == null || oldTitles.get(i).length() == 0) {
                    // new titles
                } else if (!titles.get(i).equals(oldTitles.get(i))) {
                    editedFactors.add(DatabaseContract.MoodEntry.FACTOR_COLUMNS[i]);
                }
            } else if (oldTitles.get(i) != null && oldTitles.get(i).length() > 0) {
                deletedFactors.add(DatabaseContract.MoodEntry.FACTOR_COLUMNS[i]);
            }
        }
        editedAndDeleted.add(editedFactors);
        editedAndDeleted.add(deletedFactors);
        return editedAndDeleted;
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
