package com.example.rakvat.iyana;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.setTitleBar(this, R.string.app_name);

        if (!PreferencesHelper.getAfterInstall(this)) {
            DemoData.initialize(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.enter_data:
                enterData();
                return true;
            case R.id.settings:
                showSettings();
                return true;
            case R.id.help:
                showHelp();
                return true;
            case R.id.about:
                showAbout();
                return true;
            case R.id.time_charts:
                showTimeCharts();
                return true;
            case R.id.diary:
                showDiary();
                return true;
            case R.id.mood_charts:
                showMoodCharts();
                return true;
            case R.id.correlation_charts:
                showCorrelationCharts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDiary() {
        Intent intent = new Intent(this, DiaryActivity.class);
        startActivity(intent);
    }

    private void showMoodCharts() {
        Intent intent = new Intent(this, MoodChartActivity.class);
        startActivity(intent);
    }

    private void showCorrelationCharts() {
        Intent intent = new Intent(this, CorrelationChartActivity.class);
        startActivity(intent);
    }

    private void showTimeCharts() {
        Intent intent = new Intent(this, TimeChartActivity.class);
        startActivity(intent);
    }

    private void enterData() {
        Intent intent = new Intent(this, EnterDataActivity.class);
        startActivity(intent);
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void showAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
