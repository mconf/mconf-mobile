package org.mconf.bbb.android.voip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AudioBarLayout extends LinearLayout {

	public AudioBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void hide() {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = 0;
		setLayoutParams(params);
	}
	
	public void show() {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		setLayoutParams(params);
	}
	
	

}
