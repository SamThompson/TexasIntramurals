package com.st028.texasim.cards;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.st028.texasim.R;
import com.st028.texasim.models.Game;
import com.st028.texasim.models.GameDay;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by sam on 7/26/14.
 */
public class ScheduleCard extends Card {

    private final List<GameDay> mGameDays;

    public ScheduleCard(Context c, List<GameDay> gds) {
        super(c, R.layout.schedule_card);
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

        if (mGameDays.size() > 0) {
            for (GameDay gd : mGameDays) {
                LinearLayout gameDay = (LinearLayout) inflater.inflate(R.layout.game_day_view, parent, false);
                ((TextView) gameDay.findViewById(R.id.game_date)).setText(gd.mDay);

                for (Game g : gd.games) {
                    LinearLayout game = (LinearLayout) inflater.inflate(R.layout.schedule_table, parent, false);

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
        } else {
            (parent.findViewById(R.id.no_schedule)).setVisibility(View.VISIBLE);
        }

    }

    private void populateTable(TableRow team, String[] row) {
        for (String s : row) {
            if (!s.toLowerCase().equals("no show")) {
                TextView t = new TextView(getContext());
                t.setTextColor(Color.BLACK);
                t.setPadding(8, 8, 8, 8);
                if (s.length() <= 20) {
                    t.setText(s);
                } else {
                    t.setText(s.substring(0, 21) + "...");
                }
                team.addView(t);
            }
        }
    }
}
