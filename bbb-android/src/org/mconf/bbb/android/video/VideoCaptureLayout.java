package org.mconf.bbb.android.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class VideoCaptureLayout extends LinearLayout {

	public VideoCaptureLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void hide() {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.width = 1;
		params.height = 1;
		setLayoutParams(params);
	}
	
	public void show() {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.width = 320;
		params.height = 240;
		setLayoutParams(params);
	}	
}
