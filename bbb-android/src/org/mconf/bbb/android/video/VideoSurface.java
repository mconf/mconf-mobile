package org.mconf.bbb.android.video;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.BigBlueButton;
import org.mconf.bbb.video.IVideoListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
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
	public static final float DEFAULT_ASPECT_RATIO = 4 / (float) 3;
	private float aspectRatio = DEFAULT_ASPECT_RATIO;
	
	public VideoSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
	
        mRenderer = new VideoRenderer(this);
		setRenderer(mRenderer);
	}
	
	public int[] getDisplayParameters(int width, int height){
		int[] params = new int[2];
		
	    int h = 0, w = 0;
		float displayAspectRatio = width / (float) height;
		if (displayAspectRatio < aspectRatio) {
			w = width;
			h = (int) (w / aspectRatio);
		} else {
			h = height;
			w = (int) (h * aspectRatio);			
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
		
		BigBlueButtonClient bbb = ((BigBlueButton) getContext().getApplicationContext()).getHandler();
		videoHandler = new VideoHandler(userId, bbb);
		float tmp = videoHandler.getAspectRatio();
		aspectRatio = (tmp > 0? tmp: DEFAULT_ASPECT_RATIO);

		updateLayoutParams(inDialog);		

		bbb.addVideoListener(videoHandler);
		videoHandler.start();
		
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
		int[] params = getDisplayParameters(metrics.widthPixels, metrics.heightPixels);
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
			videoHandler.stop();
			bbb.removeVideoListener(videoHandler);
			
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
		String path = "/data/data/org.mconf.bbb.android/lib/";
		try {
			System.load(path + "libavutil.so");
			System.load(path + "libswscale.so");
			System.load(path + "libavcodec.so");
			System.load(path + "libavformat.so");
			System.load(path + "libthread.so");
			System.load(path + "libcommon.so");
			System.load(path + "libqueue.so");
			System.load(path + "libdecode.so");
			System.load(path + "libmconfnative.so");
	        
	    	log.debug("Native libraries loaded");
		} catch (SecurityException e) {
	    	log.debug("Native libraries failed");
		}
    }
	
	private native int initDrawer(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
	private native int nativeResize(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
	private native int endDrawer();
    private native int enqueueFrame(byte[] data, int length);	
}

