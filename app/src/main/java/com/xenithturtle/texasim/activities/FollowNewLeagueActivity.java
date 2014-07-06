package com.xenithturtle.texasim.activities;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foound.widget.AmazingListView;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.asynctasks.AsyncTaskConstants;
import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.adapters.LeagueListAdapter;

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
    private TextView mErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_new_league);
        Bundle extras = getIntent().getExtras();
        String eventId = extras.getString("EVENT_ID");
        String name = extras.getString("EVENT_NAME");
        setTitle(name);
//        ph.initialize();

        Log.i("*********", "event id: " + eventId);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setIcon(R.drawable.ic_activity);

        mAmazingList = (AmazingListView) findViewById(R.id.amazingList);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorText = (TextView) findViewById(R.id.error_text);
        mProgressBar.setVisibility(View.VISIBLE);

        new DivisionAsyncTask().execute(eventId);
        mAmazingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject j = (JSONObject) parent.getItemAtPosition(position);

                try {
                    int lid = j.getInt(AsyncTaskConstants.LID);
                    Toast.makeText(FollowNewLeagueActivity.this, "" + lid, Toast.LENGTH_SHORT).show();
                    IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(FollowNewLeagueActivity.this);
                    sqliteAdapter.open();

                    if (sqliteAdapter.isFollowingLeague(lid)) {
                        sqliteAdapter.deleteLeague(lid);
                        ImageView imageView = (ImageView) view.findViewById(R.id.star);
                        imageView.setImageResource(R.drawable.ic_rating_not_important);
                    } else {
                        sqliteAdapter.insertLeague(lid);
                        ImageView imageView = (ImageView) view.findViewById(R.id.star);
                        imageView.setImageResource(R.drawable.ic_rating_important);
                    }
                    sqliteAdapter.close();
                } catch (JSONException e) {
                }
            }
        });
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
    public class DivisionAsyncTask extends AsyncTask<String, Void, List<Pair<String, JSONArray>>> {

        @Override
        public void onPreExecute() {}

        @Override
        protected List<Pair<String, JSONArray>> doInBackground(String... params) {
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

            List<Pair<String, JSONArray>> res = new ArrayList<Pair<String, JSONArray>>();

            try {

                JSONArray responseArray = new JSONArray(response);
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject division = responseArray.getJSONObject(i);
                    String divName = division.getString(AsyncTaskConstants.DNAME);
                    JSONArray leagues = division.getJSONArray(AsyncTaskConstants.LEAGUES);
                    res.add(new Pair<String, JSONArray>(divName, leagues));
                }

            } catch (JSONException e) {
                Log.e("************", e.toString());
                return null;
            }

            return res;
        }

        @Override
        public void onPostExecute(List<Pair<String, JSONArray>> res) {
            if (res != null) {
                LeagueListAdapter adapter = new LeagueListAdapter(FollowNewLeagueActivity.this, res, AsyncTaskConstants.LNAME);
                mAmazingList.setAdapter(adapter);
                mProgressBar.setVisibility(View.GONE);
                mAmazingList.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
            }

        }
    }
}
