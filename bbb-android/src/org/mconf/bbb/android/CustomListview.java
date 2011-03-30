package org.mconf.bbb.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

public class CustomListview extends ListView{
//creates a list view with scrolling disabled
	public CustomListview(Context context, AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev)
	{
		
		if(ev.getAction()==MotionEvent.ACTION_POINTER_DOWN || ev.getAction()==MotionEvent.ACTION_POINTER_UP  )
		{
			System.out.println("cancelled");
			ev.setAction(MotionEvent.ACTION_CANCEL);
		}
		super.dispatchTouchEvent(ev);
		return true;

	}
	
	
	


}
