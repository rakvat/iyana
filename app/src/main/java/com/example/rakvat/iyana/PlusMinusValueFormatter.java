package com.example.rakvat.iyana;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class PlusMinusValueFormatter implements IAxisValueFormatter {

    private static final Map<Integer, String> value2SignMap;
    static {
        Map<Integer, String> aMap = new HashMap<Integer, String>();
        aMap.put(1, "--");
        aMap.put(2, "-");
        aMap.put(3, "+-");
        aMap.put(4, "+");
        aMap.put(5, "++");
        value2SignMap = Collections.unmodifiableMap(aMap);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        if (!value2SignMap.containsKey((int)value)) {
            return "";
        }
        return value2SignMap.get((int)value);
    }
}
