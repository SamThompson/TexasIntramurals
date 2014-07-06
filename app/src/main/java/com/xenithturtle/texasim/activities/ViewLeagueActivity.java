package com.xenithturtle.texasim.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.fragments.ScheduleFragment;
import com.xenithturtle.texasim.fragments.StandingsFragment;
import com.xenithturtle.texasim.adapters.FragmentAdapter;

public class ViewLeagueActivity extends ActionBarActivity
        implements ScheduleFragment.OnFragmentInteractionListener, StandingsFragment.OnFragmentInteractionListener {

    private ViewPager mPager;
    private FragmentAdapter<Fragment> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_league);

        final ActionBar ab = getSupportActionBar();
        ab.setIcon(R.drawable.ic_activity);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //ab.setTitle()

        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new FragmentAdapter<Fragment>(getSupportFragmentManager());
        mAdapter.addFragments(StandingsFragment.newInstance());
        mAdapter.addFragments(ScheduleFragment.newInstance());

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                ab.setSelectedNavigationItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        ab.addTab(ab.newTab().setText("League standings").setTabListener(tabListener));
        ab.addTab(ab.newTab().setText("Schedule and scores").setTabListener(tabListener));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_league, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
