package com.example.rakvat.iyana;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    private static final Map<Integer, String[]> notesMap;
    static {
        Map<Integer, String[]> aMap = new HashMap<Integer, String[]>();
        aMap.put(0, new String[] { "fucking weather", "finally sun!" });
        aMap.put(1, new String[] { "could not sleep :(", "" });
        aMap.put(2, new String[] { "", "went for a run" });
        aMap.put(3, new String[] { "ate too many sweets", "" });
        aMap.put(4, new String[] { "I hate work", "killed it at work" });
        aMap.put(5, new String[] { "", "met friends" });
        aMap.put(6, new String[] { "sick :(", "feeling good" });
        aMap.put(7, new String[] { "", "laughed a lot" });
        aMap.put(8, new String[] { "", "" });
        aMap.put(9, new String[] { "", "" });
        aMap.put(10, new String[] { "the world sucks", "we need a new society" });
        notesMap = Collections.unmodifiableMap(aMap);
    }

    private static long DAY_IN_MS = 1000 * 60 * 60 * 24;


    public static final void initialize(Context context) {
        FactorTitleHelper.setFactorTitles(context, Arrays.asList(titleNames), true);

        DatabaseHelper dbHelper = new DatabaseHelper(context, DatabaseContract.DEMO_DATABASE_NAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Random randomGenerator = new Random();

        for (int i = 0; i < 5; i++) { // weeks
            for (int j = 0; j < 7; j++) { // days
                ContentValues values = new ContentValues();
                int randomOffset = randomGenerator.nextInt(1000 * 60 * 60 * 2);
                Date date = new Date(System.currentTimeMillis() - (((5 - i) * 7 + (7 - j)) * DAY_IN_MS - randomOffset));
                values.put(DatabaseContract.MoodEntry.COLUMN_NAME_DATE, date.getTime());

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
                storeValue(getMood(values, i), DatabaseContract.MoodEntry.COLUMN_NAME_MOOD, values);
                storeText(randomNote(values), DatabaseContract.MoodEntry.COLUMN_NAME_NOTE, values);
                db.insert(DatabaseContract.MoodEntry.TABLE_NAME, null, values);
            }
        }
    }

    private static int getMood(ContentValues values, int week) {
        int mood = 0;
        for (int k = 0; k < DatabaseContract.MoodEntry.FACTOR_COLUMNS.length; k++) {
            if (k == 9 || k == 8 || k == 3) {
                continue;
            }
            Integer v = values.getAsInteger(DatabaseContract.MoodEntry.FACTOR_COLUMNS[k]);
            mood += (v == null ? 0 : v);
        }
        mood = Math.round(mood/5 + week/2);
        return Math.max(1, Math.min(5, mood));
    }

    private static String randomNote(ContentValues values) {
        String note = "";
        Random random = new Random();
        int factor = 0;
        int value = 0;
        int tries = 5;

        while (note == "" && tries > 0) {
            factor = random.nextInt(10);
            Integer v = values.getAsInteger(DatabaseContract.MoodEntry.FACTOR_COLUMNS[factor]);
            value = (v == null ? 0 : v);
            tries -= 1;

            if (value == 1) {
                note = notesMap.get(factor)[0];
            } else if (value == 5) {
                note = notesMap.get(factor)[1];
            }
        }

        Integer v = values.getAsInteger(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD);
        if (note == "" &&  (v == null ? 0 : v ) <= 3) {
            note = notesMap.get(10)[random.nextInt(2)];
        }
        return note;
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
