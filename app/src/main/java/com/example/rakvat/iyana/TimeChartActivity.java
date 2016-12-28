package com.example.rakvat.iyana;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class TimeChartActivity extends AppCompatActivity {

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

// Filter results WHERE "title" = 'My Title'
        String selection = ""; //FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {}; //{ "My Title" };

// How you want the results sorted in the resulting Cursor
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




       LineChart chart = (LineChart) findViewById(R.id.time_chart);



        List<Entry> entries = new ArrayList<Entry>();
        List<Entry> entries2 = new ArrayList<Entry>();
        List<Entry> entries3 = new ArrayList<Entry>();

        long timeReference = 0;
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry._ID));
            int mood = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD));
            int factor0 = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR0));
            long timestamp = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_DATE));
            if(timeReference == 0) {
                timeReference = timestamp;
            }
            long t = timestamp - timeReference;
            entries.add(new Entry(t, itemId));
            entries2.add(new Entry(t, mood));
            entries3.add(new Entry(t, factor0));
        }
        cursor.close();

        LineDataSet dataSet = new LineDataSet(entries, "Test red"); // add entries to dataset
        dataSet.setColor(Color.rgb(255, 0, 0    ));
        LineDataSet dataSet2 = new LineDataSet(entries2, "Test green"); // add entries to dataset
        dataSet2.setColor(Color.rgb(0, 255, 0));
        LineDataSet dataSet3 = new LineDataSet(entries3, "Test blue"); // add entries to dataset
        dataSet3.setColor(Color.rgb(0, 0, 255));
        //dataSet.setValueTextColor(...); // styling, ...

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        LineData data = new LineData(dataSets);

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