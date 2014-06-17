package com.xenithturtle.texasim.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by sam on 6/15/14.
 */
public abstract class LeagueAsyncTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();

        if (params.length != 2) {
            throw new IllegalArgumentException("Must only have one argument for eventId");
        }

        String lid = params[0];
        String req = params[1];

        HttpGet request = new HttpGet(AsyncTaskConstants.LEAGUES_REQ_BASE + lid +
                AsyncTaskConstants.LEAGUES_REQ_MID + req);
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
    public abstract void onPostExecute(JSONObject j);

}
