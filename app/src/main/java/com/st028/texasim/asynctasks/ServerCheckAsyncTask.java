package com.st028.texasim.asynctasks;

import android.os.AsyncTask;
import android.util.Pair;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.st028.texasim.miscclasses.Triple;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by sam on 7/25/14.
 */
public abstract class ServerCheckAsyncTask<Q, M, R>
            extends AsyncTask<Q, M, Triple<Boolean, ServerCheckAsyncTask.SERVER_RESPONSES, R>> {

    public static final String SERVER_BASE = "http://www.cs.utexas.edu/~st028/cgi-bin/";

    public static final String SERVER_STATUS = SERVER_BASE + "server_status.scgi";

    public static final String IM_REQ_BASE =
            SERVER_BASE + "im_data_request.scgi?";

    public static final String LEAGUES_REQ_BASE =
            SERVER_BASE + "league_data_request.scgi?event=";

    public static final String LEAGUES_REQ_MID = "&request=";

    //JSON constants
    public static final String JSON_EID = "event_id";
    public static final String JSON_ENAME = "event_name";
    public static final String JSON_DID = "division_id";
    public static final String JSON_DNAME = "division_name";
    public static final String JSON_LID = "league_id";
    public static final String JSON_LNAME = "league_name";
    public static final String JSON_LINFO = "league_info";
    public static final String JSON_LEAGUES = "leagues";
    public static final String JSON_STATUS = "status_ok";
    public static final String JSON_MESSAGE = "message";

    //Deals with league requests
    public static final String JSON_LR_NAME = "name";
    public static final String JSON_LR_DIVISION = "division";
    public static final String JSON_LR_TIME = "play_time";
    public static final String JSON_LR_UPDATE = "update_time";
    public static final String JSON_LR_SPORT = "sport";

    static enum SERVER_RESPONSES {NO_CONNECTION, IOEXCEPTION, MALFORMED_RESPONSE, SERVER_DOWN, SERVER_UP};

    @Override
    protected void onPreExecute() {
        setUpWork();
    }

    @Override
    protected Triple<Boolean, SERVER_RESPONSES, R> doInBackground(Q ... objects) {
        Pair<Boolean, SERVER_RESPONSES> server = checkServer();
        try {
            R res = doWork(objects);

            return new Triple<Boolean, SERVER_RESPONSES, R>(server.first, server.second, res);
        } catch (Exception e) {
            SERVER_RESPONSES resp;
            if (e instanceof JSONException) {
                resp = SERVER_RESPONSES.MALFORMED_RESPONSE;
            } else if (e instanceof IOException) {
                resp = SERVER_RESPONSES.IOEXCEPTION;
            } else {
                resp = SERVER_RESPONSES.NO_CONNECTION;
            }

            return new Triple<Boolean, SERVER_RESPONSES, R>(false, resp, null);
        }
    }

    @Override
    protected void onPostExecute(Triple<Boolean, SERVER_RESPONSES, R> result) {
        if (result.first) {
            finishWork(result.third);
        } else {
             errorInWork(translateServerMessage(result.second));
        }
    }

    protected abstract void setUpWork();

    /**
     * Throws an exception if the process encountered an error.
     * This allows errorInWork() to be called.
     *
     * @return R - result of the operation
     * @throws Exception
     */
    protected abstract R doWork(Q ... params) throws Exception;
    protected abstract void finishWork(R res);
    protected abstract void errorInWork(String msg);

    private Pair<Boolean, SERVER_RESPONSES> checkServer() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SERVER_STATUS)
                .build();

//        HttpGet get = new HttpGet(SERVER_STATUS);

        String response;
        try {
            response = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            return new Pair<Boolean, SERVER_RESPONSES>(false, SERVER_RESPONSES.IOEXCEPTION);
        }

        JSONObject res;
        try {
            res = new JSONObject(response);
            boolean status = res.getBoolean(JSON_STATUS);
            SERVER_RESPONSES serverResponse;
            if (status)
                serverResponse = SERVER_RESPONSES.SERVER_UP;
            else
                serverResponse = SERVER_RESPONSES.SERVER_DOWN;

            return new Pair<Boolean, SERVER_RESPONSES>(status, serverResponse);
        } catch (JSONException e) {
            return new Pair<Boolean, SERVER_RESPONSES>(false, SERVER_RESPONSES.MALFORMED_RESPONSE);
        }
    }

    private String translateServerMessage(SERVER_RESPONSES s) {
        if (s == SERVER_RESPONSES.NO_CONNECTION) {
            return "No connection to server";
        } else if (s == SERVER_RESPONSES.IOEXCEPTION) {
            return "Error receiving server response";
        } else if (s == SERVER_RESPONSES.MALFORMED_RESPONSE) {
            return "Server returned a malformed response";
        } else if (s == SERVER_RESPONSES.SERVER_DOWN) {
            return "Server is down for maintenance";
        } else {
            return "";
        }

    }

}
