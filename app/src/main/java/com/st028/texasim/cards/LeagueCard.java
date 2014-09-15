package com.st028.texasim.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.st028.texasim.R;
import com.st028.texasim.models.League;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 5/31/14.
 */
public class LeagueCard extends Card {

    private League mLeague;

    public LeagueCard(Context c, League l) {
        super(c, R.layout.league_card);
        mLeague = l;
    }

    public League getLeague() {
        return mLeague;
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
