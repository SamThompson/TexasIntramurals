package com.xenithturtle.texasim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xenithturtle.texasim.R;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by sam on 7/26/14.
 */
public class CardArrayAdapterWithSpace extends CardArrayAdapter {


    public CardArrayAdapterWithSpace(Context context, List<Card> cards) {
        super(context, cards);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        CardView mCardView;
        Card mCard;

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Retrieve card from items
        mCard = (Card) getItem(position);
        if (mCard != null) {

            int layout = mRowLayoutId;
            boolean recycle = false;

            //Inflate layout
            if (view == null) {
                recycle = false;
                view = mInflater.inflate(layout, parent, false);
            } else {
                recycle = true;
            }

            //Setup card
            mCardView = (CardView) view.findViewById(R.id.list_cardId);
            if (mCardView != null) {
//                if (position == 0) {
//                    float scale = getContext().getResources().getDisplayMetrics().density;
//                    int dpAsPixels = (int) (8 * scale + 0.5f);
//                    mCardView.setPadding(mCardView.getPaddingLeft(), dpAsPixels, mCardView.getPaddingRight(), mCardView.getPaddingBottom());
//                }

                //It is important to set recycle value for inner layout elements
                mCardView.setForceReplaceInnerLayout(Card.equalsInnerLayout(mCardView.getCard(),mCard));

                //It is important to set recycle value for performance issue
                mCardView.setRecycle(recycle);

                //Save original swipeable to prevent cardSwipeListener (listView requires another cardSwipeListener)
                boolean origianlSwipeable = mCard.isSwipeable();
                mCard.setSwipeable(false);

                mCardView.setCard(mCard);

                //Set originalValue
                mCard.setSwipeable(origianlSwipeable);

                //If card has an expandable button override animation
                if ((mCard.getCardHeader() != null && mCard.getCardHeader().isButtonExpandVisible()) || mCard.getViewToClickToExpand()!=null ){
                    setupExpandCollapseListAnimation(mCardView);
                }

                //Setup swipeable animation
                setupSwipeableAnimation(mCard, mCardView);

                //setupMultiChoice
                setupMultichoice(view,mCard,mCardView,position);
            }
        }

        return view;
    }
}
