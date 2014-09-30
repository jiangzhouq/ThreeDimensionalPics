package com.jiangzhou.tdp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
public class ModeChoiceActivity extends Activity implements OnClickListener{
	
	private View mDecorView;
	DisplayImageOptions options;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				LinearLayout menus = (LinearLayout) findViewById(R.id.menus);
				menus.setVisibility(View.VISIBLE);
				setListener();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDecorView = getWindow().getDecorView();
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			setContentView(R.layout.mc_landscape);
		}else{
			setContentView(R.layout.mc_portrait);
		}
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.resetViewBeforeLoading(true)
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
//		hideSystemUI();
		ImageView bg = (ImageView) findViewById(R.id.bg);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage("http://vrfessor.com/mybackground.jpg",bg, options);
		
//		Log.d("qiqi", "TDPProvider start onCreate");
//		Cursor mCursor = getContentResolver().query(Page.CONTENT_URI, null, null, null, null);
//		ContentValues value = new ContentValues();
//		value.put(Page.COLUMN_DEFAULT_NAME, "nimabi");
//		value.put(Page.COLUMN_DEFAULT_URL, "zhangwanglitishidai");
//		value.put(Page.COLUMN_DEFAULT_CTIME, "20140901");
//		getContentResolver().insert(Page.CONTENT_URI, value);
		
		startGetInfo();
//		LinearLayout menus = (LinearLayout) findViewById(R.id.menus);
//		menus.setVisibility(View.VISIBLE);
//		setListener();
	}
	private void setListener(){
		Button view1 = (Button) findViewById(R.id.honglan);
		view1.setOnClickListener(this);
		Button view2 = (Button) findViewById(R.id.zuoyou);
		view2.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this,CategoryActivity.class);
		switch(v.getId()){
		case R.id.honglan:
			intent.putExtra(Constants.MODE_CHOICE_NAME, Constants.MODE_CHOLICE_HONGLAN);
			startActivity(intent);
			break;
		case R.id.zuoyou:
			intent.putExtra(Constants.MODE_CHOICE_NAME, Constants.MODE_CHOLICE_ZUOYOU);
			startActivity(intent);
			break;
		}
	}
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//			if (Constants.LOG_ENABLE) {
//				Log.d("qiqi", "横屏");
//			}
//			setContentView(R.layout.mc_landscape);
//		}else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//			if (Constants.LOG_ENABLE) {
//				Log.d("qiqi", "竖屏");
//			}
//			setContentView(R.layout.mc_portrait);
//		}
//		setListener();
//	}
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//	        super.onWindowFocusChanged(hasFocus);
//	    if (hasFocus) {
//	    	mDecorView.setSystemUiVisibility(
//	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//	                | View.SYSTEM_UI_FLAG_FULLSCREEN
//	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
//	}
	// This snippet hides the system bars.
//	private void hideSystemUI() {
//	    // Set the IMMERSIVE flag.
//	    // Set the content to appear under the system bars so that the content
//	    // doesn't resize when the system bars hide and show.
//	    mDecorView.setSystemUiVisibility(
//	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//	            | View.SYSTEM_UI_FLAG_IMMERSIVE);
//	}

	// This snippet shows the system bars. It does this by removing all the flags
	// except for the ones that make the content appear under the system bars.
	private void showSystemUI() {
	    mDecorView.setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}
	Runnable getRun = new Runnable() {
		@Override
		public void run() {
			DefaultHttpClient client = new DefaultHttpClient();
			/** NameValuePair是传送给服务器的请求参数 param.get("name") **/
			/** 新建一个get请求 **/
			HttpGet getTime = new HttpGet("http://vrfessor.com:12341/?action=1");
			HttpGet getCategory = new HttpGet("http://vrfessor.com:12341/?action=2");
			HttpGet getPics = new HttpGet("http://vrfessor.com:12341/?action=3");
			HttpResponse response = null;
			String strResult = "";
			try {
				response = client.execute(getTime);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response.getStatusLine().getStatusCode() == 200) {
				try {
					strResult = EntityUtils.toString(response.getEntity());
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				JSONObject jsonObject1 = null;
				try {
					/** 把json字符串转换成json对象 **/
					jsonObject1 = getJSON(strResult);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String names = "";
				String category = "";
				String mode = "";
				String url = "";
				
				try {
					//date
					String data3 = jsonObject1.getString("time");
					JSONArray timeArray = new JSONArray(data3);
					SharedPreferences sharedData = getSharedPreferences("qiqi", Context.MODE_PRIVATE);
					String oDate = sharedData.getString("get_time", "");
//					Log.d("qiqi", "timeArray:" + timeArray );
					String nDate = ((JSONArray) timeArray.get(0)).getString(1);
//					Log.d("qiqi", " nDate:" + nDate);
					if(nDate.equals(oDate)){
						mHandler.sendEmptyMessage(0); 
						if (Constants.LOG_ENABLE) {
							Log.d("qiqi", "data is same ,return.");
						}
						return;
					}
					if (Constants.LOG_ENABLE) {
						Log.d("qiqi", "data not same, download.");
					}
					getContentResolver().delete(Pic.CONTENT_URI, null, null);
					getContentResolver().delete(Category.CONTENT_URI, null, null);
					Editor editor = sharedData.edit();
					editor.putString("get_time", nDate);
					editor.commit();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else{
				Log.d("qiqi", "get提交失败");
			}
			//GET CATEGORY
			try {
				response = client.execute(getCategory);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response.getStatusLine().getStatusCode() == 200) {
				try {
					strResult = EntityUtils.toString(response.getEntity());
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				JSONObject jsonObject1 = null;
				try {
					/** 把json字符串转换成json对象 **/
					jsonObject1 = getJSON(strResult);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					//category
					String data3 = jsonObject1.getString("category");
					JSONArray categoryArray = new JSONArray(data3);
//					Log.d("qiqi", "categoryArray:" + categoryArray );
					for(int i = 0; i < categoryArray.length();i++){
						ContentValues value = new ContentValues();
						value.put(Category.COLUMN_DEFAULT_NAME,
								((JSONArray) categoryArray.get(i)).getString(1));
						value.put(Category.COLUMN_DEFAULT_PORT,
								((JSONArray) categoryArray.get(i)).getString(2));
						value.put(Category.COLUMN_DEFAULT_LAND,
								((JSONArray) categoryArray.get(i)).getString(3));
//						Log.d("qiqi", "values:" + ((JSONArray) categoryArray.get(0)).getString(1) + " " + ((JSONArray) categoryArray.get(0)).getString(2) + " " + ((JSONArray) categoryArray.get(0)).getString(3));
						getContentResolver().insert(Category.CONTENT_URI, value);
					}	
				}catch (JSONException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//GET CATEGORY
			try {
				response = client.execute(getPics);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response.getStatusLine().getStatusCode() == 200) {
				try {
					strResult = EntityUtils.toString(response.getEntity());
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				JSONObject jsonObject1 = null;
				try {
					/** 把json字符串转换成json对象 **/
					jsonObject1 = getJSON(strResult);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					//category
					String data3 = jsonObject1.getString("pics");
					JSONArray picArray = new JSONArray(data3);
					Log.d("qiqi", "picArray.length:" + picArray.length() );
					ArrayList<ContentValues> totalValues = new ArrayList<ContentValues>();
					for(int i = 0; i < picArray.length();i++){
						ContentValues value = new ContentValues();
						value.put(Pic.COLUMN_DEFAULT_NAME,
								((JSONArray) picArray.get(i)).getString(1));
						value.put(Pic.COLUMN_DEFAULT_MODE,
								((JSONArray) picArray.get(i)).getString(2));
						value.put(Pic.COLUMN_DEFAULT_CATEGORY,
								((JSONArray) picArray.get(i)).getString(3));
						value.put(Pic.COLUMN_DEFAULT_TAG,
								((JSONArray) picArray.get(i)).getString(4));
//						Log.d("qiqi", "values:" + ((JSONArray) picArray.get(i)).getString(1) + " " + ((JSONArray) picArray.get(i)).getString(2) + " " + ((JSONArray) picArray.get(i)).getString(3)+ " " + ((JSONArray) picArray.get(i)).getString(4));
						getContentResolver().insert(Pic.CONTENT_URI, value);
//						totalValues.add(value);
//						insert(totalValues);
					}	
					mHandler.sendEmptyMessage(0);
				}catch (JSONException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	};
	
	private void startGetInfo() {

		new Thread(getRun).start();
	}
	public JSONObject getJSON(String sb) throws JSONException {
		return new JSONObject(sb);
	}

	public Uri insert(ArrayList<ContentValues> values) {
		long rowId;
		Uri rowUri = null;
		TDPDbHelper dbHelper = new TDPDbHelper(this, "tdp.db", 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			// 批量处理操作
			// do something
			for (int i = 0; i < values.size(); i++) {
				rowId = db.insert(Pic.TABLE_NAME, null, values.get(i));
				if (rowId != -1) {
					rowUri = ContentUris
							.withAppendedId(Pic.CONTENT_URI, rowId);
				}
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} catch (Exception e) {

		}
		finally {
			// 结束事务
			db.endTransaction();
		}
		mHandler.sendEmptyMessage(0);
		db.close();
		return rowUri;
	}
}
