package com.example.rakvat.iyana;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.id;
import static android.view.View.GONE;


public class DiaryActivity extends AppCompatActivity {

    private static final int MAX_DAYS = 7;

    private static int mPage = 0;

    private static final Map<Integer, Integer> value2ViewIdMap;
    static {
        Map<Integer, Integer> aMap = new HashMap<Integer, Integer>();
        aMap.put(5, R.id.plusplus);
        aMap.put(4, R.id.plus);
        aMap.put(3, R.id.plusminus);
        aMap.put(2, R.id.minus);
        aMap.put(1, R.id.minusminus);
        value2ViewIdMap = Collections.unmodifiableMap(aMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Util.setTitleBar(this, R.string.nav_diary);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            mPage = b.getInt("page", 0);
        }
        initializeView();
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
                DialogFragment helpDialog = HelpDialog.newInstance(R.string.diary_data_help);
                helpDialog.show(getSupportFragmentManager(), "help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** Called when the user clicks an edit button */
    public void edit(View view) {
        Toast toast = Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT);
        toast.show();
    }

    /** Called when the user clicks a delete button */
    public void delete(View view) {
        Toast toast = Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT);
        toast.show();
    }

    /** Called when the user clicks a back button */
    public void back(View view) {
        Intent i = new Intent(this, DiaryActivity.class);
        i.putExtra("page", mPage + 1);
        startActivity(i);
        finish();
    }

    /** Called when the user clicks a forward button */
    public void forward(View view) {
        if (mPage == 0) {
            return;
        }
        Intent i = new Intent(this, DiaryActivity.class);
        i.putExtra("page", mPage - 1);
        startActivity(i);
        finish();
    }



    private void initializeView() {
        ViewGroup parent = (ViewGroup) findViewById(R.id.diary_rows);
        LayoutInflater inflater = getLayoutInflater();
        int fromDaysBeforeNow = MAX_DAYS * (mPage + 1);
        int toDaysBeforeNow = MAX_DAYS * mPage;
        Cursor cursor = Util.getDBData(this, fromDaysBeforeNow, toDaysBeforeNow, false);

        while(cursor.moveToNext()) {
            long timestamp = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_DATE));

            View rowView = inflater.inflate(R.layout.diary_row, null);
            TextView dateView = (TextView) rowView.findViewById(R.id.date);
            DateFormat format = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm", Locale.ENGLISH);
            Date date = new Date();
            date.setTime(timestamp);
            dateView.setText(format.format(date));

            String note = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_NOTE));
            TextView noteView = (TextView) rowView.findViewById(R.id.note);
            if (note != null && note != "") {
                noteView.setText(note);
                noteView.setVisibility(View.VISIBLE);
            }

            inflateRow(inflater, rowView, cursor,
                    DatabaseContract.MoodEntry.COLUMN_NAME_MOOD, "Mood");

            List<String> titles = FactorTitleHelper.getFactorTitles(this);
            for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
                if (titles.get(i) != null) {
                    inflateRow(inflater, rowView, cursor,
                            DatabaseContract.MoodEntry.FACTOR_COLUMNS[i],
                            Util.capitalize(titles.get(i)));
                }
            }
            rowView.setId((int) (long)timestamp);
            parent.addView(rowView);
        }

        if (mPage == 0) {
            findViewById(R.id.foreward).setVisibility(GONE);
        }
    }


    private void inflateRow(LayoutInflater inflater, View rowView, Cursor cursor, String dbId, String label) {
        int value = cursor.getInt(
                cursor.getColumnIndexOrThrow(dbId));
        if (value != 0) {
            ViewGroup rowParent = (ViewGroup) rowView.findViewById(R.id.diary_row_entries);
            View rowEntryView = inflater.inflate(R.layout.diary_row_entry, null);
            TextView labelView = (TextView) rowEntryView.findViewById(R.id.label);
            labelView.setText(label);

            TextView valueView = (TextView) rowEntryView.findViewById(value2ViewIdMap.get(value));
            valueView.setVisibility(View.VISIBLE);

            rowParent.addView(rowEntryView);
        }
    }
}
