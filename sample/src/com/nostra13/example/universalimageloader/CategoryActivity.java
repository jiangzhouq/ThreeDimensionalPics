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

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout.Directions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.directionalviewpager.DirectionalViewPager;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class CategoryActivity extends FragmentActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	DisplayImageOptions options;

	DirectionalViewPager pager;
	private ImagePagerAdapter mAdapter;
	private View mDecorView;
	int pagerPosition = 0;
	private String[] imageUrls = Constants.IMAGES;
	private int mode_choice = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);

		imageUrls = getIntent().getStringArrayExtra(Constants.CATEGORY_IMAGE_URLS_NAME);
		mode_choice = getIntent().getIntExtra(Constants.MODE_CHOICE_NAME, Constants.MODE_CHOLICE_HONGLAN);
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
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

		mDecorView = getWindow().getDecorView();
		hideSystemUI();
		pager = (DirectionalViewPager) findViewById(R.id.pager);
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageUrls,Configuration.ORIENTATION_LANDSCAPE);
			pager.setOrientation(DirectionalViewPager.HORIZONTAL);
			pager.setAdapter(mAdapter);
			pager.setCurrentItem(pagerPosition);
		}else{
			mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageUrls,Configuration.ORIENTATION_PORTRAIT);
			pager.setOrientation(DirectionalViewPager.VERTICAL);
			pager.setAdapter(mAdapter);
			pager.setCurrentItem(pagerPosition);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageUrls,Configuration.ORIENTATION_LANDSCAPE);
			pager.setOrientation(DirectionalViewPager.HORIZONTAL);
			pager.setAdapter(mAdapter);
			pager.setCurrentItem(pagerPosition);
		}else{
			mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageUrls,Configuration.ORIENTATION_PORTRAIT);
			pager.setOrientation(DirectionalViewPager.VERTICAL); 
			pager.setAdapter(mAdapter);
			pager.setCurrentItem(pagerPosition);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
//		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentPagerAdapter {

		private String[] mImages;
		private int mDirect;
		public ImagePagerAdapter(FragmentManager fm ,String[] images, int direct) {
			super(fm);
			mImages = images;
			mDirect = direct;
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			return CategoryFragment.newInstance(position , mImages , mDirect, mode_choice);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mImages.length/5 + 1;
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