package com.example.rakvat.iyana;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.List;


public class TimeChartActivity extends AppCompatActivity {

    public static final int[] COLORS = {
            Color.rgb(170, 0, 212),
            Color.rgb(10, 0, 212),
            Color.rgb(0, 170, 212),
            Color.rgb(0, 212, 110),
            Color.rgb(212, 205, 0),
            Color.rgb(212, 170, 0),
            Color.rgb(212, 125, 0),
            Color.rgb(212, 0, 0),
            Color.rgb(118, 118, 118),
            Color.rgb(200, 200, 200),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_chart);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
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

        ScatterChart chart = (ScatterChart) findViewById(R.id.time_chart);
        List<String> titles = FactorTitleHelper.getFactorTitles(this);
        List<Entry> moodEntries = new ArrayList<Entry>();
        List<List<Entry>> entries = new ArrayList<List<Entry>>();
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            List<Entry> factor_entries = new ArrayList<Entry>();
            entries.add(factor_entries);
        }

        long timeReference = 0;
        while(cursor.moveToNext()) {
            long timestamp = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_DATE));
            if(timeReference == 0) {
                timeReference = timestamp;
            }
            long t = timestamp - timeReference;
            moodEntries.add(new Entry(t, cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD))));
            for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
                if (titles.get(i) != null) {
                    int value = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.FACTOR_COLUMNS[i]));
                    if (value != 0) {
                        entries.get(i).add(new Entry(t, value));
                    }
                }
            }
        }
        cursor.close();

        List<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        ScatterDataSet dataSet = new ScatterDataSet(moodEntries, "Mood");
        dataSet.setColor(Color.rgb(0, 0, 0));
        //dataSet.setValueTextColor(...); // styling, ...
        dataSets.add(dataSet);

        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null && entries.get(i).size() > 0) {
                ScatterDataSet factorDataSet = new ScatterDataSet(entries.get(i), titles.get(i));
                factorDataSet.setColor(COLORS[i]);
                dataSets.add(factorDataSet);
            }
        }

        ScatterData data = new ScatterData(dataSets);
        chart.setData(data);

        // style
        IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(timeReference);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);

        //MyMarkerView myMarkerView= new MyMarkerView(getApplicationContext(), R.layout.my_marker_view_layout, timeReference);
        //chart.setMarkerView(myMarkerView);

        chart.invalidate(); // refresh
    }
}