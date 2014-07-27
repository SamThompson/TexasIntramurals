package com.xenithturtle.texasim.cards;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.models.Game;
import com.xenithturtle.texasim.models.GameDay;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 7/26/14.
 */
public class ScheduleCard extends Card {

    private List<GameDay> mGameDays;

    public ScheduleCard(Context c, List<GameDay> gds) {
        super(c, R.layout.linear_layout_card);
        mGameDays = gds;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        bindDataToCard(parent);
    }

    //TODO optimize this using merge...
    private void bindDataToCard(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout root = (LinearLayout) parent.findViewById(R.id.card_content);

        for (int i = 0; i < mGameDays.size(); i++) {
            GameDay gd = mGameDays.get(i);
            LinearLayout gameDay = (LinearLayout) inflater.inflate(R.layout.game_day_view, null, false);
            ((TextView) gameDay.findViewById(R.id.game_date)).setText(gd.mDay);

            for (Game g : gd.games) {
                Log.i("********** timeloc", g.mTimeLoc);
                LinearLayout game = (LinearLayout) inflater.inflate(R.layout.schedule_table, null, false);

                ((TextView) game.findViewById(R.id.game_time)).setText(g.mTimeLoc);

                TableRow team1 = (TableRow) game.findViewById(R.id.team_1);
                populateTable(team1, g.mTeam1);
                team1.setBackgroundColor(parent.getResources().getColor(R.color.schedule_white));

                TableRow team2 = (TableRow) game.findViewById(R.id.team_2);
                populateTable(team2, g.mTeam2);
                team2.setBackgroundColor(parent.getResources().getColor(R.color.schedule_gray));

                gameDay.addView(game);
            }
            root.addView(gameDay);
        }

    }

    private void populateTable(TableRow team, String[] row) {
        for (String s : row) {
            if (!s.toLowerCase().equals("no show")) {
                TextView t = new TextView(getContext());
                t.setTextColor(Color.BLACK);
                t.setPadding(8, 8, 8, 8);
                t.setText(s);
                team.addView(t);
            }
        }
    }
}
