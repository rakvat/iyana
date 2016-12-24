package com.example.rakvat.iyana;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.R.attr.defaultValue;

public class EnterDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        setFactorTitles();
    }

    public void setFactorTitles() {
        int view_ids[] = {R.id.data_label0, R.id.data_label9};
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.factor_storage_key),
                        Context.MODE_PRIVATE);
        for (int i = 0; i < view_ids.length; i++) {
            String factor_title = sharedPref.getString(getString(R.string.factor_storage_key) + i, "");
            if (factor_title.length() > 0) {
                TextView textView = (TextView) findViewById(view_ids[i]);
                textView.setText(factor_title);
            }
        }
    }
}
