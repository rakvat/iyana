package com.example.rakvat.iyana;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.CompoundButton;


public class HelpActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Util.setTitleBar(this, R.string.nav_help);

        Switch s = (Switch) findViewById(R.id.helpDemoSwitch);
        if (s != null) {
            s.setChecked(getDemoMode());
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setDemoMode(buttonView.isChecked());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backnav, menu);
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

    private boolean getDemoMode() {
        SharedPreferences sharedPref =
                this.getSharedPreferences(this.getString(R.string.demo_mode_preference),
                        Context.MODE_PRIVATE);
        return sharedPref.getBoolean(this.getString(R.string.demo_mode_key), false);
    }

    private void setDemoMode(boolean flag) {
        SharedPreferences sharedPref =
                this.getSharedPreferences(this.getString(R.string.demo_mode_preference),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(this.getString(R.string.demo_mode_key), flag);
        editor.commit();
    }
}
