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

class VideoRenderer implements GLSurfaceView.Renderer {
	
	private static final Logger log = LoggerFactory.getLogger(VideoRenderer.class);
//	private int width,height;
		
	public VideoRenderer(){
		super();
//		width = w;
//		height = h;
		
	}
	
//	public VideoHandler getVideoHandler() {
//		return videoHandler;
//	}
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//    	initDrawer(width, height);
//    	Client.bbb.addVideoListener(videoHandler);
//    	videoHandler.start();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        
    }
    
    public void onDrawFrame(GL10 gl) {
    	//synchronized (this) {
        	nativeRender();
		//}
    }
      
    private native int nativeRender();
}

