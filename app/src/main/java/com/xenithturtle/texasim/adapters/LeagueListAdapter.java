package com.xenithturtle.texasim.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foound.widget.AmazingAdapter;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.asynctasks.AsyncTaskConstants;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sam on 1/3/14.
 */
public class LeagueListAdapter extends AmazingAdapter {

    private List<Pair<String, JSONArray>> mAll;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String mNameKey;

    public LeagueListAdapter(Context c, List<Pair<String, JSONArray>> all, String nameString) {
        mContext = c;
        mAll = all;
        mNameKey = nameString;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected View getLoadingView(ViewGroup viewGroup) {
        return null;
    }

    @Override
    protected void onNextPageRequested(int i) {

    }

    @Override
    protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
        if (displaySectionHeader) {
            view.findViewById(R.id.header).setVisibility(View.VISIBLE);
            TextView lSectionTitle = (TextView) view.findViewById(R.id.header);
            lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
        } else {
            view.findViewById(R.id.header).setVisibility(View.GONE);
        }
    }

    @Override
    public View getAmazingView(final int position, View convertView, ViewGroup viewGroup) {
        View res = convertView;

        if (res == null)
            res = mLayoutInflater.inflate(R.layout.item_league, null, false);

        TextView leagueName = (TextView) res.findViewById(R.id.league_name);
//        TextView leagueInfo = (TextView) res.findViewById(R.id.league_info);
        final ImageView star = (ImageView) res.findViewById(R.id.star);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject j = getItem(position);

                try {
                    int lid = j.getInt(AsyncTaskConstants.LID);
                    Toast.makeText(mContext, "" + lid, Toast.LENGTH_SHORT).show();
                    IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(mContext);
                    sqliteAdapter.open();

                    if (sqliteAdapter.isFollowingLeague(lid)) {
                        sqliteAdapter.deleteLeague(lid);
                        star.setImageResource(R.drawable.ic_rating_not_important);
                    } else {
                        sqliteAdapter.insertLeague(lid);
                        star.setImageResource(R.drawable.ic_rating_important);
                    }
                    sqliteAdapter.close();
                } catch (JSONException e) {
                }
            }
        });

        JSONObject l = getItem(position);
        String name;
        try {
            name = l.getString(mNameKey);
            name = "Test league";
            int lid = l.getInt(AsyncTaskConstants.LID);

            IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(mContext);
            sqliteAdapter.open();

            if (!sqliteAdapter.isFollowingLeague(lid)) {
                star.setImageResource(R.drawable.ic_rating_not_important);
            } else {
                star.setImageResource(R.drawable.ic_rating_important);
            }

            sqliteAdapter.close();
        } catch (JSONException e) {
            name = "";
        }

        leagueName.setText(name);
//        leagueInfo.setText(l.getInfo());

        return res;
    }

    @Override
    public void configurePinnedHeader(View header, int position, int alpha) {
        TextView lSectionHeader = (TextView) header;
        lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
    }

    @Override
    public int getPositionForSection(int section) {
        if (section < 0) section = 0;
        if (section >= mAll.size()) section = mAll.size() - 1;
        int c = 0;
        for (int i = 0; i < mAll.size(); i++) {
            if (section == i) {
                return c;
            }
            c += mAll.get(i).second.length();
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        int c = 0;
        for (int i = 0; i < mAll.size(); i++) {
            if (position >= c && position < c + mAll.get(i).second.length()) {
                return i;
            }
            c += mAll.get(i).second.length();
        }
        return 0;
    }

    @Override
    public String[] getSections() {
        String[] res = new String[mAll.size()];
        for (int i = 0; i < mAll.size(); i++) {
            res[i] = mAll.get(i).first;
        }
        return res;
    }

    @Override
    public int getCount() {
        int res = 0;
        for (int i = 0; i < mAll.size(); i++) {
            res += mAll.get(i).second.length();
        }
        return res;
    }

    @Override
    public JSONObject getItem(int position) {
        int c = 0;
        for (int i = 0; i < mAll.size(); i++) {
            if (position >= c && position < c + mAll.get(i).second.length()) {
                try {
                    return mAll.get(i).second.getJSONObject(position - c);
                } catch (JSONException e) {
                    return null;
                }
            }
            c += mAll.get(i).second.length();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
