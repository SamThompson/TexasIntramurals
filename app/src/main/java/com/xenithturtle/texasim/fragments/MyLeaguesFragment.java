package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.qustom.dialog.QustomDialogBuilder;
import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.asynctasks.AsyncTaskConstants;
import com.xenithturtle.texasim.cards.LeagueCard;
import com.xenithturtle.texasim.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


/**
 * @author Sam
 *
 */
public class MyLeaguesFragment extends Fragment {

    private OnFollowButtonPressedListener mListener;
    private boolean mStatusOk = false;
    private ProgressBar mProgressBar;
    private LinearLayout mNoLeaguesLayout;
    private LinearLayout mErrorLayout;
    private CardListView mCardListView;

    public static MyLeaguesFragment newInstance() {
        MyLeaguesFragment fragment = new MyLeaguesFragment();
        return fragment;
    }
    public MyLeaguesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ServerAsyncTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_my_leagues, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mNoLeaguesLayout = (LinearLayout) v.findViewById(R.id.no_leagues);
        mErrorLayout = (LinearLayout) v.findViewById(R.id.error_layout);
        mCardListView = (CardListView) v.findViewById(R.id.cardlist);

        //set follow button onclick
        (v.findViewById(R.id.follow_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFollowButtonPressed();
            }
        });

        //set try again onclick
        (v.findViewById(R.id.try_again)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardListView.setVisibility(View.GONE);
                mNoLeaguesLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                new ServerAsyncTask().execute();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFollowButtonPressedListener) activity;
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

    public void setContentView() {

        //TODO: for now do this here, but move cardCreation to oncreate later
        IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(getActivity());
        sqliteAdapter.open();
        ArrayList<Card> cards = new ArrayList<Card>();
        //add cards here
        List<Integer> followingLeagues = sqliteAdapter.getFollowingLeagues();
        if (followingLeagues.size() > 0) {
            for (Integer i : followingLeagues) {
                LeagueCard l = new LeagueCard(getActivity(), i);
                cards.add(l);
            }
            sqliteAdapter.close();

            CardArrayAdapter ca = new CardArrayAdapter(getActivity(), cards);
            mCardListView.setAdapter(ca);
            mProgressBar.setVisibility(View.GONE);
            mCardListView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mNoLeaguesLayout.setVisibility(View.VISIBLE);
        }

    }

    public void setErrorView(Pair<Boolean, String> res) {
        mProgressBar.setVisibility(View.GONE);
        mCardListView.setVisibility(View.GONE);
        mNoLeaguesLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);

        if (res != null && !res.first) {
            QustomDialogBuilder builder = new QustomDialogBuilder(getActivity());
            builder.setTitle("Message from server");
            builder.setMessage(res.second + "\n");
            builder.setDividerColorResource(R.color.card_orange);
            builder.setTitleColorResource(android.R.color.black);
            builder.setNegativeButton("Close" , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
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
    public interface OnFollowButtonPressedListener {
        // TODO: Update argument type and name
        public void onFollowButtonPressed();
    }


    public class ServerAsyncTask extends AsyncTask<Void, Void, Pair<Boolean, String>> {

        @Override
        public void onPreExecute() {
        }

        @Override
        public Pair<Boolean, String> doInBackground(Void ... params) {
            HttpClient client = new DefaultHttpClient();

            HttpGet get = new HttpGet(AsyncTaskConstants.SERVER_STATUS);
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

            JSONObject res;
            try {
                res = new JSONObject(response);
                boolean status = res.getBoolean(AsyncTaskConstants.STATUS);
                String message = res.getString(AsyncTaskConstants.MESSAGE);
                return new Pair<Boolean, String>(status, message);
            } catch (JSONException e) {
                Log.e("*********", e.toString());
                return null;
            }

        }

        @Override
        public void onPostExecute(Pair<Boolean, String> res) {
            if (res != null) {
                if (res.first) {
                    setContentView();
                } else {
                    setErrorView(res);
                }
            } else {
                setErrorView(res);
            }
        }
    }

}
