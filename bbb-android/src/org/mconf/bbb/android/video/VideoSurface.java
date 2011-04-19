package org.mconf.bbb.android.video;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.video.IVideoListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;

public class VideoSurface extends GLSurfaceView {
	private class VideoHandler extends IVideoListener {

		public VideoHandler(int userId, BigBlueButtonClient context) {
			super(userId, context);
		}

		@Override
		public void onVideo(byte[] data) {
			enqueueFrame(data,data.length);
		}
		
	}
	
	
	private static final Logger log = LoggerFactory.getLogger(VideoSurface.class);
	private VideoRenderer mRenderer;		
	private VideoHandler videoHandler;
	
	public VideoSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void start(int userId, boolean inDialog) {
		DisplayMetrics metrics = VideoCentering.getDisplayMetrics(this.getContext(), inDialog);
		LayoutParams layoutParams = VideoCentering.getVideoLayoutParams(metrics, this.getLayoutParams());
		setLayoutParams(layoutParams);		
        initDrawer(metrics.widthPixels, metrics.heightPixels, layoutParams.width, layoutParams.height, 0, 0);

        mRenderer = new VideoRenderer(this);
		setRenderer(mRenderer);

		videoHandler = new VideoHandler(userId, Client.bbb);
		videoHandler.start();
		Client.bbb.addVideoListener(videoHandler);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Client.bbb.removeVideoListener(videoHandler);
		videoHandler.stop();
		
		endDrawer();
	}
		
	static {
    	System.loadLibrary("avutil");
    	System.loadLibrary("swscale");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("thread");
    	System.loadLibrary("common");
    	System.loadLibrary("queue");
    	System.loadLibrary("decode");
    	System.loadLibrary("mconfnativeshowvideo");  
        
    	log.debug("Video native libraries loaded");    
    }
	
	private native int initDrawer(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
	private native int endDrawer();
    private native int enqueueFrame(byte[] data, int length);	
}

