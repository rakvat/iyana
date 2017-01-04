package com.example.rakvat.iyana;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.Map;



public class NoteMarkerView extends MarkerView {

    private TextView mTextView;
    private Map<Integer, String> mDate2NoteMap;

    public NoteMarkerView (Context context, int layoutResource, Map<Integer, String> date2NoteMap) {
        super(context, layoutResource);
        mTextView = (TextView) findViewById(R.id.tvContent);
        mDate2NoteMap = date2NoteMap;
    }

    // callbacks every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String t = mDate2NoteMap.get((int)(e.getX()/1000));
        if (t == null) {
            t = "";
        }
        mTextView.setText(t);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
