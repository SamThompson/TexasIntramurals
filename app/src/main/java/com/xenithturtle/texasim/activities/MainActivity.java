package com.xenithturtle.texasim.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.adapters.NavdrawerArrayAdapter;
import com.xenithturtle.texasim.fragments.EventListFragment;
import com.xenithturtle.texasim.fragments.MyLeaguesFragment;


public class MainActivity extends BaseActivity implements MyLeaguesFragment.OnFollowButtonPressedListener,
        EventListFragment.OnFragmentInteractionListener {

    private static final int MY_LEAGUES = 0;
    private static final int FOLLOW_LEAGUE = 1;
    private static final int FIELD_CONDITIONS = -1;
    private static final int SETTINGS = 2;

    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mDrawerIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new NavdrawerArrayAdapter(this,
                R.layout.drawer_item, mDrawerTitles));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerClickListener());


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        selectItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        switch(position) {
            case MY_LEAGUES:
                mDrawerIndex = position;
                switchFragments(new MyLeaguesFragment());
                break;
            case FOLLOW_LEAGUE:
                mDrawerIndex = position;
                switchFragments(new EventListFragment());
                break;
            case FIELD_CONDITIONS:
                mDrawerIndex = position;
                switchFragments(new Fragment());
                break;
            case SETTINGS:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            default:
                //to make the compiler happy
                break;

        }

        mDrawerList.setItemChecked(mDrawerIndex, true);
        setTitle(mDrawerTitles[mDrawerIndex]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void switchFragments(Fragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, f)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFollowButtonPressed() {
        mDrawerList.performItemClick(null, FOLLOW_LEAGUE, 0);
    }

    private class DrawerClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position != mDrawerIndex) {
                //in order to set the default state for bolding on open, I need to set an instance
                //variable that says that the first element has been clicked
                ((NavdrawerArrayAdapter) mDrawerList.getAdapter()).clicked();

                int prevIndex = mDrawerIndex;
                selectItem(position);
                if (position != prevIndex && position != SETTINGS) {
                    ((TextView) mDrawerList.getChildAt(prevIndex)).setTypeface(null, Typeface.NORMAL);
                    ((TextView) mDrawerList.getChildAt(position)).setTypeface(null, Typeface.BOLD);
                }
            }
        }

    }

}
