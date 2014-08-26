package com.nostra13.example.universalimageloader;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public final class CategoryFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ic_empty)
	.showImageOnFail(R.drawable.ic_error)
	.resetViewBeforeLoading(true)
	.cacheOnDisk(true)
	.imageScaleType(ImageScaleType.EXACTLY)
	.bitmapConfig(Bitmap.Config.RGB_565)
	.considerExifParams(true)
	.displayer(new FadeInBitmapDisplayer(300))
	.build();
	
	private String[] mImages;
	private int mDirect;
	
	public static CategoryFragment newInstance(int content , String[] images, int direct) {
		CategoryFragment fragment = new CategoryFragment();
		fragment.mDirect = direct;
		fragment.mImages = images;
		fragment.mContent = content;
		return fragment;
	}
	private int  mContent = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getInt(KEY_CONTENT);
		}
		LinearLayout layout = null;
		if(mDirect == Configuration.ORIENTATION_LANDSCAPE){
			layout = (LinearLayout) inflater.inflate(R.layout.category_pager_landscape, container, false);
		}else{
			layout = (LinearLayout) inflater.inflate(R.layout.category_pager_portrait, container, false);
		}
		int[] imageViewIds = new int[]{
				R.id.category1,
				R.id.category2,
				R.id.category3,
				R.id.category4,
				R.id.category5};
		for (int i = 0; i < imageViewIds.length; i++){
			if((mContent * imageViewIds.length + i) < mImages.length){
				ImageView imageView = (ImageView) layout.findViewById(imageViewIds[i]);
				imageLoader.displayImage(mImages[mContent*imageViewIds.length + i], imageView, options);
			}
		}
		
		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CONTENT, mContent);
	}
}
