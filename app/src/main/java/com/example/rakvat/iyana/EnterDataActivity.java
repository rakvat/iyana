package com.example.rakvat.iyana;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterDataActivity extends AppCompatActivity {

    private static final int MOOD_ID = 100;

    private ArrayList<Integer> mColumns = new ArrayList<Integer>();

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        Util.setTitleBar(this, R.string.nav_enter_data);

        List<String> titles = FactorTitleHelper.getFactorTitles(this);
        ViewGroup parent = (ViewGroup) findViewById(R.id.enter_data_list);
        LayoutInflater inflater = getLayoutInflater();

        inflateEnterValue(inflater, parent, getString(R.string.enter_data_mood_label), MOOD_ID);

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i) == null || titles.get(i) == "") {
                continue;
            }
            mColumns.add(i);
            inflateEnterValue(inflater, parent, Util.capitalize(titles.get(i)), i);
        }
    }

    /** Called when the user clicks the save button */
    public void save(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MoodEntry.COLUMN_NAME_DATE, new Date().getTime());

        storeTextFrom(R.id.data_note, DatabaseContract.MoodEntry.COLUMN_NAME_NOTE, values);
        storeValueFrom(MOOD_ID, DatabaseContract.MoodEntry.COLUMN_NAME_MOOD, values);
        for(int i = 0; i < DatabaseContract.MoodEntry.FACTOR_COLUMNS.length; i++) {
            if (mColumns.contains(i)) {
                storeValueFrom(i, DatabaseContract.MoodEntry.FACTOR_COLUMNS[i], values);
            }
        }
        db.insert(DatabaseContract.MoodEntry.TABLE_NAME, null, values);

        Toast toast = Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT);
        toast.show();
        finish();
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

    private void inflateEnterValue(LayoutInflater inflater, ViewGroup parent, String title, int id) {
        View rowView = inflater.inflate(R.layout.enter_data_row, null);
        TextView label = (TextView) rowView.findViewById(R.id.label);
        label.setText(title);
        rowView.setId(id);
        parent.addView(rowView);
    }
}
