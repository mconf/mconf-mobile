package org.mconf.bbb.android.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.video.IVideoListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import android.content.SyncResult;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup.LayoutParams;

class VideoRenderer implements GLSurfaceView.Renderer {
	
	private static final Logger log = LoggerFactory.getLogger(VideoRenderer.class);
		
	public VideoRenderer(){
		super();
		
	}
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//    	initDrawer(width, height);
//    	Client.bbb.addVideoListener(videoHandler);
//    	videoHandler.start();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        
    }
    
    public void onDrawFrame(GL10 gl) {
    	if (nativeRender() == 0) {
//    		LayoutParams layoutParams = context.getLayoutParams();
//    		layoutParams.height = 240;
//    		layoutParams.width = 320;
//    		context.setLayoutParams(layoutParams);
    	}
    }
      
    private native int nativeRender();
}

