package com.xenithturtle.texasim.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foound.widget.AmazingAdapter;
import com.xenithturtle.texasim.R;
import com.xenithturtle.texasim.models.League;
import com.xenithturtle.texasim.views.LeagueView;

import java.util.List;

/**
 * Created by sam on 1/3/14.
 */
public class LeagueListAdapter extends AmazingAdapter {

    private List<Pair<String, List<League>>> mAll;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public LeagueListAdapter(Context c, List<Pair<String, List<League>>> all) {
        mContext = c;
        mAll = all;
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
    public View getAmazingView(int position, View convertView, ViewGroup viewGroup) {
        League l = getItem(position);
        LeagueView view = (LeagueView) convertView;

        if (view == null)
            view = (LeagueView) mLayoutInflater.inflate(R.layout.item_league, null);

        view.setModel(l);
        return view;
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
            c += mAll.get(i).second.size();
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        int c = 0;
        for (int i = 0; i < mAll.size(); i++) {
            if (position >= c && position < c + mAll.get(i).second.size()) {
                return i;
            }
            c += mAll.get(i).second.size();
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
            res += mAll.get(i).second.size();
        }
        return res;
    }

    @Override
    public League getItem(int position) {
        int c = 0;
        for (int i = 0; i < mAll.size(); i++) {
            if (position >= c && position < c + mAll.get(i).second.size()) {
                return mAll.get(i).second.get(position - c);
            }
            c += mAll.get(i).second.size();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
