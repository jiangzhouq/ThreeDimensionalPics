package com.nostra13.example.universalimageloader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ModeChoiceActivity extends Activity implements OnClickListener{
	
	private View mDecorView;
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
		setListener();
		hideSystemUI();
	}
	private void setListener(){
		ImageView view1 = (ImageView) findViewById(R.id.honglan);
		view1.setOnClickListener(this);
		ImageView view2 = (ImageView) findViewById(R.id.zuoyou);
		view2.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this,CategoryActivity.class);
		switch(v.getId()){
		case R.id.honglan:
			intent.putExtra(Constants.CATEGORY_IMAGE_URLS_NAME, Constants.IMAGES_HONGLAN);
			startActivity(intent);
			break;
		case R.id.zuoyou:
			intent.putExtra(Constants.CATEGORY_IMAGE_URLS_NAME, Constants.IMAGES_ZUOYOU);
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
