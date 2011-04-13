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
import android.view.Gravity;
import android.view.Window;

public class VideoDialog extends Dialog {	
	private static final Logger log = LoggerFactory.getLogger(VideoDialog.class);
		
	private VideoSurface videoWindow;
	private int userId;
	private String name;
	
	public VideoDialog(Context context, int userIdLocal, String nameLocal) {
		super(context);
		
		setVideoId(userIdLocal);
		setVideoName(nameLocal);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes the title from the Dialog
		setContentView(R.layout.video_window);
		
		android.view.WindowManager.LayoutParams windowAttributes = getWindow().getAttributes();
		
		//Changes the place of the Dialog
//		windowAttributes.gravity = Gravity.BOTTOM; //title shows ok
		windowAttributes.gravity = Gravity.CENTER; //title shows cutted
//		windowAttributes.gravity = Gravity.FILL; //title shows weird. The Dialog appears at the top left	
//		windowAttributes.gravity = Gravity.LEFT; //title shows cutted	
//		windowAttributes.gravity = Gravity.TOP; //title shows cutted	
		
		windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; //Makes the video brigth
//		windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //Makes it possible to interact with the window behind, but the video should be closed properly when the screen changes
//		windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_SCALED; //Removes the title from the dialog and removes the border also
		 
		getWindow().setAttributes(windowAttributes);
		
		videoWindow = (VideoSurface) findViewById(R.id.video_window);
		videoWindow.start(userId, true);
		
		setTitle(name);
		setCancelable(true);		
	}
	
	private void setVideoId(int userIdLocal){
		userId = userIdLocal;
	}
	
	private void setVideoName(String userName){
		name = userName;
	}
	
	public int getVideoId(){
		return userId;
	}
	
	public String getVideoName(){
		return name;
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		videoWindow.onPause();
	}
}
