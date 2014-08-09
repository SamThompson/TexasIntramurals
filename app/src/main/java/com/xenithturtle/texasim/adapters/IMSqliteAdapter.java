package com.xenithturtle.texasim.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 6/14/14.
 */
public class IMSqliteAdapter {

    private static final String DB_NAME = "texas_im_user_data";
    private static final int DB_VER = 2;
    private static final String LEAGUES_TABLE = "leagues";

    private static final String CREATE_LEAGUES_TABLE = "create table leagues (" +
            "lid integer primary key" +
            ");";

    private static final String[] LEAGUES_FIELDS = new String[] {"lid"};

    private final Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mSqliteDB;

    public IMSqliteAdapter(Context c) {
        mContext = c;
        mDBHelper = new DBHelper(mContext);
    }

    public IMSqliteAdapter open() throws SQLiteException {
        mSqliteDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDBHelper.close();
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context c) {
            super(c, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_LEAGUES_TABLE);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists leagues");
        }
    }

    public boolean insertLeague(int lid) {
        ContentValues cv = new ContentValues();
        cv.put(LEAGUES_FIELDS[0], lid);
        return mSqliteDB.insert(LEAGUES_TABLE, null, cv) > 0;
    }

    public boolean deleteLeague(int lid) {
        return mSqliteDB.delete(LEAGUES_TABLE, "lid=?", new String[] {"" + lid}) > 0;
    }

    public boolean isFollowingLeague(int lid) {
        Cursor c = mSqliteDB.query(LEAGUES_TABLE, LEAGUES_FIELDS, "lid=?", new String[] {"" + lid},
                null, null, null, null);
        boolean res = c.moveToFirst();
        c.close();
        return res;
    }

    public List<Integer> getFollowingLeagues() {
        List<Integer> res = new ArrayList<Integer>();
        Cursor c = mSqliteDB.query(LEAGUES_TABLE, LEAGUES_FIELDS, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                res.add(new Integer(c.getInt(0)));
            } while (c.moveToNext());
        }
        c.close();

        return res;
    }
}
