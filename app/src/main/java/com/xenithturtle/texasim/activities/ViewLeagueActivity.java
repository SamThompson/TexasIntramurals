package com.xenithturtle.texasim.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.fragments.ScheduleFragment;
import com.xenithturtle.texasim.fragments.StandingsFragment;
import com.xenithturtle.texasim.adapters.FragmentAdapter;
import com.xenithturtle.texasim.views.NoSwipeViewPager;

public class ViewLeagueActivity extends BaseActivity
        implements ScheduleFragment.OnFragmentInteractionListener, StandingsFragment.OnFragmentInteractionListener {

    public static final int TRACK_CHANGES = 0;

    public static final String NAME_KEY = "NAME";
    public static final String LID_KEY = "LID";
    public static final String JUST_LOOKING_KEY = "JUST_LOOKING";
    public static final String LIST_INDEX_KEY = "LIST_INDEX";
    public static final String FOLLOWING_KEY = "";

    public static final int FOLLOW_INDEX = 0;

    private NoSwipeViewPager mPager;
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
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.setTitle(mName);

        mPager = (NoSwipeViewPager) findViewById(R.id.pager);
        mAdapter = new FragmentAdapter<Fragment>(getSupportFragmentManager());
        mAdapter.addFragments(StandingsFragment.newInstance(mLid));
        mAdapter.addFragments(ScheduleFragment.newInstance(mLid));

        mPager.setAdapter(mAdapter);

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
        getMenuInflater().inflate(R.menu.view_league_looking, menu);
        MenuItem followAction = menu.getItem(FOLLOW_INDEX);
        if (!mFollowing) {
            followAction.setIcon(R.drawable.ic_rating_unimportant_light);
            followAction.setTitle(R.string.action_follow);
        } else {
            followAction.setIcon(R.drawable.ic_rating_important_light);
            followAction.setTitle(R.string.action_unfollow);
        }

        if (mJustLooking) {
        } else {
        //    getMenuInflater().inflate(R.menu.view_league, menu);
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
            case R.id.action_follow:

                //TODO think about relocating this
                IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(this);
                sqliteAdapter.open();

                String toastText;
                if (!sqliteAdapter.isFollowingLeague(mLid)) {
                    sqliteAdapter.insertLeague(mLid);
                    item.setIcon(R.drawable.ic_rating_important_light);
                    toastText = "Following league";
                    item.setTitle(R.string.action_unfollow);
                    mFollowing = true;
                } else {
                    sqliteAdapter.deleteLeague(mLid);
                    item.setIcon(R.drawable.ic_rating_unimportant_light);
                    toastText = "Unfollowing league";
                    item.setTitle(R.string.action_follow);
                    mFollowing = false;
                }

                mResult.putExtra(FOLLOWING_KEY, mFollowing);
                sqliteAdapter.close();

                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
