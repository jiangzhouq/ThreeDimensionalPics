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
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
public class ModeChoiceActivity extends Activity implements OnClickListener{
	
	private View mDecorView;
	DisplayImageOptions options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
		setListener();
		hideSystemUI();
		ImageView bg = (ImageView) findViewById(R.id.bg);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage("http://jiangzhoumigs-jiangzhoumigs.stor.sinaapp.com/original/20140909bg.jpg",bg, options);
		
//		Log.d("qiqi", "TDPProvider start onCreate");
//		Cursor mCursor = getContentResolver().query(Page.CONTENT_URI, null, null, null, null);
//		ContentValues value = new ContentValues();
//		value.put(Page.COLUMN_DEFAULT_NAME, "nimabi");
//		value.put(Page.COLUMN_DEFAULT_URL, "zhangwanglitishidai");
//		value.put(Page.COLUMN_DEFAULT_CTIME, "20140901");
//		getContentResolver().insert(Page.CONTENT_URI, value);
		startGetInfo();
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
			intent.putExtra(Constants.CATEGORY_IMAGE_URLS_NAME, Constants.IMAGES_HONGLAN_CATEGORIES);
			intent.putExtra(Constants.MODE_CHOICE_NAME, Constants.MODE_CHOLICE_HONGLAN);
			startActivity(intent);
			break;
		case R.id.zuoyou:
			intent.putExtra(Constants.CATEGORY_IMAGE_URLS_NAME, Constants.IMAGES_ZUOYOU_CATEGORIES);
			intent.putExtra(Constants.MODE_CHOICE_NAME, Constants.MODE_CHOLICE_ZUOYOU);
			startActivity(intent);
			break;
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			if (Constants.LOG_ENABLE) {
				Log.d("qiqi", "横屏");
			}
			setContentView(R.layout.mc_landscape);
		}else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			if (Constants.LOG_ENABLE) {
				Log.d("qiqi", "竖屏");
			}
			setContentView(R.layout.mc_portrait);
		}
		setListener();
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	        super.onWindowFocusChanged(hasFocus);
	    if (hasFocus) {
	    	mDecorView.setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	}
	// This snippet hides the system bars.
	private void hideSystemUI() {
	    // Set the IMMERSIVE flag.
	    // Set the content to appear under the system bars so that the content
	    // doesn't resize when the system bars hide and show.
	    mDecorView.setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	// This snippet shows the system bars. It does this by removing all the flags
	// except for the ones that make the content appear under the system bars.
	private void showSystemUI() {
	    mDecorView.setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}
	
	private void startGetInfo() {
		Runnable getRun = new Runnable() {

			@Override
			public void run() {
				DefaultHttpClient client1 = new DefaultHttpClient();
				/** NameValuePair是传送给服务器的请求参数 param.get("name") **/

				UrlEncodedFormEntity entity1 = null;

				/** 新建一个get请求 **/
				HttpGet get = new HttpGet(
						"http://106.186.22.172:12341?name=qiqi");
				HttpResponse response1 = null;
				String strResult1 = "";
				try {
					/** 客服端向服务器发送请求 **/
					response1 = client1.execute(get);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/** 请求发送成功，并得到响应 **/
				if (response1.getStatusLine().getStatusCode() == 200) {
					try {
						/** 读取服务器返回过来的json字符串数据 **/
						strResult1 = EntityUtils
								.toString(response1.getEntity());
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					JSONObject jsonObject1 = null;
					try {
						/** 把json字符串转换成json对象 **/
						jsonObject1 = getJSON(strResult1);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String names = "";
					String category = "";
					String mode = "";
					String url = "";
					Log.d("qiqi", strResult1);
					ArrayList<ContentValues> totalValues = new ArrayList<ContentValues>();
					try {
						String data = jsonObject1.getString("pics");
						JSONArray jarr1 = new JSONArray(data);
						for (int i = 0; i < jarr1.length(); i++) {

							/** **/
							JSONObject jsono = (JSONObject) jarr1.get(i);

							/** 取出list下的name的值 **/
							Log.d("qiqi", mode + " " + category + " " + names);
							ContentValues value = new ContentValues();
							value.put(Pic.COLUMN_DEFAULT_MODE,
									jsono.getString("mode"));
							value.put(Pic.COLUMN_DEFAULT_CATEGORY,
									jsono.getString("category"));
							value.put(Pic.COLUMN_DEFAULT_NAME,
									jsono.getString("name"));
							value.put(Pic.COLUMN_DEFAULT_URL,
									jsono.getString("url"));
							totalValues.add(value);
						}
						insert(totalValues);
						
						String data2 = jsonObject1.getString("page");
						JSONObject jsono = new JSONObject(data2);

							ContentValues value = new ContentValues();
							value.put(Page.COLUMN_DEFAULT_NAME,
									jsono.getString("name"));
							value.put(Page.COLUMN_DEFAULT_URL,
									jsono.getString("url"));
							value.put(Page.COLUMN_DEFAULT_CTIME,
									jsono.getString("ctime"));
							getContentResolver().insert(Page.CONTENT_URI, value);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else
					// Toast.makeText(MainActivity.this, "get提交失败",
					// Toast.LENGTH_SHORT).show();
					Log.d("qiqi", "get提交失败");
			}
		};
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
		db.close();
		return rowUri;
	}
}
