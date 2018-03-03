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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
            if (titles.get(i) != null && titles.get(i) != "") {
                means.add(getMean(entries.get(i)));
            }
        }

        List<Float> covariance = new ArrayList<Float>();
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null && titles.get(i) != "") {
                covariance.add(getCovariance(moodEntries, means.get(0), entries.get(i), means.get(i+1)));
            }
        }

        List<Float> stdDeviation = new ArrayList<Float>();
        stdDeviation.add(getStdDeviation(moodEntries, means.get(0)));
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null && titles.get(i) != "") {
                stdDeviation.add(getStdDeviation(entries.get(i), means.get(i+1)));
            }
        }

        // Pearson_correlation_coefficient
        List<Float> correlation = new ArrayList<Float>();
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null && titles.get(i) != "") {
                correlation.add(getCorrelation(covariance.get(i), stdDeviation.get(0), stdDeviation.get(i+1)));
            }
        }

        List<BarEntry> barEntries = new ArrayList<BarEntry>();
        int counter = 0;
        // for testing you can plugin means, covariance, stdDeviation in here
        for (Float f : correlation) {
            if (f != null) {
                barEntries.add(new BarEntry(counter, f));
                counter += 1;
            }
        }
        setBarChartData(chart, titles, barEntries);
        styleChart(chart, titles);
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
                if (titles.get(i) != null && titles.get(i) != "") {
                    value = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.FACTOR_COLUMNS[i]));
                    entries.get(i).add(value == 0 ? null : value);
                }
            }
        }
        cursor.close();
    }


    private void setBarChartData(Chart chart, List<String> titles, List<BarEntry> entries) {
        BarDataSet set = new BarDataSet(entries, "Correlation of Factors with Mood");
        set.setColors(Util.COLORS);
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
    }

    private void styleChart(BarChart chart, List<String> titles) {
        // Hide the description
        Description d = new Description();
        d.setText("");
        chart.setDescription(d);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawAxisLine(true);
        right.setDrawGridLines(false);

        Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);
        List<Integer> usedColors = new ArrayList<Integer>();
        List<String> usedLabels = new ArrayList<String>();
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i) != null && titles.get(i) != "") {
                usedColors.add(Util.COLORS[i]);
                usedLabels.add(titles.get(i));
            }
        }
        int[] usedColorsInt = new int[usedColors.size()];
        for (int i = 0; i < usedColors.size(); i++) {
            usedColorsInt[i] = usedColors.get(i);
        }
        legend.setExtra(usedColorsInt, usedLabels.toArray(new String[0]));
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

    private Float getStdDeviation(List<Integer> values, Float mean) {
        int n = 0;
        float acc = 0;
        for (Integer i : values) {
            if (i != null) {
                n += 1;
                acc += Math.pow(i - mean, 2);
            }
        }

        if (n <= 1) {
            return null;
        }
        return (float)(Math.sqrt(acc/(float)(n-1)));
    }

    private Float getCovariance(List<Integer> xs, Float xMean, List<Integer> ys, Float yMean) {
        int n = 0;
        float acc = 0;
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

    private Float getCorrelation(Float covariance, Float xStdDeviation, Float yStdDeviation) {
        if (covariance == null || xStdDeviation == null || yStdDeviation == null) {
            return null;
        }
        return covariance/(xStdDeviation * yStdDeviation);
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