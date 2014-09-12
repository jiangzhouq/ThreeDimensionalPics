package com.nostra13.example.universalimageloader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
	
}
