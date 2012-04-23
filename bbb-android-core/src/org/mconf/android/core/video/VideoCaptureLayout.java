package org.mconf.android.core.video;

import org.mconf.android.core.BigBlueButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class VideoCaptureLayout extends LinearLayout {

	private static final Logger log = LoggerFactory.getLogger(VideoCaptureLayout.class);	
	
	public VideoCaptureLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void hide() {
		VideoCentering mVideoCentering = new VideoCentering();
		ViewGroup.LayoutParams layoutParams = mVideoCentering.hidePreview(getLayoutParams());   	
		setLayoutParams(layoutParams);
		
//		ViewGroup.LayoutParams params = getLayoutParams();
//		params.width = 1;
//		params.height = 1;
//		setLayoutParams(params);
	}
	
	public void show(int margin) {
		VideoPublish mVideoPublish = ((BigBlueButton) getContext().getApplicationContext()).getVideoPublish();
		if(mVideoPublish == null){
    		log.debug("Error: could not show capture preview. Reason: could not get or instantiate a VideoPublisher");
    		return;
    	}
		
		VideoCentering mVideoCentering = new VideoCentering();
    	mVideoCentering.setAspectRatio(mVideoPublish.getWidth()/(float)mVideoPublish.getHeight());
    	ViewGroup.LayoutParams layoutParams = mVideoCentering.getVideoLayoutParams(mVideoCentering.getDisplayMetrics(getContext(),margin), getLayoutParams());
		setLayoutParams(layoutParams);

		//ViewGroup.LayoutParams params = getLayoutParams();
		//params.width = 320;
		//params.height = 240;
		//setLayoutParams(params);
	}

	public void destroy() {
		VideoCentering mVideoCentering = new VideoCentering();
		ViewGroup.LayoutParams layoutParams = mVideoCentering.destroyPreview(getLayoutParams());   	
		setLayoutParams(layoutParams);
		
//		ViewGroup.LayoutParams params = getLayoutParams();
//		params.width = 0;
//		params.height = 0;
//		setLayoutParams(params);
	}	
}
