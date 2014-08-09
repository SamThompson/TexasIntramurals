package com.xenithturtle.texasim.cards;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 8/8/14.
 */
public abstract class ComparableCard extends Card implements Comparable<Card> {

    public ComparableCard(Context context) {
        super(context);
    }

    public ComparableCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }
}
