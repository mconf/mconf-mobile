package org.mconf.bbb.android.video;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.BigBlueButton;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.video.IVideoListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
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
	private int userId;
	private boolean inDialog;
	private boolean showing = false;
	private static final float defaultAspectRatio = 4 / (float) 3;
	
	public VideoSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
	
        mRenderer = new VideoRenderer(this);
		setRenderer(mRenderer);
	}
	
	public static int[] getDisplayParameters(int width, int height){
		int[] params = new int[2];
		
	    int h = 0, w = 0;
		float displayAspectRatio = width / (float) height;
		if (displayAspectRatio < defaultAspectRatio) {
			w = width;
			h = (int) (w / defaultAspectRatio);
		} else {
			h = height;
			w = (int) (h * defaultAspectRatio);			
		}
		
		params[0] = w;
		params[1] = h;		
		return params;		
	}
	
	public void start(int userId, boolean inDialog) {
		this.userId = userId;
		this.inDialog = inDialog;
		
		if (showing)
			stop();
		
		updateLayoutParams(inDialog);		

		BigBlueButtonClient bbb = ((BigBlueButton) getContext().getApplicationContext()).getHandler();
		videoHandler = new VideoHandler(userId, bbb);
		videoHandler.start();
		bbb.addVideoListener(videoHandler);
		
		showing = true;
	}
	
	public void updateLayoutParams (boolean inDialog) {
		LayoutParams layoutParams = getLayoutParams();
		DisplayMetrics metrics = getDisplayMetrics(getContext());
		log.debug("Maximum display resolution: {} X {}\n", metrics.widthPixels, metrics.heightPixels);
		if(inDialog){
			metrics.widthPixels -= 40;
			metrics.heightPixels -= 40;
		}		
		int[] params = new int[2];
		params = getDisplayParameters(metrics.widthPixels, metrics.heightPixels);
		layoutParams.width = params[0];
		layoutParams.height = params[1];
		setLayoutParams(layoutParams);
		
		if(showing)
			nativeResize(metrics.widthPixels, metrics.heightPixels, params[0], params[1], 0, 0);
		else 
			initDrawer(metrics.widthPixels, metrics.heightPixels, params[0], params[1], 0, 0);       
	}
	
	public void stop() {
		if (showing) {
			BigBlueButtonClient bbb = ((BigBlueButton) getContext().getApplicationContext()).getHandler();
			bbb.removeVideoListener(videoHandler);
			videoHandler.stop();
			
			log.debug("VideoSurface.stop().endDrawer()");
			endDrawer();
			
			showing = false;
		}
	}

	static public DisplayMetrics getDisplayMetrics(Context context){
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
	}
		
	static {
    	System.load("/data/data/org.mconf.bbb.android/lib/libavutil.so");
    	System.load("/data/data/org.mconf.bbb.android/lib/libswscale.so");
        System.load("/data/data/org.mconf.bbb.android/lib/libavcodec.so");
        System.load("/data/data/org.mconf.bbb.android/lib/libavformat.so");
        System.load("/data/data/org.mconf.bbb.android/lib/libthread.so");
    	System.load("/data/data/org.mconf.bbb.android/lib/libcommon.so");
    	System.load("/data/data/org.mconf.bbb.android/lib/libqueue.so");
    	System.load("/data/data/org.mconf.bbb.android/lib/libdecode.so");
    	System.load("/data/data/org.mconf.bbb.android/lib/libmconfnative.so");  
        
    	log.debug("Video native libraries loaded");    
    }
	
	private native int initDrawer(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
	private native int nativeResize(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
	private native int endDrawer();
    private native int enqueueFrame(byte[] data, int length);	
}

