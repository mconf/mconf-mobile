package org.mconf.bbb.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ListView;

// creates a list view with scrolling disabled
public class CustomListview extends ListView {

	public static final int ROW_HEIGHT = 42;

	public CustomListview(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		
		if (ev.getAction()==MotionEvent.ACTION_POINTER_DOWN || ev.getAction()==MotionEvent.ACTION_POINTER_UP) {
			System.out.println("cancelled");
			ev.setAction(MotionEvent.ACTION_CANCEL);
			return true;
		}
		
		return super.dispatchTouchEvent(ev);
	}
	
	//calculates the size of the contacts and listeners lists
	public int setHeight() {
		int totalHeight = 0;
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT, r.getDisplayMetrics());
		totalHeight= getCount()*(px+1);
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = totalHeight + (getDividerHeight() * (getCount() - 1));
		setLayoutParams(params); 
		requestLayout();
		return params.height;
	}

}
