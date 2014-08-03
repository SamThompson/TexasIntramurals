package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.activities.FollowNewLeagueActivity;
import com.xenithturtle.texasim.adapters.JSONArrayAdapter;
import com.xenithturtle.texasim.asynctasks.ServerCheckAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EventListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;
    private ListView mListView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mLinearLayout = (LinearLayout) v.findViewById(R.id.no_events);
        mListView = (ListView) v.findViewById(android.R.id.list);
        (v.findViewById(R.id.try_again)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EventsAsyncTask().execute();
            }
        });
        new EventsAsyncTask().execute();
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        JSONObject ev = (JSONObject) l.getItemAtPosition(position);
        try {
            Intent i = new Intent(getActivity(), FollowNewLeagueActivity.class);
            i.putExtra("EVENT_NAME", ev.getString(ServerCheckAsyncTask.JSON_ENAME));
                i.putExtra("EVENT_ID", "" + ev.getInt(ServerCheckAsyncTask.JSON_EID));
            startActivity(i);
        } catch (JSONException e) {

        }
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

    private class EventsAsyncTask extends ServerCheckAsyncTask<Void, Void, JSONArray> {

        @Override
        protected void setUpWork() {
            mLinearLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doWork(Void ... params) throws IOException, JSONException {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(IM_REQ_BASE)
                    .build();

            //Exception would be thrown here
            String response = client.newCall(request).execute().body().string();

            //or here
            return new JSONArray(response);
        }

        @Override
        protected void finishWork(JSONArray res) {
            JSONArrayAdapter adapter =
                    new JSONArrayAdapter(EventListFragment.this.getActivity(),
                            res, JSON_ENAME);
            setListAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void errorInWork(String msg) {
            mProgressBar.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

}
