package com.st028.texasim.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.st028.texasim.R;


public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar ab = getActionBar();
        ab.setIcon(R.drawable.ic_activity);
    }
}
