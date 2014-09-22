package com.st028.texasim.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.st028.texasim.models.Event;

import java.util.List;


/**
 * Created by sam on 6/5/14.
 */
public class EventAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Event> mEventList;

    public EventAdapter(Context c, List<Event> eList) {
        mContext = c;
        mEventList = eList;
    }

    @Override
    public int getCount() {
        return mEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, android.R.layout.simple_list_item_1, null);
        }

        String name = ((Event) getItem(position)).name;

        TextView t = (TextView) convertView.findViewById(android.R.id.text1);
        t.setText(name);
        return convertView;
    }
}
