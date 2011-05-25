package org.mconf.bbb.android.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup.LayoutParams;

class VideoRenderer implements GLSurfaceView.Renderer {
	
	private static final Logger log = LoggerFactory.getLogger(VideoRenderer.class);
	private VideoSurface context;
		
	public VideoRenderer(VideoSurface context){
		super();
		this.context = context;
	}
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        log.debug("onSurfaceCreated");
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        log.debug("onSurfaceChanged");
        
        int[] params = new int[2];
		params = VideoSurface.getDisplayParameters(w, h);
        nativeResize(w, h, params[0], params[1], 0, 0);       
    }
    
    public void onDrawFrame(GL10 gl) {
    	if (nativeRender() != 0) {
//    		LayoutParams layoutParams = context.getLayoutParams();
//    		float layoutAspectRatio = layoutParams.width / (float) layoutParams.height;
//    		float videoAspectRatio = getVideoWidth() / (float) getVideoHeight();
//    		
//    		if (videoAspectRatio < layoutAspectRatio) {
//    			layoutParams.height = (int) (layoutParams.width / videoAspectRatio);
//    		} else {
//    			layoutParams.width = (int) (layoutParams.height * videoAspectRatio);
//    		}
    		
    		// this call must run from a UI thread!
//    		context.setLayoutParams(layoutParams);    		
    	}
    }
      
    private native int nativeRender();
    private native int nativeResize(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
//    private native int getVideoWidth();
//    private native int getVideoHeight();
}

