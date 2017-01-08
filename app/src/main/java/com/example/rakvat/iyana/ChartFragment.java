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

    private static final float MARKER_OFFSET = 0.1f;

    ViewGroup mRootView;


    // newInstance constructor for creating fragment with arguments
    public static ChartFragment newInstance(List<ArrayList<Integer>> numbers, String title) {
        ChartFragment fragmentFirst = new ChartFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putIntegerArrayList("xValues", numbers.get(0));
        args.putIntegerArrayList("yValues", numbers.get(1));
        args.putIntegerArrayList("timeValues", numbers.get(2));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(
                R.layout.chart_fragment, container, false);

        String title = (String)getArguments().get("title");
        ArrayList<Integer> xValues = getArguments().getIntegerArrayList("xValues");
        ArrayList<Integer> yValues = getArguments().getIntegerArrayList("yValues");
        ArrayList<Integer> timeValues = getArguments().getIntegerArrayList("timeValues");

        initializeChart(title, xValues, yValues, timeValues);
        return mRootView;
    }

    private void initializeChart(String title,
                                 ArrayList<Integer> xValues,
                                 ArrayList<Integer> yValues,
                                 ArrayList<Integer> timeValues) {

        ScatterChart chart = (ScatterChart) mRootView.findViewById(R.id.mood_chart);
        List<Entry> entries = new ArrayList<Entry>();
        timeValues = populateEntries(entries, xValues, yValues, timeValues);
        setScatterData(chart, entries, timeValues, title);
        styleChart(chart);
        chart.invalidate(); // refresh
    }




    private ArrayList<Integer> populateEntries(List<Entry> entries,
                                 ArrayList<Integer> xValues,
                                 ArrayList<Integer> yValues,
                                 ArrayList<Integer> timeValues
                                 ) {
        // TODO: check for multiple values on same spot, use offset
        ArrayList<Integer> modifiedTimeValues = new ArrayList<Integer>();
        for(int i = 0; i < xValues.size(); i++) {
            //check if factor values are 0, skip those
            if(yValues.get(i) != 0){
                modifiedTimeValues.add(timeValues.get(i));
                entries.add(new Entry(xValues.get(i), yValues.get(i)));
            }
        }
        return modifiedTimeValues;
    }

    private void setScatterData(Chart chart, List<Entry> entries, ArrayList<Integer> timeValues, String title) {
        List<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();

        ScatterDataSet dataSet = new ScatterDataSet(entries, title);
        // TODO change color to color of factor
        styleDataSet(dataSet, Color.rgb(0, 0, 0), timeValues);
        dataSets.add(dataSet);

        ScatterData data = new ScatterData(dataSets);
        chart.setData(data);
    }

    private void styleDataSet(ScatterDataSet dataSet, int color, ArrayList<Integer> timeValues) {
       dataSet.setColors(color);
        //dataSet.setColors(); // TODO: does that help? we can have multiple colors?
        // use timeValues for that
        dataSet.setDrawValues(false);
        dataSet.setScatterShapeSize(20);
        dataSet.setScatterShape(ScatterChart.ScatterShape.SQUARE);
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
