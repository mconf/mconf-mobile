package org.mconf.bbb.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import android.util.DisplayMetrics;


class Renderer_mconf extends GLSurfaceView_FFMPEG.Renderer {
	private static final Logger log = LoggerFactory.getLogger(ShowVideo.class);
	private boolean DEBUG = true;
	
	//private Benchmarks mBench = null;
	DisplayMetrics metrics = null;
	private int width,heigth;
	
	public Renderer_mconf(int w, int h){
		super();
		width = w;
		heigth = h;
	}
	
	private void initVideoJava(){
		nativeVideoInitJavaCallbacks();
		//mBench = new Benchmarks();		
	}
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        //nativeResize(w, h);
    }
    
    public void onDrawFrame(GL10 gl) {
    	initVideoJava();
    	nativeRender(width,heigth);
    	if(DEBUG){
    		log.debug("Java Renderer_mconf class", String.format("Video rendering was stoped\n"));
    	}
    }
    
    public int swapBuffers() // Called from native code, returns 1 on success, 0 when GL context lost (user put app to background)
	{
		synchronized (this) {
			this.notify();
		}
		//Thread.yield();
		//mBench.simpleBench(30000);
		return super.SwapBuffers() ? 1 : 0;
	}
    

    private native int nativeVideoInitJavaCallbacks();
    private native int nativeRender(int width,int heigth);
}

