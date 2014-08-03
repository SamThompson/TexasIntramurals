package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.asynctasks.ServerCheckAsyncTask;
import com.xenithturtle.texasim.cards.StandingsCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import it.gmariotti.cardslib.library.view.CardView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StandingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StandingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class StandingsFragment extends Fragment {

    private static final String LEAGUE_KEY = "LEAGUE_ID";

    private OnFragmentInteractionListener mListener;
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private CardView mContent;
    private int mLid;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StandingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StandingsFragment newInstance(int leagueId) {
        StandingsFragment fragment = new StandingsFragment();
        Bundle args = new Bundle();
        args.putInt(LEAGUE_KEY, leagueId);
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }
    public StandingsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_standings, container, false);
//        mWebView = (WebView) v.findViewById(R.id.webview);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mErrorText = (TextView) v.findViewById(R.id.error_text);
        mContent = (CardView) v.findViewById(R.id.content);
        mProgressBar.setVisibility(View.VISIBLE);

        new StandingsAsyncTask().execute("" + mLid, "standings");

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

    private class StandingsAsyncTask extends ServerCheckAsyncTask<String, Void, JSONObject> {

        @Override
        protected void setUpWork() {

        }

        @Override
        protected JSONObject doWork(String... params) throws IOException, JSONException {

            if (params.length != 2) {
                throw new IllegalArgumentException("Must only have one argument for eventId");
            }

            OkHttpClient client = new OkHttpClient();

            String lid = params[0];
            String req = params[1];

            Request request = new Request.Builder()
                    .url(LEAGUES_REQ_BASE + lid + LEAGUES_REQ_MID + req)
                    .build();

            String response = client.newCall(request).execute().body().string();

            return new JSONObject(response);
        }

        @Override
        protected void finishWork(JSONObject res) {
            mContent.setCard(new StandingsCard(getActivity(), res));
            mContent.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected void errorInWork(String msg) {
            mProgressBar.setVisibility(View.GONE);
            mErrorText.setVisibility(View.VISIBLE);
        }
    }

}
