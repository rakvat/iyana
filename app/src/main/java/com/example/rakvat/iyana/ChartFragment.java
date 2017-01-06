package com.example.rakvat.iyana;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.List;



public class ChartFragment extends Fragment {

    ViewGroup mRootView;


    // newInstance constructor for creating fragment with arguments
    public static ChartFragment newInstance(int page, String title) {
        ChartFragment fragmentFirst = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        // TODO: args.putIntegerArrayList();
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(
                R.layout.chart_fragment, container, false);
        initializeChart();
        return mRootView;
    }

    private void initializeChart() {
        Cursor cursor = Util.getDBData(getActivity());
        ScatterChart chart = (ScatterChart) mRootView.findViewById(R.id.mood_chart);
        List<Entry> entries = new ArrayList<Entry>();
        populateEntries(cursor, entries);
        setScatterData(chart, entries);
        styleChart(chart);
        chart.invalidate(); // refresh
    }


    private void populateEntries(Cursor cursor, List<Entry> entries) {
        while(cursor.moveToNext()) {
            int moodValue = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD));
            int factor0Value = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_FACTOR0));
            entries.add(new Entry(moodValue, factor0Value));
        }
        cursor.close();
    }

    private void setScatterData(Chart chart, List<Entry> entries) {
        List<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();

        ScatterDataSet dataSet = new ScatterDataSet(entries, "Factor0");
        styleDataSet(dataSet, Color.rgb(0, 0, 0), ScatterChart.ScatterShape.SQUARE);
        dataSets.add(dataSet);

        ScatterData data = new ScatterData(dataSets);
        chart.setData(data);
    }

    private void styleDataSet(ScatterDataSet dataSet, int color, ScatterChart.ScatterShape shape) {
        dataSet.setColor(color);
        //dataSet.setColors(); // TODO: does that help? we can have multiple colors?
        dataSet.setDrawValues(false);
        dataSet.setScatterShapeSize(20);
        dataSet.setScatterShape(shape);
    }

    private void styleChart(ScatterChart chart) {
        XAxis xAxis = chart.getXAxis();
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

        chart.setScaleYEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
    }
}
