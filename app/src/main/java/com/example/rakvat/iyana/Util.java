package com.example.rakvat.iyana;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import java.sql.Timestamp;
import java.util.Date;

public class Util {

    public static final int[] COLORS = {
            Color.rgb(220, 0, 176),
            Color.rgb(140, 0, 220),
            Color.rgb(2, 37, 243),
            Color.rgb(42, 195, 252),
            Color.rgb(65, 201, 3),
            Color.rgb(254, 245, 6),
            Color.rgb(254, 147, 7),
            Color.rgb(220, 0, 0),
            Color.rgb(121, 0, 0),
            Color.rgb(0, 121, 11),
    };

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void setTitleBar(AppCompatActivity context, int string_id) {
        context.getSupportActionBar().
                setTitle(context.getString(R.string.app_name) + ": " + context.getString(string_id));
    }

    public static Cursor getDBData(Context context) {
        return getDBData(context, 0);
    }

    public static Cursor getDBData(Context context, int numberOfDays) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DatabaseContract.MoodEntry._ID,
                DatabaseContract.MoodEntry.COLUMN_NAME_DATE,
                DatabaseContract.MoodEntry.COLUMN_NAME_MOOD,
                DatabaseContract.MoodEntry.COLUMN_NAME_NOTE,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR0,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR1,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR2,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR3,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR4,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR5,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR6,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR7,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR8,
                DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR9
        };

        String selection = "";
        String[] selectionArgs = {};
        if(numberOfDays > 0) {
            long now = new Date().getTime();
            long timeLimit = now - 1000l * 60 * 60 * 24 * numberOfDays;
            selection = DatabaseContract.MoodEntry.COLUMN_NAME_DATE + " > ?";
            selectionArgs = new String[] { Long.toString(timeLimit) };
        }
        String sortOrder =
                DatabaseContract.MoodEntry.COLUMN_NAME_DATE + " ASC";
        Cursor cursor = db.query(
                DatabaseContract.MoodEntry.TABLE_NAME,    // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return cursor;
    }
}
