package com.example.rakvat.iyana;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.id;

public class DiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Util.setTitleBar(this, R.string.nav_diary);


        ViewGroup parent = (ViewGroup) findViewById(R.id.diary_entry_list);
        LayoutInflater inflater = getLayoutInflater();
        Cursor cursor = Util.getDBData(this);

        while(cursor.moveToNext()) {
            long timestamp = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_DATE));

            View rowView = inflater.inflate(R.layout.diary_row, null);
            TextView dateView = (TextView) rowView.findViewById(R.id.date);
            DateFormat format = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm", Locale.ENGLISH);
            Date date = new Date();
            date.setTime(timestamp);
            dateView.setText(format.format(date));
            rowView.setId((int) (long)timestamp);
            parent.addView(rowView);
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
                DialogFragment helpDialog = HelpDialog.newInstance(R.string.diary_data_help);
                helpDialog.show(getSupportFragmentManager(), "help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** Called when the user clicks an edit button */
    public void edit(View view) {

    }
}
