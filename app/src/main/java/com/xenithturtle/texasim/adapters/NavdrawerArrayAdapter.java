package com.xenithturtle.texasim.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.xenithturtle.texasim.R;

import java.util.List;

/**
 * Created by sam on 7/27/14.
 */
public class NavdrawerArrayAdapter extends ArrayAdapter<String> {

    public NavdrawerArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public NavdrawerArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public NavdrawerArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public NavdrawerArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public NavdrawerArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public NavdrawerArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (position != getCount() - 1) {
            return super.getView(position, convertView, parent);
        } else {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.navdrawer_settings, null);
            }

            return convertView;
        }
    }
}
