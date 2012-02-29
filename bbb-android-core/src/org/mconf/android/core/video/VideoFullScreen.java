/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.android.core.video;

import org.mconf.android.core.BigBlueButtonActivity;
import org.mconf.android.core.Client;
import org.mconf.android.core.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

public class VideoFullScreen extends BigBlueButtonActivity {	
	private static final Logger log = LoggerFactory.getLogger(VideoFullScreen.class);
	
	private VideoSurface videoWindow;
	private int userId;
	private String name;
	private boolean isPreview;
	
	VideoCaptureLayout videocaplayout;

	private BroadcastReceiver closeVideo = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			int userId= extras.getInt("userId");
			if (VideoFullScreen.this.userId == userId) {
				if(!isPreview){
					videoWindow.stop();
				}
				finish();
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userId = extras.getInt("userId");
			name = extras.getString("name");
		}
		
		if(userId == getBigBlueButton().getMyUserId()){
			isPreview = true;
		} else {
			isPreview = false;
		}

		IntentFilter closeVideoFilter = new IntentFilter(Client.CLOSE_VIDEO);
		registerReceiver(closeVideo, closeVideoFilter);
		
		if(isPreview){
			setContentView(R.layout.video_capture);
			videocaplayout = (VideoCaptureLayout) findViewById(R.id.video_capture_layout);
			videocaplayout.setOnClickListener(		
					
					new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
						//Intent intent = new Intent(getApplicationContext(), Client.class);
						//intent.putExtra("userId", userId);
						//intent.putExtra("name", name);
						//intent.setAction(Client.BACK_TO_VIDEO_DIALOG);
						//startActivity(intent);
						finish();
					}
					
				                                });		
		
		} else {
			setContentView(R.layout.video_window_fullscreen);			
			videoWindow = (VideoSurface) findViewById(R.id.video_window);
			videoWindow.setOnClickListener(		
					
					 new View.OnClickListener() {

					 @Override
					 public void onClick(View v) {
						 						   						
							//Intent intent = new Intent(getApplicationContext(), Client.class);
							//intent.putExtra("userId", userId);
							//intent.putExtra("name", name);
							//intent.setAction(Client.BACK_TO_VIDEO_DIALOG);
							//startActivity(intent);
							finish();
					 }
					
				                                  });			
		}
	}
	
	@Override
	protected void onPause() {	
		if(!isPreview){
			videoWindow.stop();
		}
		
		super.onPause();		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if(isPreview){
			videocaplayout.show(0);
		} else {
			videoWindow.start(userId, false);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(closeVideo);
		
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if(isPreview){			
			videocaplayout.show(0);
		} else {
			videoWindow.updateLayoutParams(false);
		}
	}
}
