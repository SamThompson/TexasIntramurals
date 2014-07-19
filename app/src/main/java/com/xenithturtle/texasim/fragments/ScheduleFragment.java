package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.asynctasks.AsyncTaskConstants;
import com.xenithturtle.texasim.asynctasks.LeagueAsyncTask;
import com.xenithturtle.texasim.models.Game;
import com.xenithturtle.texasim.models.GameDay;
import com.xenithturtle.texasim.views.GameDayView;

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
import java.util.Arrays;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ScheduleFragment extends Fragment {

    private static final String LEAGUE_KEY = "LEAGUE_ID";

    private OnFragmentInteractionListener mListener;
    private LinearLayout mContent;
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private int mLid;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(int leagueId) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(LEAGUE_KEY, leagueId);
        fragment.setArguments(args);
        return fragment;
    }
    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mLid = args.getInt(LEAGUE_KEY);
        } else {
            mLid = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);
//        mWebView = (WebView) v.findViewById(R.id.webview);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mErrorText = (TextView) v.findViewById(R.id.error_text);
        mProgressBar.setVisibility(View.VISIBLE);
        mContent = (LinearLayout) v.findViewById(R.id.content);
        new ScheduleLoader().execute("" + mLid, "schedule");
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    private class ScheduleLoader extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
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

            JSONArray res;
            try {
                res = new JSONArray(response);
            } catch (JSONException e) {
                return null;
            }

            return res;
        }

        private String[] toStringArray(JSONArray j) throws JSONException {
            String[] res = new String[j.length()];
            for (int i = 0; i < j.length(); i++) {
                res[i] = j.getString(i);
            }

            return res;
        }

        @Override
        public void onPostExecute(JSONArray gameDays) {
            if (gameDays != null) {
                try {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 16, 8, 0);

                    //loop over the event days
                    Log.i("************", "1");
                    for (int i = 0; i < gameDays.length(); i++) {

                        JSONObject gameDay = gameDays.getJSONObject(i);
                        GameDay gD = new GameDay();
                        gD.mDay = gameDay.getString("day");
                        Log.i("************", gD.mDay);
                        gD.games = new ArrayList<Game>();
                        JSONArray games = gameDay.getJSONArray("games");
                        Log.i("************", "2");

                        for (int j = 0; j < games.length(); j++) {
                            Game g = new Game();
                            JSONObject game = games.getJSONObject(j);
                            JSONArray timeLoc = game.getJSONArray("time_loc");
                            Log.i("************", "3");
                            g.mTimeLoc = timeLoc.getString(0);
                            Log.i("************", "4");
                            g.mTeam1 = toStringArray(game.getJSONArray("team_1"));
                            g.mTeam2 = toStringArray(game.getJSONArray("team_2"));
                            Log.i("***********", Arrays.toString(g.mTeam1));
                            Log.i("***********", Arrays.toString(g.mTeam2));
                            gD.games.add(g);
                        }

                        GameDayView gdv = (GameDayView) LinearLayout.inflate(getActivity(), R.layout.day_text_view, null);
                        gdv.setModel(gD);
                        mContent.addView(gdv);
                    }
                } catch (JSONException e) {
                    Log.i("************", "JSON exception in on post execute");
                }
                mProgressBar.setVisibility(View.GONE);
                mContent.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
            }
        }
    }

}
