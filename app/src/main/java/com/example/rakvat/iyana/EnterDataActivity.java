package com.example.rakvat.iyana;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import static android.R.attr.value;
import static android.R.id.message;

public class EnterDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        setFactorTitles();
    }

    public void setFactorTitles() {
        int view_ids[] = { R.id.data_label0, R.id.data_label1,
                           R.id.data_label2, R.id.data_label3,
                           R.id.data_label4, R.id.data_label5,
                           R.id.data_label6, R.id.data_label7,
                           R.id.data_label8, R.id.data_label9 };
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.factor_storage_key),
                        Context.MODE_PRIVATE);
        for (int i = 0; i < view_ids.length; i++) {
            String factor_title = sharedPref.getString(getString(R.string.factor_storage_key) + i, "");
            if (factor_title.length() > 0) {
                TextView textView = (TextView) findViewById(view_ids[i]);
                textView.setText(factor_title + ":");
            }
        }
    }

    /** Called when the user clicks the save button */
    public void save(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MoodEntry.COLUMN_NAME_DATE, new Date().getTime());

        storeTextFrom(R.id.data_note, DatabaseContract.MoodEntry.COLUMN_NAME_NOTE, values);
        storeValueFrom(R.id.data_mood, DatabaseContract.MoodEntry.COLUMN_NAME_MOOD, values);

        int view_ids[] = { R.id.data0, R.id.data1, R.id.data2, R.id.data3, R.id.data4,
                           R.id.data5, R.id.data6, R.id.data7, R.id.data8, R.id.data9};
        for(int i = 0; i < view_ids.length; i++) {
            storeValueFrom(view_ids[i], DatabaseContract.MoodEntry.FACTOR_COLUMNS[i], values);
        }

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DatabaseContract.MoodEntry.TABLE_NAME,
                null,
                values);

        Toast toast = Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void storeValueFrom(int view_id, String dbColumn, ContentValues values) {
        EditText editText = (EditText) findViewById(view_id);
        String s = editText.getText().toString();
        if (!s.equals("")) {
            int value = Integer.parseInt(s);
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
}
