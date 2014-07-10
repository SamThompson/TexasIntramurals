package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import com.xenithturtle.texasim.asynctasks.LeagueAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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


    private class ScheduleLoader extends LeagueAsyncTask {

        // { days: [<days>], <day>:[], ...}
        @Override
        public void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONArray days = jsonObject.getJSONArray("days");

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 16, 8, 0);

                    //loop over the event days
                    for (int i = 0; i < days.length(); i++) {

                        String day = days.getString(i);

                        TextView dayView = (TextView) mContent.inflate(getActivity(), R.layout.day_text_view, null);
                        dayView.setLayoutParams(params);
                        dayView.setText(day);

                        mContent.addView(dayView);

                        //get the games on that day
                        JSONArray times = jsonObject.getJSONArray(day);
                        for (int j = 0; j < times.length(); j++) {

                            LinearLayout ll = (LinearLayout) mContent.inflate(getActivity(), R.layout.schedule_table, null);
                            TableLayout tableView = (TableLayout) ll.findViewById(R.id.result_table);

                            TextView timeHeader = (TextView) ll.findViewById(R.id.game_header);

                            //get the jth game
                            JSONArray game = times.getJSONArray(j);
                            timeHeader.setText(game.getJSONArray(0).getString(0));
                            for (int k = 1; k < game.length(); k++) {


                                JSONArray row = game.getJSONArray(k);
                                TableRow tableRow = new TableRow(getActivity());

                                if ((k-1) % 2 == 1) {
                                    tableRow.setBackgroundResource(R.color.table_gray);
                                } else {
                                    tableRow.setBackgroundColor(Color.WHITE);
                                }

                                for (int l = 0; l < row.length(); l++) {
                                    String text = row.getString(l);

                                    if (!text.toLowerCase().equals("no show")) {
                                        TextView t = new TextView(getActivity());
                                        t.setTextColor(Color.BLACK);
                                        t.setPadding(8, 8, 8, 8);
                                        t.setText(row.getString(l));
                                        tableRow.addView(t);
                                    }
                                }

                                tableView.addView(tableRow);
                            }
                            mContent.addView(ll);
                        }

                    }
                } catch (JSONException e) {
                    Log.i("************", "JSON exception");
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
