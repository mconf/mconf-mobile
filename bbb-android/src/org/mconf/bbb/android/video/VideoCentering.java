package org.mconf.bbb.android.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class VideoCentering {
	private static final Logger log = LoggerFactory.getLogger(VideoCentering.class);
	public static final float DEFAULT_ASPECT_RATIO = 4 / (float) 3;
	private float aspectRatio = DEFAULT_ASPECT_RATIO;
	
	public LayoutParams getVideoLayoutParams(DisplayMetrics metrics, LayoutParams layoutParams) {		
		int h = 0, w = 0;
		float displayAspectRatio = metrics.widthPixels / (float) metrics.heightPixels;
		if (displayAspectRatio < getAspectRatio()) {
			w = metrics.widthPixels;
			h = (int) (w / getAspectRatio());
		} else {
			h = metrics.heightPixels;
			w = (int) (h * getAspectRatio());			
		}
		layoutParams.width = w;
		layoutParams.height = h;
		return layoutParams;
	}
	
	public DisplayMetrics getDisplayMetrics(Context context, boolean inDialog){
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(metrics);
        log.debug("Maximum display resolution: {} X {}\n", metrics.widthPixels, metrics.heightPixels);
        if(inDialog){
			metrics.widthPixels -= 40;
			metrics.heightPixels -= 40;
		}
        return metrics;
	}

	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}	
}

