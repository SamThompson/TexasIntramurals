package com.xenithturtle.texasim.fragments;

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

import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.asynctasks.LeagueAsyncTask;
import com.xenithturtle.texasim.adapters.JSONTableAdapter;

import org.json.JSONObject;


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
    private LinearLayout mContent;
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private TableFixHeaders mTable;
    private LinearLayout mInfo;
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
        mProgressBar.setVisibility(View.VISIBLE);
        mTable = (TableFixHeaders) v.findViewById(R.id.table);
        mContent = (LinearLayout) v.findViewById(R.id.content);
        mInfo = (LinearLayout) v.findViewById(R.id.info);
        new StandingsLoader().execute("" + mLid, "standings");

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


    private class StandingsLoader extends LeagueAsyncTask {

        @Override
        public void onPostExecute(JSONObject res) {
            if (res != null) {
                if (res.length() > 0) {
                    mTable.setAdapter(new JSONTableAdapter(getActivity(), res));
                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView t = new TextView(getActivity());
                    t.setLayoutParams(params);
                    t.setText("test");
                    TextView t2 = new TextView(getActivity());
                    t2.setLayoutParams(params);
                    t2.setText("test2");
                    mInfo.addView(t);
                    mInfo.addView(t2);
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
