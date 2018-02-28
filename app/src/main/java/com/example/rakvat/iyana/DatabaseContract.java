package com.example.rakvat.iyana;

import android.provider.BaseColumns;


public final class DatabaseContract {

    public static final  int    DATABASE_VERSION   = 1;
    public static final  String DATABASE_NAME      = "database.db";
    public static final  String DEMO_DATABASE_NAME      = "demo_database.db";


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static class MoodEntry implements BaseColumns {
        public static final String TABLE_NAME = "mood_entries";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_MOOD = "mood";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_FACTOR0 = "factor0";
        public static final String COLUMN_NAME_FACTOR1 = "factor1";
        public static final String COLUMN_NAME_FACTOR2 = "factor2";
        public static final String COLUMN_NAME_FACTOR3 = "factor3";
        public static final String COLUMN_NAME_FACTOR4 = "factor4";
        public static final String COLUMN_NAME_FACTOR5 = "factor5";
        public static final String COLUMN_NAME_FACTOR6 = "factor6";
        public static final String COLUMN_NAME_FACTOR7 = "factor7";
        public static final String COLUMN_NAME_FACTOR8 = "factor8";
        public static final String COLUMN_NAME_FACTOR9 = "factor9";

        public static final String[] FACTOR_COLUMNS = {
                COLUMN_NAME_FACTOR0, COLUMN_NAME_FACTOR1,
                COLUMN_NAME_FACTOR2, COLUMN_NAME_FACTOR3,
                COLUMN_NAME_FACTOR4, COLUMN_NAME_FACTOR5,
                COLUMN_NAME_FACTOR6, COLUMN_NAME_FACTOR7,
                COLUMN_NAME_FACTOR8, COLUMN_NAME_FACTOR9 };

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_DATE + " TIMESTAMP," +
                        COLUMN_NAME_MOOD + " TINYINT," +
                        COLUMN_NAME_FACTOR0 + " TINYINT," +
                        COLUMN_NAME_FACTOR1 + " TINYINT," +
                        COLUMN_NAME_FACTOR2 + " TINYINT," +
                        COLUMN_NAME_FACTOR3 + " TINYINT," +
                        COLUMN_NAME_FACTOR4 + " TINYINT," +
                        COLUMN_NAME_FACTOR5 + " TINYINT," +
                        COLUMN_NAME_FACTOR6 + " TINYINT," +
                        COLUMN_NAME_FACTOR7 + " TINYINT," +
                        COLUMN_NAME_FACTOR8 + " TINYINT," +
                        COLUMN_NAME_FACTOR9 + " TINYINT," +
                        COLUMN_NAME_NOTE + " TEXT)";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}