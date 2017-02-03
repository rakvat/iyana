package com.example.rakvat.iyana;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Date;

import static android.R.attr.name;
import static android.R.attr.offset;

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

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    private static final String[] projection = {
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


    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void setTitleBar(AppCompatActivity context, int string_id) {
        context.setSupportActionBar((Toolbar) context.findViewById(R.id.my_toolbar));
        context.getSupportActionBar().setDisplayShowHomeEnabled(true);
        context.getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        context.getSupportActionBar().
                setTitle("  " + context.getString(string_id));
    }

    public static Cursor getDBData(Context context) {
        return getDBData(context, 0);
    }

    public static Cursor getDBData(Context context, int fromDaysBeforeNow) {
        return getDBData(context, fromDaysBeforeNow, 0, true);
    }

    public static Cursor getDBData(Context context, int fromDaysBeforeNow, int toDaysBeforeNow, boolean asc) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "";
        String[] selectionArgs = {};
        if (fromDaysBeforeNow > 0) {
            long now = new Date().getTime();
            long lowerLimit = now - 1000l * 60 * 60 * 24 * fromDaysBeforeNow;
            if (toDaysBeforeNow > 0) {
                long upperLimit = now - 1000l * 60 * 60 * 24 * toDaysBeforeNow;
                selection = DatabaseContract.MoodEntry.COLUMN_NAME_DATE + " < ? AND " +
                        DatabaseContract.MoodEntry.COLUMN_NAME_DATE + " >= ?";
                selectionArgs = new String[]{Long.toString(upperLimit), Long.toString(lowerLimit)};
            } else {
                selection = DatabaseContract.MoodEntry.COLUMN_NAME_DATE + " >= ?";
                selectionArgs = new String[]{Long.toString(lowerLimit)};
            }
        }
        String sortOrder =
                DatabaseContract.MoodEntry.COLUMN_NAME_DATE + " ASC";
        if (asc == false) {
            sortOrder = DatabaseContract.MoodEntry.COLUMN_NAME_DATE + " DESC";
        }
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

    public static Cursor getDBRow(Context context, int dbRow) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = DatabaseContract.MoodEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(dbRow)};

        Cursor cursor = db.query(
                DatabaseContract.MoodEntry.TABLE_NAME,    // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        return cursor;
    }

    public static void deleteRow(Context context, int dbRow)
    {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseContract.MoodEntry.TABLE_NAME,
                DatabaseContract.MoodEntry._ID + "=" + Integer.toString(dbRow), null);
    }

    public static void deleteColumn(Context context, String dbColumn)
    {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbColumn, "");
        String[] selectionArgs = { };
        db.update(DatabaseContract.MoodEntry.TABLE_NAME, values, null, selectionArgs);
    }

}
