package com.example.rakvat.iyana;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.value;


public class MoodChartActivity extends AppCompatActivity {

    private static int NUM_DAYS = 30;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    protected List<String> mTitles;
    protected ArrayList<Integer> mMoodValues;
    protected ArrayList<Integer> mTimeValues;
    protected ArrayList<Integer> mColors;
    protected ArrayList<ArrayList<Integer>> mFactorValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_chart);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        Util.setTitleBar(this, R.string.nav_mood_graphs);

        initializeData();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.mood_chart_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private void initializeData() {
        mColors = new ArrayList<Integer>();
        for (int i = 0; i < Util.COLORS.length; i++) {
            mColors.add(Util.COLORS[i]);
        }
        mTitles = FactorTitleHelper.getFactorTitles(this);
        Cursor cursor = Util.getDBData(this, NUM_DAYS);
        mMoodValues = new ArrayList<Integer>();
        mTimeValues = new ArrayList<Integer>();
        mFactorValues = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
            mFactorValues.add(new ArrayList<Integer>());
        }

        long timeReference = new Date().getTime() - 1000l * 60 * 60 * 24 * NUM_DAYS;
        long now = new Date().getTime() - timeReference;
        long t = 0;
        while(cursor.moveToNext()) {
            long timestamp = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_DATE));
            t = timestamp - timeReference;
            int value = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.COLUMN_NAME_MOOD));
            mMoodValues.add(value);
            mTimeValues.add((int)((1 - ((now - t)/(float)now)) * 255));
            for (int i = 0; i < FactorTitleHelper.MAX_FACTORS; i++) {
                if (mTitles.get(i) != null) {
                    value = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseContract.MoodEntry.FACTOR_COLUMNS[i]));
                    mFactorValues.get(i).add(value);
                }
            }
        }
        cursor.close();

        // eliminate mFactorValues with all values == 0
        for (int i = FactorTitleHelper.MAX_FACTORS - 1; i >= 0; i--) {
            int sum = 0;
            for (int j = 0; j < mFactorValues.get(i).size(); j++) {
                sum+= mFactorValues.get(i).get(j);
            }
            if (sum == 0) {
                mFactorValues.remove(i);
                mTitles.remove(i);
                mColors.remove(i);
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
    //TODO: Do I need this one?
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
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


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String title = mTitles.get(position);
            List<ArrayList<Integer>> values = new ArrayList<ArrayList<Integer>>();
            values.add(mMoodValues);
            values.add(mFactorValues.get(position));
            values.add(mTimeValues);
            return ChartFragment.newInstance(values, title, mColors.get(position));
        }

        @Override
        public int getCount() {
            return mFactorValues.size();
        }
    }
}
