package org.mconf.bbb.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListview extends ListView{

	public CustomListview(Context context, AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{



		ev.setAction(MotionEvent.ACTION_CANCEL);
		super.dispatchTouchEvent(ev);
		return true;

	}


}
