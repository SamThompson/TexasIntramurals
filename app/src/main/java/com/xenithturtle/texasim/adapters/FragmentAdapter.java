package com.xenithturtle.texasim.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by sam on 5/31/14.
 */
public class FragmentAdapter<T extends Fragment> extends FragmentPagerAdapter {

    private ArrayList<T> mFragments;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<T>();
    }

    @Override
    public T getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void addFragments(T frag) {
        mFragments.add(frag);
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).toString();
    }
}
