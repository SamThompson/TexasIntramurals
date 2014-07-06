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
import com.xenithturtle.texasim.asynctasks.LeagueAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 5/31/14.
 */
public class LeagueCard extends Card {

    private enum QueryStatus {PENDING, SUCCESS, FAIL}

    private String mLeagueName = "";
    private String mDivisionName = "";
    private String mLeagueInfo = "";
    private String mLastUpdate = "";
    private QueryStatus mQueryStatus;

    public LeagueCard(Context c, int leagueId) {
        super(c, R.layout.league_card);
        setOnClickListener(new LeagueCardClickedListener());
        new LeagueCardAsyncTask().execute("5423", "info");
        mQueryStatus = QueryStatus.PENDING;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        switch (mQueryStatus) {
            case PENDING:
                //probably want to do nothing here
                break;
            case SUCCESS:
                TextView lName = (TextView) parent.findViewById(R.id.card_league_name);
                lName.setText(mLeagueName);
                TextView dName = (TextView) parent.findViewById(R.id.card_division_name);
                dName.setText(mDivisionName);
                TextView lInfo = (TextView) parent.findViewById(R.id.card_play_time);
                lInfo.setText(mLeagueInfo);
                TextView lastupdate = (TextView) parent.findViewById(R.id.card_last_update);
                lastupdate.setText("Last update: " + mLastUpdate);
                ProgressBar pb = (ProgressBar) parent.findViewById(R.id.card_spinner);
                pb.setVisibility(View.GONE);
                RelativeLayout r = (RelativeLayout) parent.findViewById(R.id.card_content);
                r.setVisibility(View.VISIBLE);
                break;
            case FAIL:
                //show a message in view
                pb = (ProgressBar) parent.findViewById(R.id.card_spinner);
                pb.setVisibility(View.GONE);
                break;
        }
    }

    private class LeagueCardClickedListener implements OnCardClickListener {

        @Override
        public void onClick(Card card, View view) {
            Intent i = new Intent(mContext, ViewLeagueActivity.class);
            mContext.startActivity(i);
        }
    }



    private class LeagueCardAsyncTask extends LeagueAsyncTask {

        @Override
        public void onPostExecute(JSONObject j) {
            if (j != null) {
                try {
                    //TODO use constants
                    mLeagueName = j.getString("name");
                    mDivisionName = j.getString("division");
                    mLeagueInfo = j.getString("play_time");
                    mLastUpdate = j.getString("update_time");
                    mQueryStatus = QueryStatus.SUCCESS;

                    //Need to check if the cardview is null b/c of off screen views
                    if (getCardView() != null) {
                        TextView lName = (TextView) getCardView().findViewById(R.id.card_league_name);
                        lName.setText(mLeagueName);
                        TextView dName = (TextView) getCardView().findViewById(R.id.card_division_name);
                        dName.setText(mDivisionName);
                        TextView lInfo = (TextView) getCardView().findViewById(R.id.card_play_time);
                        lInfo.setText(mLeagueInfo);
                        TextView lastupdate = (TextView) getCardView().findViewById(R.id.card_last_update);
                        lastupdate.setText("Last update: " + mLastUpdate);
                        ProgressBar pb = (ProgressBar) getCardView().findViewById(R.id.card_spinner);
                        pb.setVisibility(View.GONE);
                        RelativeLayout r = (RelativeLayout) getCardView().findViewById(R.id.card_content);
                        r.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    Log.i("***********", "Bad JSON params");
                    mQueryStatus = QueryStatus.FAIL;
                    ProgressBar pb = (ProgressBar) getCardView().findViewById(R.id.card_spinner);
                    pb.setVisibility(View.GONE);
                }
            } else {
                mQueryStatus = QueryStatus.FAIL;
                Log.i("***********", "Request returns null");
                ProgressBar pb = (ProgressBar) getCardView().findViewById(R.id.card_spinner);
                pb.setVisibility(View.GONE);
            }
        }
    }
}
