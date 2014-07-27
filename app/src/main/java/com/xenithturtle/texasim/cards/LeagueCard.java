package com.xenithturtle.texasim.cards;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.activities.ViewLeagueActivity;
import com.xenithturtle.texasim.models.League;


import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 5/31/14.
 */
public class LeagueCard extends Card {

    private League mLeague;

    public LeagueCard(Context c, League l) {
        super(c, R.layout.league_card);
        mLeague = l;
        setOnClickListener(new LeagueCardClickedListener());
    }

    private class LeagueCardClickedListener implements OnCardClickListener {

        @Override
        public void onClick(Card card, View view) {
            Intent i = new Intent(mContext, ViewLeagueActivity.class);
            i.putExtra(ViewLeagueActivity.NAME_KEY, mLeague.mLeagueName);
            i.putExtra(ViewLeagueActivity.LID_KEY, mLeague.mLid);
            i.putExtra(ViewLeagueActivity.JUST_LOOKING_KEY, false);
            mContext.startActivity(i);
        }
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        bindDataToCard(parent);
    }

    private void bindDataToCard(ViewGroup parent) {
        TextView lName = (TextView) parent.findViewById(R.id.card_league_name);
        lName.setText(mLeague.mLeagueName);
        TextView dName = (TextView) parent.findViewById(R.id.card_division_name);
        dName.setText(mLeague.mDivisionName);
        TextView lInfo = (TextView) parent.findViewById(R.id.card_play_time);
        lInfo.setText(mLeague.mLeagueInfo);
        TextView lastupdate = (TextView) parent.findViewById(R.id.card_last_update);
        lastupdate.setText("Last update: " + mLeague.mUpdatedAt);
        TextView sportName = (TextView) parent.findViewById(R.id.sport_icon);
        sportName.setText(mLeague.mSport.toUpperCase());
    }

}
