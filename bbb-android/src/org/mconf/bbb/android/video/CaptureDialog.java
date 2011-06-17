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

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class CaptureDialog extends Dialog {	
	private static final Logger log = LoggerFactory.getLogger(CaptureDialog.class);
		
	private VideoCapture videoWindow;
	private int videoId;
		
	public CaptureDialog(Context context, int userId) {
		super(context);
		
		this.videoId = userId;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes the title from the Dialog
		setContentView(R.layout.video_capture);
		
		android.view.WindowManager.LayoutParams windowAttributes = getWindow().getAttributes();		
		windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; //Makes the video brigth
//		windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //Makes it possible to interact with the window behind, but the video should be closed properly when the screen changes
//		windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_SCALED; //Removes the title from the dialog and removes the border also		 
		getWindow().setAttributes(windowAttributes);
		
		videoWindow = (VideoCapture) findViewById(R.id.video_capture);
		
		setTitle("Camera preview");
		setCancelable(true);
	}

	public int getVideoId() {
		return videoId;
	}
	
	public void setVideoId(int Id) {
		this.videoId = Id;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		resume();
	}
	
	@Override
	protected void onStop() {
		pause();
		super.onStop();
	}

	public void pause() {
	}
	
	public void resume() {
		videoWindow.setUserId(this.videoId);
		videoWindow.centerPreview(true);
	}
}
