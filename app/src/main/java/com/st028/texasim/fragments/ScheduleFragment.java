package com.st028.texasim.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.st028.texasim.R;
import com.st028.texasim.asynctasks.ServerCheckAsyncTask;
import com.st028.texasim.cards.ScheduleCard;
import com.st028.texasim.models.Game;
import com.st028.texasim.models.GameDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.view.CardView;


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
    private CardView mContent;
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
        fragment.setRetainInstance(true);
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
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mErrorText = (TextView) v.findViewById(R.id.error_text);
        mProgressBar.setVisibility(View.VISIBLE);
        mContent = (CardView) v.findViewById(R.id.content);
        new ScheduleAsyncTask().execute("" + mLid, "schedule");
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

    private class ScheduleAsyncTask extends ServerCheckAsyncTask<String, Void, ScheduleCard> {

        @Override
        protected void setUpWork() {

        }

        @Override
        protected ScheduleCard doWork(String ... params) throws IOException, JSONException {

            if (params.length != 2) {
                throw new IllegalArgumentException("Must only have one argument for eventId");
            }

            OkHttpClient client = new OkHttpClient();

            String lid = params[0];
            String req = params[1];

            Request request = new Request.Builder()
                    .url(LEAGUES_REQ_BASE + lid + LEAGUES_REQ_MID + req)
                    .build();

            //IOException thrown here
            String response = client.newCall(request).execute().body().string();

            JSONArray gameDays = new JSONArray(response);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 16, 8, 0);

            //loop over the event days
            List<GameDay> gds = new ArrayList<GameDay>();
            for (int i = 0; i < gameDays.length(); i++) {

                JSONObject gameDay = gameDays.getJSONObject(i);
                GameDay gD = new GameDay();
                gD.mDay = gameDay.getString("day");
                gD.games = new ArrayList<Game>();
                JSONArray games = gameDay.getJSONArray("games");

                for (int j = 0; j < games.length(); j++) {
                    Game g = new Game();
                    JSONObject game = games.getJSONObject(j);
                    JSONArray timeLoc = game.getJSONArray("time_loc");
                    g.mTimeLoc = timeLoc.getString(0);
                    g.mTeam1 = toStringArray(game.getJSONArray("team_1"));
                    g.mTeam2 = toStringArray(game.getJSONArray("team_2"));
                    gD.games.add(g);
                }

                gds.add(gD);
            }

            return new ScheduleCard(getActivity(), gds);
        }

        private String[] toStringArray(JSONArray j) throws JSONException {
            String[] res = new String[j.length()];
            for (int i = 0; i < j.length(); i++) {
                res[i] = j.getString(i);
            }

            return res;
        }

        @Override
        protected void finishWork(ScheduleCard res) {
            mContent.setCard(res);
            mProgressBar.setVisibility(View.GONE);
            mContent.setVisibility(View.VISIBLE);
        }

        @Override
        protected void errorInWork(String msg) {
            mProgressBar.setVisibility(View.GONE);
            mErrorText.setVisibility(View.VISIBLE);
        }
    }

}
