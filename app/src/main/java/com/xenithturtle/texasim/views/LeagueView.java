package com.xenithturtle.texasim.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.models.League;

/**
 * Created by sam on 7/17/14.
 */
public class LeagueView extends RelativeLayout {

    public League mLeague;
    private TextView mLeagueName;
    private TextView mLeagueInfo;
    private ImageView mStar;

    public LeagueView(Context context) {
        super(context);
    }

    public LeagueView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeagueView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        mLeagueName = (TextView) findViewById(R.id.league_name);
        mLeagueInfo = (TextView) findViewById(R.id.league_info);
        mStar = (ImageView) findViewById(R.id.star);
        mStar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(getContext());
                sqliteAdapter.open();

                String toastText;
                boolean following = false;

                if (mLeague.mFollowing) {
                    sqliteAdapter.deleteLeague(mLeague.mLid);
                    mStar.setImageResource(R.drawable.ic_rating_not_important);
                    toastText = "Unfollowing league";
                } else {
                    if (sqliteAdapter.insertLeague(mLeague.mLid)) {
                        mStar.setImageResource(R.drawable.ic_rating_important);
                        toastText = "Following league";
                        following = true;
                    } else {
                        toastText = "You can\'t follow more than " + IMSqliteAdapter.LEAGUE_LIMITS + " leagues";
                    }
                }

                Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
                mLeague.mFollowing = following;
                sqliteAdapter.close();
            }
        });
    }

    public void setModel(League model) {
        mLeague = model;
        bindModel();
    }

    public void updateState(boolean following) {
        mLeague.mFollowing = following;
        setStarIcon();
    }


    private void bindModel() {
        mLeagueName.setText(mLeague.mLeagueName);
        mLeagueInfo.setText(mLeague.mLeagueInfo);
        setStarIcon();
    }

    private void setStarIcon() {
        if (mLeague.mFollowing) {
            mStar.setImageResource(R.drawable.ic_rating_important);
        } else {
            mStar.setImageResource(R.drawable.ic_rating_not_important);
        }
    }
}

