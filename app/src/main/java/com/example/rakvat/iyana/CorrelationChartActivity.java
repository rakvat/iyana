package com.example.rakvat.iyana;

import android.database.Cursor;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;


public class CorrelationChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correlation_chart);
        Util.setTitleBar(this, R.string.nav_correlation_graph);
        initializeChart();
    }

    private void initializeChart() {
        Cursor cursor = Util.getDBData(this);
        BarChart chart = (BarChart) findViewById(R.id.correlation_chart);
        List<String> titles = FactorTitleHelper.getFactorTitles(this);
        List<Integer> moodEntries = new ArrayList<Integer>();
        List<List<Integer>> entries = new ArrayList<List<Integer>>();

        readDataFromDB(cursor, titles, moodEntries, entries);
        List<Float> means = new ArrayList<Float>();
        means.add(getMean(moodEntries));
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null) {
                means.add(getMean(entries.get(i)));
            }
        }

        List<Float> covariance = new ArrayList<Float>();
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null) {
                covariance.add(getCovariance(moodEntries, means.get(0), entries.get(i), means.get(i+1)));
            }
        }

        List<BarEntry> barEntries = new ArrayList<BarEntry>();
        int counter = 0;
        for (Float f : covariance) {
            barEntries.add(new BarEntry(counter, f == null ? 0 : f));
            counter += 1;
        }
        setBarChartData(chart, titles, barEntries);
        styleChart(chart);
        chart.invalidate(); // refresh
    }

    private void readDataFromDB(Cursor cursor, List<String> titles, List<Integer> moodEntries, List<List<Integer>> entries) {
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            List<Integer> factorEntries = new ArrayList<Integer>();
            entries.add(factorEntries);
        }

        while (cursor.moveToNext()) {
            Integer value = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD));
            moodEntries.add(value == 0 ? null : value);
            for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
                if (titles.get(i) != null) {
                    value = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.FACTOR_COLUMNS[i]));
                    entries.get(i).add(value == 0 ? null : value);
                }
            }
        }
        cursor.close();
    }


    private void setBarChartData(Chart chart, List<String> titles, List<BarEntry> entries) {
        BarDataSet set = new BarDataSet(entries, "Correlation with Mood");
        set.setColors(Util.COLORS);
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
    }

    private void styleChart(BarChart chart) {
        // Hide the description
        Description d = new Description();
        d.setText("");
        chart.setDescription(d);
    }

    private Float getMean(List<Integer> values) {
        int n = 0;
        int acc = 0;
        for (Integer i : values) {
            if (i != null) {
                n += 1;
                acc += i;
            }
        }

        if (n == 0) {
            return null;
        }
        return ((float)acc)/n;
    }

    private Float getCovariance(List<Integer> xs, Float xMean, List<Integer> ys, Float yMean) {
        int n = 0;
        int acc = 0;
        for (int i = 0; i < xs.size(); i++) {
            if (xs.get(i) != null && ys.get(i) != null) {
                acc += (xs.get(i) - xMean) * (ys.get(i) - yMean);
                n += 1;
            }
        }
        if (n <= 1) {
            return null;
        }
        return (1/(float)(n-1)) * acc;
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
                DialogFragment helpDialog = HelpDialog.newInstance(R.string.correlation_chart_help);
                helpDialog.show(getSupportFragmentManager(), "help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}