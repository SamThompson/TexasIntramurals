package com.st028.texasim.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam on 6/5/14.
 */
public class JSONArrayAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray mJsonArray;
    private final String mNameKey;

    public JSONArrayAdapter(Context c, JSONArray jsonArray, String nameKey) {
        mContext = c;
        mJsonArray = jsonArray;
        mNameKey = nameKey;
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mJsonArray.getJSONObject(position);
        } catch (JSONException e) {
            return null;
        }
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

        String name;
        try {
            name = ((JSONObject) getItem(position)).getString(mNameKey);
        } catch (JSONException e) {
            name = "";
        }

        TextView t = (TextView) convertView.findViewById(android.R.id.text1);
        t.setText(name);
        return convertView;
    }
}
