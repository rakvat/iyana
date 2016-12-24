package com.example.rakvat.iyana;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.attr.duration;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /** Called when the user clicks the save button */
    public void save(View view) {
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.factor_storage_key),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int view_ids[] = {R.id.factor0, R.id.factor1, R.id.factor2, R.id.factor3, R.id.factor4,
                          R.id.factor5, R.id.factor6, R.id.factor7, R.id.factor8, R.id.factor9};
        for(int i = 0; i < view_ids.length; i++) {
            EditText editText = (EditText) findViewById(view_ids[i]);
            String message = editText.getText().toString();
            if (message.length() > 0) {
                editor.putString(getString(R.string.factor_storage_key) + i, message);
            }
        }
        editor.commit();

        // TODO: check if table columns changed, if yes, delete previous data

        Toast toast = Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT);
        toast.show();
    }
}
