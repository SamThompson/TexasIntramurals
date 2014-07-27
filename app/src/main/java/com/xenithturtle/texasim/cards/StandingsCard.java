package com.xenithturtle.texasim.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.adapters.JSONTableAdapter;

import org.json.JSONObject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 7/26/14.
 */
public class StandingsCard extends Card {

    JSONTableAdapter mAdapt;

    public StandingsCard(Context context, JSONObject tableData) {
        super(context, R.layout.standings_card);

        if (tableData.length() > 0)
            mAdapt = new JSONTableAdapter(getContext(), tableData);
        else
            mAdapt = null;
    }

    public void setupInnerViewElements(ViewGroup parent, View view) {
        bindDataToCard(parent);
    }

    private void bindDataToCard(ViewGroup parent) {
        if (mAdapt != null) {
            TableFixHeaders t = (TableFixHeaders) parent.findViewById(R.id.table);
            t.setAdapter(mAdapt);
            t.setVisibility(View.VISIBLE);
        } else {
            (parent.findViewById(R.id.no_standings)).setVisibility(View.VISIBLE);
        }
    }
}
