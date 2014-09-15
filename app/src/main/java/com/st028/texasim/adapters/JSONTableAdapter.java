package com.st028.texasim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import com.st028.texasim.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam on 6/11/14.
 */
public class JSONTableAdapter extends BaseTableAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private JSONArray mHeader;
    private JSONArray mData;
    private float mDensity;

    public JSONTableAdapter(Context c, JSONObject tableData) {
        mContext = c;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDensity = mContext.getResources().getDisplayMetrics().density;

        try {
            mHeader = tableData.getJSONArray("cols");
            mData = tableData.getJSONArray("data");
        } catch (JSONException e) {
            mHeader = new JSONArray();
            mData = new JSONArray();
        }
    }

    @Override
    public int getRowCount() {
        return mData.length();
    }

    @Override
    /**
     * Should return columns - 1
     */
    public int getColumnCount() {
        return mHeader.length() - 1;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        final View view;
        switch (getItemViewType(row, column)) {
            case 0:
                view = getFirstHeader(row, column, convertView, parent);
                break;
            case 1:
                view = getHeader(row, column, convertView, parent);
                break;
            case 2:
                view = getFirstBody(row, column, convertView, parent);
                break;
            case 3:
                view = getBody(row, column, convertView, parent);
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return view;
    }

    private View getFirstHeader(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_table_header_first, parent, false);
        }

        String text = "";
        try {
            text = mHeader.getString(0);
        } catch (JSONException e) {

        }

        ((TextView) convertView.findViewById(android.R.id.text1)).setText(text);
        return convertView;
    }

    private View getHeader(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_table_header, parent, false);
        }

        String text = "";
        try {
            text = mHeader.getString(column + 1);
        } catch (JSONException e) {

        }

        ((TextView) convertView.findViewById(android.R.id.text1)).setText(text);
        return convertView;
    }

    private View getFirstBody(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_table_first, parent, false);
        }

        String text = "";
        try {
            text = mData.getJSONArray(row).getString(column + 1);
        } catch (JSONException e) {

        }

        convertView.setBackgroundResource(row % 2 == 0 ? R.drawable.bg_table_color1 : R.drawable.bg_table_color2);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(text);
        return convertView;
    }

    private View getBody(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_table, parent, false);
        }

        String text = "";
        try {
            text = mData.getJSONArray(row).getString(column + 1);
        } catch (JSONException e) {

        }

        convertView.setBackgroundResource(row % 2 == 0 ? R.drawable.bg_table_color1 : R.drawable.bg_table_color2);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(text);


        return convertView;
    }

    @Override
    public int getWidth(int column) {
        return Math.round(100 * mDensity);
    }

    @Override
    public int getHeight(int row) {
        return Math.round(60 * mDensity);
    }

    @Override
    public int getItemViewType(int row, int column) {
        final int itemViewType;
        if (row == -1 && column == -1) {
            itemViewType = 0;
        } else if (row == -1) {
            itemViewType = 1;
        } else if (column == -1) {
            itemViewType = 2;
        } else {
            itemViewType = 3;
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        //TODO figure out what this does...
        //Edit: types of rows
        return 4;
    }

}

