package com.xenithturtle.texasim.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.fragments.ScheduleFragment;
import com.xenithturtle.texasim.fragments.StandingsFragment;
import com.xenithturtle.texasim.adapters.FragmentAdapter;

public class ViewLeagueActivity extends ActionBarActivity
        implements ScheduleFragment.OnFragmentInteractionListener, StandingsFragment.OnFragmentInteractionListener {

    public static final int TRACK_CHANGES = 0;

    public static final String NAME_KEY = "NAME";
    public static final String LID_KEY = "LID";
    public static final String JUST_LOOKING_KEY = "JUST_LOOKING";
    public static final String LIST_INDEX_KEY = "LIST_INDEX";
    public static final String FOLLOWING_KEY = "";

    public static final int FOLLOW_INDEX = 0;

    private ViewPager mPager;
    private FragmentAdapter<Fragment> mAdapter;

    private Intent mResult;

    private String mName;
    private int mLid;
    private boolean mJustLooking;
    private int mListIndex;
    private boolean mFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_league);

        Bundle extras = getIntent().getExtras();
        mName = extras.getString(NAME_KEY);
        mLid = extras.getInt(LID_KEY);
        mJustLooking = extras.getBoolean(JUST_LOOKING_KEY);
        mListIndex = extras.getInt(LIST_INDEX_KEY);

        IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(this);
        sqliteAdapter.open();

        mFollowing = sqliteAdapter.isFollowingLeague(mLid);

        sqliteAdapter.close();

        mResult = new Intent();
        mResult.putExtra(LIST_INDEX_KEY, mListIndex);
        mResult.putExtra(FOLLOWING_KEY, mFollowing);

        final ActionBar ab = getSupportActionBar();
        ab.setIcon(R.drawable.ic_activity);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.setTitle(mName);

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
    public void onBackPressed() {
        setResult(RESULT_OK, mResult);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mJustLooking) {
            getMenuInflater().inflate(R.menu.view_league_looking, menu);

            MenuItem followAction = menu.getItem(FOLLOW_INDEX);

            if (!mFollowing) {
                followAction.setIcon(R.drawable.ic_rating_not_important);
                followAction.setTitle(R.string.action_follow);
            } else {
                followAction.setIcon(R.drawable.ic_rating_important);
                followAction.setTitle(R.string.action_unfollow);
            }

        } else {
            getMenuInflater().inflate(R.menu.view_league, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                return true;
            case R.id.action_follow:

                IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(this);
                sqliteAdapter.open();

                if (!sqliteAdapter.isFollowingLeague(mLid)) {
                    sqliteAdapter.insertLeague(mLid);
                    item.setIcon(R.drawable.ic_rating_important);
                    item.setTitle(R.string.action_unfollow);
                    mFollowing = true;
                } else {
                    sqliteAdapter.deleteLeague(mLid);
                    item.setIcon(R.drawable.ic_rating_not_important);
                    item.setTitle(R.string.action_follow);
                    mFollowing = false;
                }

                mResult.putExtra(FOLLOWING_KEY, mFollowing);
                sqliteAdapter.close();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
