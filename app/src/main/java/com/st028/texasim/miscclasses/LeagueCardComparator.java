package com.st028.texasim.miscclasses;

import com.st028.texasim.cards.LeagueCard;

import java.util.Comparator;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 8/9/14.
 */
public class LeagueCardComparator implements Comparator<Card> {

    @Override
    public int compare(Card a, Card b) {
        if (a instanceof LeagueCard && b instanceof LeagueCard) {
            LeagueCard leagueCardA = (LeagueCard) a;
            LeagueCard leagueCardB = (LeagueCard) b;

            int leagueDiff = leagueCardA.getLeague().mSport.compareTo(leagueCardB.getLeague().mSport);

            if (leagueDiff == 0) {
                int divDiff = leagueCardA.getLeague().mDivisionName.compareTo(leagueCardB.getLeague().mDivisionName);

                if (divDiff == 0) {
                    return leagueCardA.getLeague().mLeagueName.compareTo(leagueCardB.getLeague().mLeagueName);
                }

                return divDiff;
            }
            return leagueDiff;
        } else {
            return -1;
        }
    }
}
