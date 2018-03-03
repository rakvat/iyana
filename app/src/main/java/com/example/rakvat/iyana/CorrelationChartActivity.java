package com.example.rakvat.iyana;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;


public class CorrelationChartActivity extends AppCompatActivity {

    protected ArrayList<Integer> mMoodValues;
    protected ArrayList<Integer> mTimeValues;
    protected ArrayList<ArrayList<Integer>> mFactorValues;

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
        List<Entry> moodEntries = new ArrayList<Entry>();
        List<List<Entry>> entries = new ArrayList<List<Entry>>();


        List<BarEntry> barEntries = new ArrayList<BarEntry>();

        setBarChartData(chart, titles, barEntries);

        chart.invalidate(); // refresh
    }

    private void setBarChartData(Chart chart, List<String> titles, List<BarEntry> entries) {
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        // gap of 2f
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(6f, 60f));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.helpnav, menu);
        return true;
    }
}
