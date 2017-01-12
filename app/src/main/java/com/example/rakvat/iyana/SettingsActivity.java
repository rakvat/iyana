package com.example.rakvat.iyana;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.duration;
import static android.R.id.message;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Util.setTitleBar(this, R.string.nav_settings);
        setFactorTitles();
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
                DialogFragment helpDialog = HelpDialog.newInstance(R.string.settings_help);
                helpDialog.show(getSupportFragmentManager(), "help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFactorTitles() {
        int view_ids[] = { R.id.factor0, R.id.factor1,
                R.id.factor2, R.id.factor3,
                R.id.factor4, R.id.factor5,
                R.id.factor6, R.id.factor7,
                R.id.factor8, R.id.factor9 };
        List<String> titles = FactorTitleHelper.getFactorTitles(this);
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            if (titles.get(i).length() > 0) {
                EditText editText = (EditText) findViewById(view_ids[i]);
                editText.setText(titles.get(i));
            }
        }
    }

    /** Called when the user clicks the save button */
    public void save(View view) {
        List<String> titles = new ArrayList<String>();
        int view_ids[] = {R.id.factor0, R.id.factor1, R.id.factor2, R.id.factor3, R.id.factor4,
                          R.id.factor5, R.id.factor6, R.id.factor7, R.id.factor8, R.id.factor9};
        for(int i = 0; i < view_ids.length; i++) {
            EditText editText = (EditText) findViewById(view_ids[i]);
            titles.add(editText.getText().toString());
        }
        FactorTitleHelper.setFactorTitles(this, titles);

        // TODO: check if table columns changed, if yes, delete previous data

        Toast toast = Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }
}
