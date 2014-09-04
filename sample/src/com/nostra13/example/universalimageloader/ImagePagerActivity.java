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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
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
	BluetoothAdapter mBluetoothAdapter;
	DisplayImageOptions options;
//	private AcceptTask mAcceptTask;
	MyViewPager pager;
	private View mDecorView;
	private String[] mImageUrls;
	private int mPagerPosition;
	private Timer mTimer;
//	ImageButton blue;
//	private int blue_state = 0;
//	private final int STATE_BLUE_DISCONNECTED = 0;
//	private final int STATE_BLUE_CONNECTING = 1;
//	private final int STATE_BLUE_CONNECTED = 2;
	private Handler mHandler= new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				pager.setCurrentItem(pager.getCurrentItem() + 1,true);
				break;
			case 1:
				pager.setCurrentItem(pager.getCurrentItem() - 1,true);
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
//		blue = (ImageButton) findViewById(R.id.bluetooth);
//		blue.setOnClickListener(this);
		
		mDecorView = getWindow().getDecorView();
		hideSystemUI();
		
		SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		int sensorType = Sensor.TYPE_ACCELEROMETER;
		sm.registerListener(myAccelerometerListener,sm.getDefaultSensor(sensorType),SensorManager.SENSOR_DELAY_NORMAL);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
//		case R.id.bluetooth:
//			switch(blue_state){
//			case STATE_BLUE_DISCONNECTED:
//				blue_state = STATE_BLUE_CONNECTING;
//				blue.setImageResource(R.drawable.blue_anim);
//				AnimationDrawable animationDrawable = (AnimationDrawable)blue.getDrawable();  
//	            animationDrawable.start();  
//	            
//
//	    		if (mBluetoothAdapter != null) {
//	    			if (!mBluetoothAdapter.isEnabled()) {
//	    			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//	    			    startActivityForResult(enableBtIntent, 1);
//	    			}else{
//	    				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//	    				// If there are paired devices
//	    				Log.d("qiqi", "pairedDevices.size():" + pairedDevices.size());
//	    				if (pairedDevices.size() > 0) {
//	    				    // Loop through paired devices
//	    				    for (BluetoothDevice device : pairedDevices) {
//	    				        // Add the name and address to an array adapter to show in a ListView
//	    				    	Log.d("qiqi", device.getName() + "=====" + device.getAddress());
//	    				    	mAcceptTask = new AcceptTask();
//	    				    	mAcceptTask.execute();
//	    				    }
//	    				}
//	    			}
//	    		}else{
//	    			
//	    		}
//				break;
//			case STATE_BLUE_CONNECTING:
//				blue_state = STATE_BLUE_DISCONNECTED;
//				blue.setImageResource(R.drawable.bluetooth_off);
//				if(null != mAcceptTask)
//				{
//					mAcceptTask.cancelSocket();
//					mAcceptTask.cancel(true);
//				}
//				break;
//			case STATE_BLUE_CONNECTED:
//				blue_state = STATE_BLUE_DISCONNECTED;
//				blue.setImageResource(R.drawable.bluetooth_off);
//				if(null != mAcceptTask)
//				{
//					mAcceptTask.cancelSocket();
//					mAcceptTask.cancel(true);
//				}
//				break;
//			}
//			break;
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
		if(mTimer != null)
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
//	private class AcceptTask extends AsyncTask<Void, Integer, Boolean>{
//		private final BluetoothServerSocket mmServerSocket;
//		public void cancelSocket(){
//			try {
//	            mmServerSocket.close();
//	        } catch (IOException e) { }
//		}
//		public AcceptTask() {
//			BluetoothServerSocket tmp = null;
//	        try {
//	            // MY_UUID is the app's UUID string, also used by the client code
//	            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("main", UUID.fromString("71f78e96-3024-11e4-89c4-a6c5e4d22fb7"));
//	        } catch (IOException e) { }
//	        mmServerSocket = tmp;
//	        Log.d("qiqi", "create acceptthread");
//		}
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			BluetoothSocket socket = null;
//	        // Keep listening until exception occurs or a socket is returned
//	        Log.d("qiqi", "run()");
//	        while (true) {
//	            try {
//	            	Log.d("qiqi", "accetping socket");
//	                socket = mmServerSocket.accept();
//	                Log.d("qiqi", "accetping socket 2");
//	            } catch (IOException e) {
//	            	Log.d("qiqi", "" + e.toString());
//	                break;
//	            }
//	            if (socket != null) {
//	            	new ConnectedThread(socket).run();
//	                try {
//						mmServerSocket.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//	                return true;
//	            }else{
//	            	Log.d("qiqi", "nul ==  socket");
//	            	return false;
//	            }
//	        }
//			return false;
//		}
//		@Override
//		protected void onPostExecute(Boolean result) {
//			super.onPostExecute(result);
//			if(result)
//			{
//				blue_state = STATE_BLUE_CONNECTED;
//				blue.setImageResource(R.drawable.bluetooth_on);
//			}
//		}
//	}
//	private class ConnectedThread extends Thread {
//	    private final BluetoothSocket mmSocket;
//	    private final InputStream mmInStream;
//	    private final OutputStream mmOutStream;
//	 
//	    public ConnectedThread(BluetoothSocket socket) {
//	        mmSocket = socket;
//	        InputStream tmpIn = null;
//	        OutputStream tmpOut = null;
//	 
//	        // Get the input and output streams, using temp objects because
//	        // member streams are final
//	        try {
//	            tmpIn = socket.getInputStream();
//	            tmpOut = socket.getOutputStream();
//	        } catch (IOException e) { }
//	 
//	        mmInStream = tmpIn;
//	        mmOutStream = tmpOut;
//	    }
//	 
//	    public void run() {
//	        byte[] buffer = new byte[1024];  // buffer store for the stream
//	        int bytes; // bytes returned from read()
//	 
//	        // Keep listening to the InputStream until an exception occurs
//	        Log.d("qiqi", "start receive thread");
//	        while (true) {
//	            try {
//	                // Read from the InputStream
//	                bytes = mmInStream.read(buffer);
//	                byte[] newBuffer = new byte[bytes];
//	                for(int i = 0; i < bytes; i++){
//	                	newBuffer[i] = buffer[i];
//	                }
//	                
//	                Log.d("qiqi", "hello received:" + new String(newBuffer, "UTF-8") + " bytes:" + bytes);
//	                if(new String(newBuffer, "UTF-8").equals("right")){
//	                	if(pager != null){
//	        				mHandler.sendEmptyMessage(0);
//	        			}
//	                }else if(new String(newBuffer, "UTF-8").equals("left")){
//	                	if(pager != null){
//	        				mHandler.sendEmptyMessage(1);
//	        			}
//	                }
//	                // Send the obtained bytes to the UI activity
////	                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
////	                        .sendToTarget();
//	            } catch (IOException e) {
//	                break;
//	            }
//	        }
//	    }
//	 
//	    /* Call this from the main activity to send data to the remote device */
//	    public void write(byte[] bytes) {
//	        try {
//	            mmOutStream.write(bytes);
//	        } catch (IOException e) { }
//	    }
//	    /* Call this from the main activity to shutdown the connection */
//	    public void cancel() {
//	        try {
//	            mmSocket.close();
//	        } catch (IOException e) { }
//	    }
//	}
	
}