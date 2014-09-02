/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.example.universalimageloader;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.widget.MyViewPager;
import com.nostra13.example.universalimageloader.widget.MyViewPager.Toucher;
import com.nostra13.example.universalimageloader.widget.ZoomOutPageTransformer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends BaseActivity implements OnClickListener {

	private static final String STATE_POSITION = "STATE_POSITION";

	DisplayImageOptions options;

	MyViewPager pager;
	private View mDecorView;
	private String[] mImageUrls;
	private int mPagerPosition;
	private Timer mTimer;
	private Handler mHandler= new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				pager.setCurrentItem(pager.getCurrentItem() + 1,true);
				break;
			}
		};
	};
	private boolean bar_show = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_image_pager_2);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		mImageUrls= bundle.getStringArray(Extra.IMAGES);
		mPagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

		if (savedInstanceState != null) {
			mPagerPosition = savedInstanceState.getInt(STATE_POSITION);
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

		pager = (MyViewPager) findViewById(R.id.pager);
		pager.setPageTransformer(true, new ZoomOutPageTransformer());
		pager.setAdapter(new ImagePagerAdapter(mImageUrls));
		pager.setCurrentItem(mPagerPosition);
		pager.setToucher(new Toucher() {
			@Override
			public void onTouchUp() {
				mTimer = new Timer();
				bar_show = !bar_show;
				LinearLayout linear = (LinearLayout) findViewById(R.id.bar);
				if(bar_show){
					linear.setVisibility(View.VISIBLE);
				}else{
					linear.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onTouchDown() {
				if(mTimer != null)
					mTimer.cancel();
			}
		});
		ImageButton display = (ImageButton) findViewById(R.id.display);
		display.setOnClickListener(this);
		mDecorView = getWindow().getDecorView();
		hideSystemUI();
		
		SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		int sensorType = Sensor.TYPE_ACCELEROMETER;
		sm.registerListener(myAccelerometerListener,sm.getDefaultSensor(sensorType),SensorManager.SENSOR_DELAY_NORMAL);
	}
	private int mSensorCount = 0;
	final SensorEventListener myAccelerometerListener = new SensorEventListener(){  
        
        //复写onSensorChanged方法  
        public void onSensorChanged(SensorEvent sensorEvent){  
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){  
                //图解中已经解释三个值的含义  
                float X_lateral = sensorEvent.values[0];  
                float Y_longitudinal = sensorEvent.values[1];  
                float Z_vertical = sensorEvent.values[2];  
//                if(X_lateral > 5)
//                	Log.d("qiqi","\n heading "+X_lateral); 
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE  ){
                	if(Y_longitudinal > 3 ){
                		if(X_lateral > 0){
                			mSensorCount ++;
                			if(mSensorCount == 10){
                				mSensorCount = 0;
                				pager.setCurrentItem(pager.getCurrentItem() + 1,true);
                			}
                		}else if(X_lateral < 0){
                			mSensorCount ++;
                			if(mSensorCount == 10){
                				mSensorCount = 0;
                				pager.setCurrentItem(pager.getCurrentItem() -1,true);
                			}
                		}
                	}else if (Y_longitudinal < -3){
                		if(X_lateral > 0){
                			mSensorCount ++;
                			if(mSensorCount == 10){
                				mSensorCount = 0;
                				pager.setCurrentItem(pager.getCurrentItem() - 1,true);
                			}
                		}else{
                			mSensorCount ++;
                			if(mSensorCount == 10){
                				mSensorCount = 0;
                				pager.setCurrentItem(pager.getCurrentItem() + 1,true);
                			}
                		}
                	}
                }
            }  
        }  
        //复写onAccuracyChanged方法  
        public void onAccuracyChanged(Sensor sensor , int accuracy){  
            Log.d("qiqi", "onAccuracyChanged");  
        }  
    };  
    
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.display:
			mTimer = new Timer();
			bar_show = !bar_show;
			LinearLayout linear = (LinearLayout) findViewById(R.id.bar);
			if(bar_show){
				linear.setVisibility(View.VISIBLE);
			}else{
				linear.setVisibility(View.INVISIBLE);
			}
			mTimer.schedule(new MyTask(), 5000,5000);
			break;
		}
	}
	
	class MyTask extends TimerTask{

		@Override
		public void run() {
			if(pager != null){
				mHandler.sendEmptyMessage(0);
			}
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTimer.cancel();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		pager.setAdapter(new ImagePagerAdapter(mImageUrls));
		pager.setCurrentItem(mPagerPosition);
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
//		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void destroyItem(View view, int arg1, Object object) {
			ViewGroup container = (ViewGroup) view;
			container.removeView((View) object);
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object instantiateItem(View view, int position) {
			ViewGroup viewG = (ViewGroup) view;
			View imageLayout = inflater.inflate(R.layout.item_pager_image, viewG, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			viewG.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}
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