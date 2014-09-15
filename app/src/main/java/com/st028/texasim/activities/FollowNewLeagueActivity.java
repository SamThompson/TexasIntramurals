package com.st028.texasim.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.foound.widget.AmazingListView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.st028.texasim.R;
import com.st028.texasim.adapters.IMSqliteAdapter;
import com.st028.texasim.adapters.LeagueListAdapter;
import com.st028.texasim.asynctasks.ServerCheckAsyncTask;
import com.st028.texasim.models.League;
import com.st028.texasim.views.LeagueView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FollowNewLeagueActivity extends BaseActivity {

    private AmazingListView mAmazingList;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_new_league);
        Bundle extras = getIntent().getExtras();
        final String eventId = extras.getString("EVENT_ID");
        String name = extras.getString("EVENT_NAME");
        setTitle(name);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mAmazingList = (AmazingListView) findViewById(R.id.amazingList);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLinearLayout = (LinearLayout) findViewById(R.id.no_leagues);
        (findViewById(R.id.try_again)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DivisionAsyncTask().execute(eventId);
            }
        });

        new DivisionAsyncTask().execute(eventId);

        mAmazingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                League l = (League) parent.getItemAtPosition(position);

                int lid = l.mLid;
                String name = l.mLeagueName;
                Intent i = new Intent(FollowNewLeagueActivity.this, ViewLeagueActivity.class);
                i.putExtra(ViewLeagueActivity.NAME_KEY, name);
                i.putExtra(ViewLeagueActivity.LID_KEY, lid);
                i.putExtra(ViewLeagueActivity.JUST_LOOKING_KEY, true);
                i.putExtra(ViewLeagueActivity.LIST_INDEX_KEY, position);
                startActivityForResult(i, ViewLeagueActivity.TRACK_CHANGES);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ViewLeagueActivity.TRACK_CHANGES) {
            if (resultCode == RESULT_OK) {
                boolean following = data.getBooleanExtra(ViewLeagueActivity.FOLLOWING_KEY, false);
                int listIndex = data.getIntExtra(ViewLeagueActivity.LIST_INDEX_KEY, -1);

                if (listIndex >= 0) {

                    //getchildat only gets the views currently in view, not whole layout
                    //problem stemmed from using getAmazingView incorrectly
                    //unfortunately this is how we need to update the star
                    int transIndex = listIndex - mAmazingList.getFirstVisiblePosition() - mAmazingList.getHeaderViewsCount();
                    LeagueView v = (LeagueView) mAmazingList.getChildAt(transIndex);

                    if (v != null) {
                        v.updateState(following);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.follow_new_league, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private class DivisionAsyncTask extends ServerCheckAsyncTask<String, Void, List<Pair<String, List<League>>>> {

        @Override
        protected void setUpWork() {
            mLinearLayout.setVisibility(View.GONE);
            mAmazingList.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Pair<String, List<League>>> doWork(String... params) throws IOException, JSONException {

            if (params.length != 1) {
                throw new IllegalArgumentException("Must only have one argument for eventId");
            }

            OkHttpClient client = new OkHttpClient();

            String args = "event=" + params[0];

            Request request = new Request.Builder()
                    .url(IM_REQ_BASE + args)
                    .build();

            //IOException
            String response = client.newCall(request).execute().body().string();

            List<Pair<String, List<League>>> res = new ArrayList<Pair<String, List<League>>>();

            IMSqliteAdapter adapter = new IMSqliteAdapter(FollowNewLeagueActivity.this);
            adapter.open();
            JSONArray responseArray = new JSONArray(response);

            for (int i = 0; i < responseArray.length(); i++) {

                JSONObject division = responseArray.getJSONObject(i);
                String divName = division.getString(JSON_DNAME);
                JSONArray leagues = division.getJSONArray(JSON_LEAGUES);
                List<League> l = new ArrayList<League>();

                for (int j = 0; j < leagues.length(); j++) {
                    JSONObject jsonObject = leagues.getJSONObject(j);

                    League league = new League();
                    league.mLid = jsonObject.getInt(JSON_LID);
                    league.mLeagueName = jsonObject.getString(JSON_LNAME);
                    league.mLeagueInfo = jsonObject.getString(JSON_LINFO);
                    league.mFollowing = adapter.isFollowingLeague(league.mLid);
                    l.add(league);
                }
                res.add(new Pair<String, List<League>>(divName, l));
            }

            adapter.close();

            return res;
        }

        @Override
        protected void finishWork(List<Pair<String, List<League>>> res) {
            LeagueListAdapter adapter = new LeagueListAdapter(FollowNewLeagueActivity.this, res);
            mAmazingList.setAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
            mAmazingList.setVisibility(View.VISIBLE);
        }

        @Override
        protected void errorInWork(String msg) {
            mProgressBar.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

}
