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

import org.mconf.bbb.android.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class CaptureFullScreen extends Activity {	
	private static final Logger log = LoggerFactory.getLogger(CaptureFullScreen.class);
	
	private VideoCapture videoWindow;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.video_capture);
		
		videoWindow = (VideoCapture) findViewById(R.id.video_capture);
		videoWindow.centerPreview(false);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
			finish();
		}	
	}
}
