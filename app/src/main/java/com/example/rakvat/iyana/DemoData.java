package com.example.rakvat.iyana;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;


public class DemoData {

    private static final String[] titleNames = {
            "Weather",
            "Sleep",
            "Sport",
            "Food",
            "Work",
            "Social",
            "Body",
            "Fun",
            "Meditation",
            "Menstruation",
    };

    private static final String[] notes = { "sick :(", "I hate work", "met friends", "back hurting", "the world sucks", "we need a new society"};

    private static long DAY_IN_MS = 1000 * 60 * 60 * 24;


    public static final void initialize(Context context) {
        FactorTitleHelper.setFactorTitles(context, Arrays.asList(titleNames));

        DatabaseHelper dbHelper = new DatabaseHelper(context, Util.getCurrentDBName(context));
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Random randomGenerator = new Random();

        for (int i = 0; i < 5; i++) { // weeks
            for (int j = 0; j < 7; j++) { // days
                ContentValues values = new ContentValues();
                Date date = new Date(System.currentTimeMillis() - ((i + 1) * (j + 1) * DAY_IN_MS));
                int randomOffset = randomGenerator.nextInt(1000 * 60 * 4);
                values.put(DatabaseContract.MoodEntry.COLUMN_NAME_DATE, date.getTime() + randomOffset);

                for (int k = 0; k < DatabaseContract.MoodEntry.FACTOR_COLUMNS.length; k++) {
                     if (k == 9) {
                         if (i == 2 && j == 3) {
                             storeValue(1, DatabaseContract.MoodEntry.FACTOR_COLUMNS[k], values);
                         }
                     } else if (k == 8 || k == 2) {
                         int value = randomGenerator.nextInt((k == 2 ? 3 : 10) * 4);
                         storeValue(value, DatabaseContract.MoodEntry.FACTOR_COLUMNS[k], values);
                     } else {
                         int value = (int)(randomGenerator.nextGaussian() * 6);
                         storeValue(value, DatabaseContract.MoodEntry.FACTOR_COLUMNS[k], values);
                    }
                }
                storeValue(getMood(values), DatabaseContract.MoodEntry.COLUMN_NAME_MOOD, values);
                storeText(randomNote(values), DatabaseContract.MoodEntry.COLUMN_NAME_NOTE, values);
                db.insert(DatabaseContract.MoodEntry.TABLE_NAME, null, values);
            }
        }
    }

    private static int getMood(ContentValues values) {
        return 3;
    }

    private static String randomNote(ContentValues values) {
        Random random = new Random();
        if (random.nextFloat() > 0.7) {
            int index = random.nextInt(notes.length);
            return notes[index];
        } else {
            return "";
        }
    }

    private static void storeValue(int value, String dbColumn, ContentValues values) {
        if (value >= 1 && value <= 5) {
            values.put(dbColumn, value);
        }
    }

    private static void storeText(String s, String dbColumn, ContentValues values) {
        if (!s.equals("")) {
            values.put(dbColumn, s);
        }
    }
}
