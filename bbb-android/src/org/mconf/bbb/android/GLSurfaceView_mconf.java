package org.mconf.bbb.android;

import android.app.Activity;

class GLSurfaceView_mconf extends GLSurfaceView_FFMPEG {	
	Renderermconf mRenderer;
		
	public GLSurfaceView_mconf(Activity ctx,int w, int h) {
		super(ctx);
				
		mRenderer = new Renderermconf(w,h);
		setRenderer(mRenderer);
    }   
}

