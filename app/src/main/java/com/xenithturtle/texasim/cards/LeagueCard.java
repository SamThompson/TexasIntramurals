package com.xenithturtle.texasim.cards;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.activities.ViewLeagueActivity;
import com.xenithturtle.texasim.asynctasks.AsyncTaskConstants;
import com.xenithturtle.texasim.asynctasks.LeagueAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;


import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 5/31/14.
 */
public class LeagueCard extends Card {

    private enum QueryStatus {PENDING, SUCCESS, FAIL}

    private int mLid;
    private String mLeagueName = "";
    private String mDivisionName = "";
    private String mLeagueInfo = "";
    private String mLastUpdate = "";
    private String mSport = "";
    private QueryStatus mQueryStatus;

    public LeagueCard(Context c, int leagueId) {
        super(c, R.layout.league_card);
        mLid = leagueId;
        setOnClickListener(new LeagueCardClickedListener());
        new LeagueCardAsyncTask().execute("" + mLid, "info");
        mQueryStatus = QueryStatus.PENDING;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        switch (mQueryStatus) {
            case PENDING:
                //probably want to do nothing here
                parent.findViewById(R.id.could_not_load).setVisibility(View.GONE);
                parent.findViewById(R.id.card_content).setVisibility(View.GONE);
                parent.findViewById(R.id.card_spinner).setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                parent.findViewById(R.id.could_not_load).setVisibility(View.GONE);
                bindDataToCard(parent);
                ProgressBar pb = (ProgressBar) parent.findViewById(R.id.card_spinner);
                pb.setVisibility(View.GONE);
                RelativeLayout r = (RelativeLayout) parent.findViewById(R.id.card_content);
                r.setVisibility(View.VISIBLE);
                break;
            case FAIL:
                //show a message in view
                //todo retry a certain number of times
                parent.findViewById(R.id.could_not_load).setVisibility(View.VISIBLE);
                pb = (ProgressBar) parent.findViewById(R.id.card_spinner);
                pb.setVisibility(View.GONE);
                break;
        }
    }

    private void bindDataToCard(ViewGroup parent) {
        TextView lName = (TextView) parent.findViewById(R.id.card_league_name);
        lName.setText(mLeagueName);
        TextView dName = (TextView) parent.findViewById(R.id.card_division_name);
        dName.setText(mDivisionName);
        TextView lInfo = (TextView) parent.findViewById(R.id.card_play_time);
        lInfo.setText(mLeagueInfo);
        TextView lastupdate = (TextView) parent.findViewById(R.id.card_last_update);
        lastupdate.setText("Last update: " + mLastUpdate);
        TextView sportName = (TextView) parent.findViewById(R.id.sport_icon);
        sportName.setText(mSport.toUpperCase());
    }

    private class LeagueCardClickedListener implements OnCardClickListener {

        @Override
        public void onClick(Card card, View view) {
            if (mQueryStatus == QueryStatus.SUCCESS) {
                Intent i = new Intent(mContext, ViewLeagueActivity.class);
                i.putExtra(ViewLeagueActivity.NAME_KEY, mLeagueName);
                i.putExtra(ViewLeagueActivity.LID_KEY, mLid);
                i.putExtra(ViewLeagueActivity.JUST_LOOKING_KEY, false);
                mContext.startActivity(i);
            }
        }
    }



    private class LeagueCardAsyncTask extends LeagueAsyncTask {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(JSONObject j) {
            if (j != null) {
                try {
                    mLeagueName = j.getString(AsyncTaskConstants.NAME);
                    mDivisionName = j.getString(AsyncTaskConstants.DIVISION);
                    mLeagueInfo = j.getString(AsyncTaskConstants.TIME);
                    mLastUpdate = j.getString(AsyncTaskConstants.UPDATE);
                    mSport = j.getString(AsyncTaskConstants.SPORT);
                    mQueryStatus = QueryStatus.SUCCESS;

                    //Need to check if the cardview is null b/c of off screen views
                    if (getCardView() != null) {
                        bindDataToCard(getCardView());
                        ProgressBar pb = (ProgressBar) getCardView().findViewById(R.id.card_spinner);
                        pb.setVisibility(View.GONE);
                        RelativeLayout r = (RelativeLayout) getCardView().findViewById(R.id.card_content);
                        r.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    Log.i("***********", "Bad JSON params");
                    mQueryStatus = QueryStatus.FAIL;
//                    getCardView().findViewById(R.id.could_not_load).setVisibility(View.VISIBLE);
//                    ProgressBar pb = (ProgressBar) getCardView().findViewById(R.id.card_spinner);
//                    pb.setVisibility(View.GONE);
                }
            } else {
                mQueryStatus = QueryStatus.FAIL;
                Log.i("***********", "Request returns null");
                getCardView().findViewById(R.id.could_not_load).setVisibility(View.VISIBLE);
                ProgressBar pb = (ProgressBar) getCardView().findViewById(R.id.card_spinner);
                pb.setVisibility(View.GONE);
            }
        }
    }
}
