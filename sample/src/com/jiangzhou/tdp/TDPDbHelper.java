package com.jiangzhou.tdp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TDPDbHelper extends SQLiteOpenHelper {

	public TDPDbHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public TDPDbHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("qiqi", "start create tables");
		db.execSQL("CREATE TABLE " + Page.TABLE_NAME + "("
				+ Page.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Page.COLUMN_DEFAULT_NAME + " TEXT,"
				+ Page.COLUMN_DEFAULT_URL + " TEXT,"
				+ Page.COLUMN_DEFAULT_CTIME + " TEXT"
				+ ")");
		
		db.execSQL("CREATE TABLE " + Pic.TABLE_NAME + "("
				+ Pic.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Pic.COLUMN_DEFAULT_MODE + " TEXT,"
				+ Pic.COLUMN_DEFAULT_CATEGORY + " TEXT,"
				+ Pic.COLUMN_DEFAULT_NAME + " TEXT,"
				+ Pic.COLUMN_DEFAULT_URL + " TEXT"
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}