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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

public class CaptureDialog extends Dialog {	
	private static final Logger log = LoggerFactory.getLogger(CaptureDialog.class);
		
	private VideoCapture videoWindow;
	private int videoId;
	public boolean isPreviewHidden = true;
	public boolean wasPreviewHidden = false;
		
	public CaptureDialog(Context context, int userId) {
		super(context);
		
		this.videoId = userId;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes the title from the Dialog
		setContentView(R.layout.video_capture);
		
		videoWindow = (VideoCapture) findViewById(R.id.video_capture);		
		
		videoWindow.setUserId(this.videoId);
		showPreview(true);
			
		setTitle("Camera preview");
		setCancelable(false);
	}

	public void hidePreview() { //hides the preview but keeps capturing
		if(!isPreviewHidden){   	
			android.view.WindowManager.LayoutParams windowAttributes = getWindow().getAttributes();	  
		    windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_SCALED
		    					| android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		    					| android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		    ;	  
		    getWindow().setAttributes(windowAttributes);
		  
		    if(videoWindow != null){
			    videoWindow.hidePreview();
		    }
		  
		    isPreviewHidden = true;
		}
	}
	
	public void showPreview(boolean center) { //if the preview is hidden, show it
		if(isPreviewHidden){			
			android.view.WindowManager.LayoutParams windowAttributes = getWindow().getAttributes();		
			windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; //Makes the video brigth
			getWindow().setAttributes(windowAttributes);
			
			if(videoWindow != null && center){
				videoWindow.centerPreview(true);
			}
			
			isPreviewHidden = false;
		}
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
		if(isPreviewHidden){
			showPreview(false); //this is needed to avoid a crash when closing the dialog
			wasPreviewHidden = true;
		} else {
			wasPreviewHidden = false;
		}
	}
	
	public void resume() {
		if(wasPreviewHidden){ //if the preview was hidden before the onStop,
							  //then lets hide it again after the Dialog is resumed
			hidePreview();
		}
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event){
		super.onKeyDown(keyCode, event);
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			hidePreview();
        }
		
		return true;
	}
}
