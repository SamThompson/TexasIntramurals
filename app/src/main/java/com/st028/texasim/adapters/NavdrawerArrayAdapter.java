package com.st028.texasim.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.st028.texasim.R;

/**
 * Created by sam on 7/27/14.
 */
public class NavdrawerArrayAdapter extends ArrayAdapter<String> {

    private boolean mFirstClick = false;

    public NavdrawerArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public void clicked() {
        mFirstClick = true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (position != getCount() - 1) {
            convertView = super.getView(position, convertView, parent);
            if (position == 0 && !mFirstClick) {
                ((TextView) convertView).setTypeface(null, Typeface.BOLD);
            }
        } else {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.navdrawer_settings, null);
            }
        }
        return convertView;
    }
}
