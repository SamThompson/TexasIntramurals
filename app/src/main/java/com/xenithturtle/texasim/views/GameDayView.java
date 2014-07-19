package com.xenithturtle.texasim.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.models.Game;
import com.xenithturtle.texasim.models.GameDay;

/**
 * Created by sam on 7/18/14.
 */
public class GameDayView extends LinearLayout {

    private GameDay mGameDay;
    private TextView mGameDate;

    public GameDayView(Context context) {
        super(context);
    }

    public GameDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameDayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 16, 8, 0);

        mGameDate = (TextView) findViewById(R.id.game_date);

    }

    public void setModel(GameDay model) {
        mGameDay = model;
        bindModel();
    }

    private void bindModel() {
        mGameDate.setText(mGameDay.mDay);

        for (Game g : mGameDay.games) {
            LinearLayout game = (LinearLayout) LinearLayout.inflate(getContext(), R.layout.schedule_table, null);
            ((TextView) game.findViewById(R.id.game_time)).setText(g.mTimeLoc);

            TableRow team1 = (TableRow) game.findViewById(R.id.team_1);
            populateTable(team1, g.mTeam1);
            team1.setBackgroundColor(getResources().getColor(R.color.schedule_white));

            TableRow team2 = (TableRow) game.findViewById(R.id.team_2);
            populateTable(team2, g.mTeam2);
            team2.setBackgroundColor(getResources().getColor(R.color.schedule_gray));

            addView(game);
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
