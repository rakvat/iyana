package com.example.rakvat.iyana;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.R.attr.value;
import static com.example.rakvat.iyana.R.id.note;

public class EnterDataActivity extends AppCompatActivity {

    private static final int MOOD_ID = 100;

    private ArrayList<Integer> mColumns = new ArrayList<Integer>();
    private int mRowId = -1;
    private long mTimestamp;

    private static final Map<Integer, Integer> viewId2ValueMap;
    static {
        Map<Integer, Integer> aMap = new HashMap<Integer, Integer>();
        aMap.put(R.id.plusplus, 5);
        aMap.put(R.id.plus, 4);
        aMap.put(R.id.plusminus, 3);
        aMap.put(R.id.minus, 2);
        aMap.put(R.id.minusminus, 1);
        viewId2ValueMap = Collections.unmodifiableMap(aMap);
    }
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
        setContentView(R.layout.activity_enter_data);
        Util.setTitleBar(this, R.string.nav_enter_data);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            mRowId = b.getInt("row", -1);
        }
        Cursor cursor = null;
        if (mRowId != -1) {
            cursor = Util.getDBRow(this, mRowId);
            cursor.moveToNext();
            mTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_DATE));
        }

        List<String> titles = FactorTitleHelper.getFactorTitles(this);
        ViewGroup parent = (ViewGroup) findViewById(R.id.enter_data_list);
        LayoutInflater inflater = getLayoutInflater();

        inflateEnterValue(inflater, parent, getString(R.string.enter_data_mood_label), MOOD_ID, cursor);

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i) == null || titles.get(i).length() == 0) {
                continue;
            }
            mColumns.add(i);
            inflateEnterValue(inflater, parent, Util.capitalize(titles.get(i)), i, cursor);
        }

        if (cursor != null) {
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_NOTE));
            if (note != null) {
                EditText editText = (EditText) findViewById(R.id.data_note);
                editText.setText(note);
            }
        }
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
                DialogFragment helpDialog = HelpDialog.newInstance(R.string.enter_data_help);
                helpDialog.show(getSupportFragmentManager(), "help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Called when the user clicks the save button */
    public void save(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(this, DatabaseContract.DATABASE_NAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MoodEntry.COLUMN_NAME_DATE, new Date().getTime());

        storeTextFrom(R.id.data_note, DatabaseContract.MoodEntry.COLUMN_NAME_NOTE, values);
        storeValueFrom(MOOD_ID, DatabaseContract.MoodEntry.COLUMN_NAME_MOOD, values);
        if (values.getAsInteger(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD) == null) {
            Toast toast = Toast.makeText(this, R.string.data_mood_required, Toast.LENGTH_LONG);
            toast.show();
        } else {
            for (int i = 0; i < DatabaseContract.MoodEntry.FACTOR_COLUMNS.length; i++) {
                if (mColumns.contains(i)) {
                    storeValueFrom(i, DatabaseContract.MoodEntry.FACTOR_COLUMNS[i], values);
                }
            }
            if (mRowId != -1) {
                values.put(DatabaseContract.MoodEntry.COLUMN_NAME_DATE, mTimestamp);
                String[] selectionArgs = {Integer.toString(mRowId)};
                db.update(DatabaseContract.MoodEntry.TABLE_NAME, values,
                        DatabaseContract.MoodEntry._ID + " = ?",
                        selectionArgs);
            } else {
                db.insert(DatabaseContract.MoodEntry.TABLE_NAME, null, values);
            }
            Toast toast = Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    private void storeValueFrom(int view_id, String dbColumn, ContentValues values) {
        RadioGroup radioButtonGroup = (RadioGroup) findViewById(view_id).findViewById(R.id.radio_group);
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        if (radioButtonID != -1) {
            int value = viewId2ValueMap.get(radioButtonID);
            values.put(dbColumn, value);
        }
    }

    private void storeTextFrom(int view_id, String dbColumn, ContentValues values) {
        EditText editText = (EditText) findViewById(view_id);
        String s = editText.getText().toString();
        if (!s.equals("")) {
            values.put(dbColumn, s);
        }
    }

    private void inflateEnterValue(LayoutInflater inflater, ViewGroup parent, String title, int id, Cursor cursor) {
        View rowView = inflater.inflate(R.layout.enter_data_row, null);
        TextView label = (TextView) rowView.findViewById(R.id.label);
        int color = Color.BLACK;
        if (id != MOOD_ID) {
            color = Util.COLORS[id];
        }
        label.setTextColor(color);
        label.setText(title);
        rowView.setId(id);
        if (cursor != null) {
            String dbColumnId = DatabaseContract.MoodEntry.COLUMN_NAME_MOOD;
            if (id != MOOD_ID) {
                dbColumnId = DatabaseContract.MoodEntry.FACTOR_COLUMNS[id];
            }
            int value = cursor.getInt(cursor.getColumnIndexOrThrow(dbColumnId));
            if (value != 0) {
                RadioButton button = (RadioButton) rowView.findViewById(value2ViewIdMap.get(value));
                button.setChecked(true);
            }
        }
        parent.addView(rowView);
    }
}
