package com.xenithturtle.texasim.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.asynctasks.AsyncTaskConstants;
import com.xenithturtle.texasim.adapters.JSONTableAdapter;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TestActivity extends Activity {

    private LinearLayout mContent;
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private TableFixHeaders mTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorText = (TextView) findViewById(R.id.error_text);
        mProgressBar.setVisibility(View.VISIBLE);
        mTable = (TableFixHeaders) findViewById(R.id.table);
        mContent = (LinearLayout) findViewById(R.id.content);
        new StandingsLoader().execute("5423");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class StandingsLoader extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();

            if (params.length != 1) {
                throw new IllegalArgumentException("Must only have one argument for eventId");
            }

            String lid = params[0];

            HttpGet request = new HttpGet(AsyncTaskConstants.LEAGUES_REQ_BASE + lid);
            String response;
            try {
                response = client.execute(request, new BasicResponseHandler());
            } catch (UnsupportedEncodingException e) {
                Log.e("*********", e.toString());
                return null;
            } catch (IOException e) {
                Log.e("*********", e.toString());
                return null;
            }

            JSONObject res;
            try {
                res = new JSONObject(response);
            } catch (JSONException e) {
                return null;
            }

            return res;
        }

        @Override
        public void onPostExecute(JSONObject res) {
            if (res != null) {
                mTable.setAdapter(new JSONTableAdapter(TestActivity.this, res));
                mProgressBar.setVisibility(View.GONE);
                mContent.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
            }
        }
    }
}
