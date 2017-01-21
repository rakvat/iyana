package com.example.rakvat.iyana;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.offset;


public class ChartFragment extends Fragment {

    private static final float MARKER_OFFSET = 0.05f;
    private String mTitle;
    private Integer mColor;
    private ArrayList<Integer> mXValues;
    private ArrayList<Integer> mYValues;
    private ArrayList<Integer> mTimeValues;

    ViewGroup mRootView;


    // newInstance constructor for creating fragment with arguments
    public static ChartFragment newInstance(List<ArrayList<Integer>> numbers, String title, Integer color) {
        ChartFragment fragmentFirst = new ChartFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("color", color);
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

        mTitle = (String)getArguments().get("title");
        mColor = getArguments().getInt("color");
        mXValues = getArguments().getIntegerArrayList("xValues");
        mYValues = getArguments().getIntegerArrayList("yValues");
        mTimeValues = getArguments().getIntegerArrayList("timeValues");

        initializeChart();
        return mRootView;
    }

    private void initializeChart() {
        TextView label = (TextView) mRootView.findViewById(R.id.label);
        label.setText(Util.capitalize(mTitle));
        ScatterChart chart = (ScatterChart) mRootView.findViewById(R.id.mood_chart);
        List<Entry> entries = new ArrayList<Entry>();
        mTimeValues = populateEntries(entries);
        setScatterData(chart, entries);
        styleChart(chart);
        chart.invalidate(); // refresh
    }

    private ArrayList<Integer> populateEntries(List<Entry> entries) {
        int[][] valueCounter = new int[5][5]; // default value is 0
        ArrayList<Integer> modifiedTimeValues = new ArrayList<Integer>();
        for(int i = 0; i < mXValues.size(); i++) {
            //check if factor values are 0, skip those
            if(mYValues.get(i) != 0 && mXValues.get(i) != 0){
                modifiedTimeValues.add(mTimeValues.get(i));
                float offset = valueCounter[mXValues.get(i)-1][mYValues.get(i)-1] * MARKER_OFFSET;
                entries.add(new Entry(mXValues.get(i), mYValues.get(i) + offset));
                valueCounter[mXValues.get(i)-1][mYValues.get(i)-1] =
                        valueCounter[mXValues.get(i)-1][mYValues.get(i)-1] + 1;
            }
        }
        return modifiedTimeValues;
    }

    private void setScatterData(Chart chart, List<Entry> entries) {
        List<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        ScatterDataSet dataSet = new ScatterDataSet(entries, "");
        styleDataSet(dataSet);
        dataSets.add(dataSet);
        ScatterData data = new ScatterData(dataSets);
        chart.setData(data);
    }

    private void styleDataSet(ScatterDataSet dataSet) {
        int[] colors = new int[mTimeValues.size()];
        for (int i = 0; i < mTimeValues.size(); i++) {
            colors[i] = interpolateColor(mColor, mTimeValues.get(i));
        }
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setScatterShapeSize(20);
        dataSet.setScatterShape(ScatterChart.ScatterShape.SQUARE);
    }

    // a * (1-t) + b * t
    private int interpolateColor(Integer mColor, Integer timeValue) {
        float t = timeValue/255.0f;
        return Color.rgb(
                (int)(255.0f * (1.0f - t) + Color.red(mColor) * t),
                (int)(255.0f * (1.0f - t) + Color.green(mColor) * t),
                (int)(255.0f * (1.0f - t) + Color.blue(mColor) * t));
    }

    private void styleChart(ScatterChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.0f);
        xAxis.setAxisMaximum(6.0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(7, true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new PlusMinusValueFormatter());

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
        yAxis.setValueFormatter(new PlusMinusValueFormatter());

        // Hide the description
        Description d = new Description();
        d.setText("Mood");
        //d.setPosition(0.0f, chart.getViewPortHandler().contentTop() + 30);
        chart.setDescription(d);

        chart.getLegend().setEnabled(false);
        chart.setHighlightPerTapEnabled(false);

        chart.setScaleXEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
    }
}
