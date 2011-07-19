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

package org.mconf.bbb.android.video;

import org.mconf.bbb.android.BigBlueButtonActivity;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

public class VideoFullScreen extends BigBlueButtonActivity {	
	private static final Logger log = LoggerFactory.getLogger(VideoFullScreen.class);
	
	private VideoSurface videoWindow;
	private int userId;
	private String name;

	private BroadcastReceiver closeVideo = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			int userId= extras.getInt("userId");
			if (VideoFullScreen.this.userId == userId) {
				videoWindow.stop();
				finish();
			}
		}
		
	};
	
	private BroadcastReceiver closeVideoCapture = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			VideoCapture mVideoCapture = (VideoCapture) findViewById(R.id.video_capture);
			mVideoCapture.resume(false);
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

		IntentFilter closeVideoFilter = new IntentFilter(Client.CLOSE_VIDEO);
		registerReceiver(closeVideo, closeVideoFilter);
		
		setContentView(R.layout.video_window);
		
		videoWindow = (VideoSurface) findViewById(R.id.video_window);
		
		IntentFilter closeVideoCaptureFilter = new IntentFilter(Client.CLOSE_VIDEO_CAPTURE);
		registerReceiver(closeVideoCapture, closeVideoCaptureFilter);
	}
	
	@Override
	protected void onPause() {	
		videoWindow.stop();
		
		super.onPause();		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		videoWindow.start(userId, false);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(closeVideo);
		unregisterReceiver(closeVideoCapture);
		
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		videoWindow.updateLayoutParams(false);
	}
}
