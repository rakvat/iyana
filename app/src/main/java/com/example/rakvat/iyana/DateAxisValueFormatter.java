package com.example.rakvat.iyana;


import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


// from https://github.com/PhilJay/MPAndroidChart/issues/789
public class DateAxisValueFormatter implements IAxisValueFormatter
{

    private static final float DAY_HOUR_THRESHOLD = 60 * 60 * 24; // 1 day

    private long referenceTimestamp; // minimum timestamp in your data set
    private DateFormat mDayFormat;
    private DateFormat mHourFormat;
    private Date mDate;

    public DateAxisValueFormatter(long referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
        this.mHourFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        this.mDayFormat = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
        this.mDate = new Date();
    }


    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // convertedTimestamp = originalTimestamp - referenceTimestamp
        long convertedTimestamp = (long) value;

        // Retrieve original timestamp
        long originalTimestamp = referenceTimestamp + convertedTimestamp;

        if (axis.getAxisMaximum() - axis.getAxisMinimum() > DAY_HOUR_THRESHOLD) {
            return getDay(originalTimestamp);
        } else {
            return getHour(originalTimestamp);
        }
    }

    //@Override
    public int getDecimalDigits() {
        return 0;
    }

    private String getHour(long timestamp){
        try{
            mDate.setTime(timestamp);
            return mHourFormat.format(mDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    private String getDay(long timestamp){
        try{
            mDate.setTime(timestamp);
            return mDayFormat.format(mDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}