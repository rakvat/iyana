package com.example.rakvat.iyana;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

public class Util {
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void setTitleBar(AppCompatActivity context, int string_id) {
        context.getSupportActionBar().
                setTitle(context.getString(R.string.app_name) + ": " + context.getString(string_id));
    }


    public static Cursor getDBData(Context context) {
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
