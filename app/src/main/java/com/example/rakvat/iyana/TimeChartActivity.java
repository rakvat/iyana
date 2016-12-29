package com.example.rakvat.iyana;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.left;
import static android.R.attr.shape;


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
    public static final int MAX_X_RANGE = 1000 * 60 * 60 * 24 * 7; // 7 days

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_chart);
        initializeChart();
    }

    private void initializeChart() {
        Cursor cursor = getDBData();
        ScatterChart chart = (ScatterChart) findViewById(R.id.time_chart);
        List<String> titles = FactorTitleHelper.getFactorTitles(this);
        List<Entry> moodEntries = new ArrayList<Entry>();
        List<List<Entry>> entries = new ArrayList<List<Entry>>();
        long timeReference = populateEntries(cursor, titles, moodEntries, entries);
        setScatterData(chart, titles, moodEntries, entries);
        styleChart(chart, timeReference);
        chart.invalidate(); // refresh
    }

    private Cursor getDBData() {
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
        return cursor;
    }

    private long populateEntries(Cursor cursor, List<String> titles, List<Entry> moodEntries, List<List<Entry>> entries) {
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
        return timeReference;
    }

    private void setScatterData(Chart chart, List<String> titles, List<Entry> moodEntries, List<List<Entry>> entries) {
        List<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        ScatterDataSet dataSet = new ScatterDataSet(moodEntries, "Mood");
        styleDataSet(dataSet, Color.rgb(0, 0, 0));
        dataSets.add(dataSet);

        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null && entries.get(i).size() > 0) {
                ScatterDataSet factorDataSet = new ScatterDataSet(entries.get(i), titles.get(i));
                styleDataSet(factorDataSet, COLORS[i]);
                dataSets.add(factorDataSet);
            }
        }
        ScatterData data = new ScatterData(dataSets);
        chart.setData(data);
    }

    private void styleDataSet(ScatterDataSet dataSet, int color) {
        dataSet.setColor(color);
        dataSet.setDrawValues(false);
        dataSet.setScatterShapeSize(20);
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
    }

    private void styleChart(ScatterChart chart, long timeReference) {
        IAxisValueFormatter xAxisFormatter = new DateAxisValueFormatter(timeReference);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawAxisLine(true);
        right.setDrawGridLines(false);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(1);
        yAxis.setAxisMaximum(5);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setGranularity(1);
        yAxis.setLabelCount(5, true);

        // Hide the description
        Description d = new Description();
        d.setText("");
        chart.setDescription(d);

        chart.setVisibleXRangeMaximum(MAX_X_RANGE);
        chart.moveViewToX(new Date().getTime() - timeReference - MAX_X_RANGE);

        //MyMarkerView myMarkerView= new MyMarkerView(getApplicationContext(), R.layout.my_marker_view_layout, timeReference);
        //chart.setMarkerView(myMarkerView);
    }
}