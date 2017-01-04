package com.example.rakvat.iyana;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class HelpActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        Util.setTitleBar(this, R.string.nav_help);
    }
}
