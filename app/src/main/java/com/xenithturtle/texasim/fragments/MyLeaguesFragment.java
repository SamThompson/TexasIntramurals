package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.xenithturtle.texasim.activities.ViewLeagueActivity;
import com.xenithturtle.texasim.adapters.CardArrayAdapterWithSpace;
import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.asynctasks.ServerCheckAsyncTask;
import com.xenithturtle.texasim.cards.LeagueCard;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.models.League;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.dismissanimation.SwipeDismissAnimation;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;


/**
 * @author Sam
 *
 */
public class MyLeaguesFragment extends Fragment {

    private OnFollowButtonPressedListener mListener;
    private ProgressBar mProgressBar;
    private LinearLayout mNoLeaguesLayout;
    private LinearLayout mErrorLayout;
    private CardListView mCardListView;
    private SwipeDismissAnimation mDismissAnimation;

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
        mDismissAnimation = new SwipeDismissAnimation(getActivity());
        new MyLeaguesAsyncTask().execute();

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
                new MyLeaguesAsyncTask().execute();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ViewLeagueActivity.TRACK_CHANGES) {
            if (resultCode == Activity.RESULT_OK) {
                boolean following = data.getBooleanExtra(ViewLeagueActivity.FOLLOWING_KEY, false);
                int listIndex = data.getIntExtra(ViewLeagueActivity.LIST_INDEX_KEY, -1);

                if (listIndex >= 0) {

                    //getchildat only gets the views currently in view, not whole layout
                    //problem stemmed from using getAmazingView incorrectly
                    //unfortunately this is how we need to update the star
                    int transIndex = listIndex - mCardListView.getFirstVisiblePosition() - mCardListView.getHeaderViewsCount();
                    CardView v = (CardView) mCardListView.getChildAt(transIndex);

                    if (v != null && !following) {
                        Card lc = v.getCard();
                        mDismissAnimation.animateDismiss(lc);

                        if (mCardListView.getCount() == 1) {
                            mCardListView.setVisibility(View.GONE);
                            mNoLeaguesLayout.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }
        }
    }

    private class LeagueCardClickedListener implements Card.OnCardClickListener {

        @Override
        public void onClick(Card card, View view) {
            Intent i = new Intent(getActivity(), ViewLeagueActivity.class);
            i.putExtra(ViewLeagueActivity.NAME_KEY, ((LeagueCard) card).getLeague().mLeagueName);
            i.putExtra(ViewLeagueActivity.LID_KEY, ((LeagueCard) card).getLeague().mLid);
            i.putExtra(ViewLeagueActivity.JUST_LOOKING_KEY, true);
            startActivityForResult(i, ViewLeagueActivity.TRACK_CHANGES);
        }
    }


    private class MyLeaguesAsyncTask
            extends ServerCheckAsyncTask<Void, Void, List<Card>> {

        @Override
        protected void setUpWork() {
            mProgressBar.setVisibility(View.VISIBLE);
            mCardListView.setVisibility(View.GONE);
            mNoLeaguesLayout.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.GONE);
        }

        /**
         * Gets the leagues the user is following and returns a list of
         * cards to put in an adapter. This method does not throw an exception
         * when parsing the json object so that we can check if the object is
         * still supported
         *
         * @return List<Card> -  the cards to put in the adapter
         */
        @Override
        protected List<Card> doWork(Void ... params) throws IOException {
            IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(getActivity());

            sqliteAdapter.open();
            List<Integer> followingLeagues = sqliteAdapter.getFollowingLeagues();
            sqliteAdapter.close();

            OkHttpClient client = new OkHttpClient();

            List<Card> leagueCards = new ArrayList<Card>();
            for (Integer i : followingLeagues) {

                Request request = new Request.Builder()
                        .url(LEAGUES_REQ_BASE + "" + i + LEAGUES_REQ_MID + "info")
                        .build();

                //This statement throws the IOE exception if server not there, etc
                String response = client.newCall(request).execute().body().string();

                JSONObject res;
                try {
                    res = new JSONObject(response);

                    League l = new League();
                    l.mLid = i;
                    l.mLeagueName = res.getString(JSON_LR_NAME);
                    l.mDivisionName = res.getString(JSON_LR_DIVISION);
                    l.mLeagueInfo = res.getString(JSON_LR_TIME);
                    l.mUpdatedAt = res.getString(JSON_LR_UPDATE);
                    l.mSport = res.getString(JSON_LR_SPORT);

                    LeagueCard lc = new LeagueCard(getActivity(), l);
                    lc.setOnClickListener(new LeagueCardClickedListener());

                    leagueCards.add(lc);

                } catch (JSONException e) {
                    //remove this from the db, it is no longer supported
                    //because it returned a malformed json query
                    sqliteAdapter.open();
                    sqliteAdapter.deleteLeague(i);
                    sqliteAdapter.close();
                }
            }

            return leagueCards;
        }

        @Override
        protected void finishWork(List<Card> res) {
            if (res.size() > 0) {
                CardArrayAdapterWithSpace ca = new CardArrayAdapterWithSpace(getActivity(), res);
                mDismissAnimation.setup(ca);
                mCardListView.setAdapter(ca);
                mProgressBar.setVisibility(View.GONE);
                mCardListView.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mNoLeaguesLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void errorInWork(String msg) {
            mProgressBar.setVisibility(View.GONE);
            mCardListView.setVisibility(View.GONE);
            mNoLeaguesLayout.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        }
    }

}
