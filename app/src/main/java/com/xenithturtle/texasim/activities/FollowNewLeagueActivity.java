package com.xenithturtle.texasim.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foound.widget.AmazingListView;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.asynctasks.AsyncTaskConstants;
import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.adapters.LeagueListAdapter;
import com.xenithturtle.texasim.models.League;
import com.xenithturtle.texasim.views.LeagueView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FollowNewLeagueActivity extends ActionBarActivity {

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
        ab.setIcon(R.drawable.ic_activity);

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
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //TODO may want to make this a separate abstract async task since it overlaps with Event list
    public class DivisionAsyncTask extends AsyncTask<String, Void, List<Pair<String, List<League>>>> {

        @Override
        public void onPreExecute() {
            mLinearLayout.setVisibility(View.GONE);
            mAmazingList.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Pair<String, List<League>>> doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();

            if (params.length != 1) {
                throw new IllegalArgumentException("Must only have one argument for eventId");
            }

            String args = "event=" + params[0];

            HttpGet get = new HttpGet(AsyncTaskConstants.IM_REQ_BASE + args);
            String response;
            try {
                response = client.execute(get, new BasicResponseHandler());
            } catch (UnsupportedEncodingException e) {
                Log.e("*********", e.toString());
                return null;
            } catch (IOException e) {
                Log.e("*********", e.toString());
                return null;
            }

            List<Pair<String, List<League>>> res = new ArrayList<Pair<String, List<League>>>();

            try {

                IMSqliteAdapter adapter = new IMSqliteAdapter(FollowNewLeagueActivity.this);
                adapter.open();
                JSONArray responseArray = new JSONArray(response);

                for (int i = 0; i < responseArray.length(); i++) {

                    JSONObject division = responseArray.getJSONObject(i);
                    String divName = division.getString(AsyncTaskConstants.DNAME);
                    JSONArray leagues = division.getJSONArray(AsyncTaskConstants.LEAGUES);
                    List<League> l = new ArrayList<League>();

                    for (int j = 0; j < leagues.length(); j++) {
                        JSONObject jsonObject = leagues.getJSONObject(j);

                        League league = new League();
                        league.mLid = jsonObject.getInt(AsyncTaskConstants.LID);
                        Log.i("***********", "" + league.mLid);
                        league.mLeagueName = jsonObject.getString(AsyncTaskConstants.LNAME);
                        league.mLeagueInfo = jsonObject.getString(AsyncTaskConstants.LINFO);
                        league.mFollowing = adapter.isFollowingLeague(league.mLid);
                        l.add(league);
                    }
                    res.add(new Pair<String, List<League>>(divName, l));
                }

                adapter.close();

            } catch (JSONException e) {
                Log.e("************", e.toString());
            }

            return res;
        }

        @Override
        public void onPostExecute(List<Pair<String, List<League>>> res) {
            if (res != null) {
                LeagueListAdapter adapter = new LeagueListAdapter(FollowNewLeagueActivity.this, res);
                mAmazingList.setAdapter(adapter);
                mProgressBar.setVisibility(View.GONE);
                mAmazingList.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.VISIBLE);
            }

        }
    }
}
