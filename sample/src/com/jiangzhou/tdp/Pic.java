package com.jiangzhou.tdp;

import android.content.ContentValues;
import android.net.Uri;

public class Pic {

	public final static String TABLE_NAME = "pic";

	public final static String COLUMN_ID = "_id";
	public final static String COLUMN_DEFAULT_MODE = "default_mode";
	public final static String COLUMN_DEFAULT_CATEGORY = "default_category";
	public final static String COLUMN_DEFAULT_NAME = "default_name";
	public final static String COLUMN_DEFAULT_URL = "default_url";
	public final static String COLUMN_DEFALUT_ISTITLE = "default_istitle";
	public final static Uri CONTENT_URI = Uri.parse("content://"
			+ TDPProvider.URI_AUTHORITY + "/" + TABLE_NAME);

	public long mId;
	public String mDefaultMode;
	public String mDefaultCategory;
	public String mDefaultName;
	public String mDefaultUrl;
	public String mDefaultIsTitle;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		if(mId != 0) {
			values.put(COLUMN_ID, mId);
		}
		values.put(COLUMN_DEFAULT_MODE, mDefaultMode);
		values.put(COLUMN_DEFAULT_CATEGORY, mDefaultCategory);
		values.put(COLUMN_DEFAULT_NAME, mDefaultName);
		values.put(COLUMN_DEFAULT_URL, mDefaultUrl);
		values.put(COLUMN_DEFALUT_ISTITLE, mDefaultIsTitle);
		return values;
	}
}