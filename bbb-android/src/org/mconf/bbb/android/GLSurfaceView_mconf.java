package org.mconf.bbb.android;

import android.app.Activity;

class GLSurfaceView_mconf extends GLSurfaceView_FFMPEG {	
	Renderer_mconf mRenderer;
		
	public GLSurfaceView_mconf(Activity ctx,int w, int h) {
		super(ctx);
				
		mRenderer = new Renderer_mconf(w,h);
		setRenderer(mRenderer);
    }   
}

