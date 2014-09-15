package com.st028.texasim.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.st028.texasim.R;


public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar ab = getSupportActionBar();
        ab.setIcon(R.drawable.ic_activity);
    }
}
