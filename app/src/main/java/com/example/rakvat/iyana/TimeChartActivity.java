package com.example.rakvat.iyana;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.rakvat.iyana.R.string.app_name;

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
    private static final int MAX_X_RANGE = 1000 * 60 * 60 * 24 * 7; // 7 days
    private static final float MARKER_OFFSET = 0.1f;
    private static final int X_OFFSET = 1000 * 60 * 60 * 6;

    private List<Integer> mValueCounter;
    private Map<Integer, String> mDate2NotesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_chart);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        Util.setTitleBar(this, R.string.nav_time_graphs);
        initializeChart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.helpnav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_back:
                finish();
                return true;
            case R.id.action_help:
                DialogFragment helpDialog = HelpDialog.newInstance(R.string.time_chart_help);
                helpDialog.show(getSupportFragmentManager(), "help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mDate2NotesMap = new HashMap<Integer, String>();
        long timeReference = 0;
        long t = 0;
        while(cursor.moveToNext()) {
            long timestamp = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_DATE));
            if(timeReference == 0) {
                timeReference = timestamp;
            }
            t = timestamp - timeReference;
            String note = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_NOTE));
            if (note != null) {
                mDate2NotesMap.put((int)(t/1000), note);
            }
            int value = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD));
            mValueCounter = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0));
            mValueCounter.set(value - 1, mValueCounter.get(value - 1) + 1);
            moodEntries.add(new Entry(t, value + mValueCounter.get(value - 1) * MARKER_OFFSET));
            for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
                if (titles.get(i) != null) {
                    value = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.FACTOR_COLUMNS[i]));
                    if (value != 0) {
                        mValueCounter.set(value - 1, mValueCounter.get(value - 1) + 1);
                        entries.get(i).add(new Entry(t, value + mValueCounter.get(value - 1) * MARKER_OFFSET));
                    }
                }
            }
        }
        cursor.close();
        // additional point to force the chart to include the full circle of the last data point
        moodEntries.add(new Entry(t + X_OFFSET, -5));
        return timeReference;
    }

    private void setScatterData(Chart chart, List<String> titles, List<Entry> moodEntries, List<List<Entry>> entries) {
        List<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        ScatterDataSet dataSet = new ScatterDataSet(moodEntries, "Mood");
        styleDataSet(dataSet, Color.rgb(0, 0, 0), ScatterChart.ScatterShape.SQUARE);
        dataSets.add(dataSet);

        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null && entries.get(i).size() > 0) {
                ScatterDataSet factorDataSet =
                        new ScatterDataSet(entries.get(i), Util.capitalize(titles.get(i)));
                styleDataSet(factorDataSet, COLORS[i]);
                dataSets.add(factorDataSet);
            }
        }
        ScatterData data = new ScatterData(dataSets);
        chart.setData(data);
    }

    private void styleDataSet(ScatterDataSet dataSet, int color) {
        styleDataSet(dataSet, color, ScatterChart.ScatterShape.CIRCLE);
    }

    private void styleDataSet(ScatterDataSet dataSet, int color, ScatterChart.ScatterShape shape) {
        dataSet.setColor(color);
        dataSet.setDrawValues(false);
        dataSet.setScatterShapeSize(20);
        dataSet.setScatterShape(shape);
    }

    private void styleChart(ScatterChart chart, long timeReference) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DateAxisValueFormatter(timeReference));
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawAxisLine(true);
        right.setDrawGridLines(false);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0.0f);
        yAxis.setAxisMaximum(6.0f);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setGranularity(1);
        yAxis.setLabelCount(7, true);
        yAxis.setDrawGridLines(false);
        yAxis.setValueFormatter(new YAxisValueFormatter());

        // Hide the description
        Description d = new Description();
        d.setText("");
        chart.setDescription(d);

        Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);

        chart.setVisibleXRangeMaximum(MAX_X_RANGE);
        chart.moveViewToX(new Date().getTime() - timeReference - MAX_X_RANGE);

        chart.setScaleYEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);

        NoteMarkerView markerView = new NoteMarkerView(this, R.layout.note_marker_view, mDate2NotesMap);
        chart.setMarker(markerView);
    }
}