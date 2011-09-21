package org.mconf.android.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

// creates a list view with scrolling disabled
public class NoScrollListView extends ListView {

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		updateLayout();
	}
	
	@Override
	public void postInvalidate() {
		// TODO Auto-generated method stub
		super.postInvalidate();
	}
	
	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		super.invalidate();
	}
	
	@Override
	protected void handleDataChanged() {
		// TODO Auto-generated method stub
		super.handleDataChanged();
	}
	
	public void updateLayout() {
		ViewGroup.LayoutParams params = getLayoutParams();
		if (getCount() > 0
				&& getChildAt(0) != null)
			params.height = getCount() * getChildAt(0).getMeasuredHeight() + getDividerHeight() * (getCount() - 1);
		else
			params.height = 0;
		setLayoutParams(params); 
		requestLayout();
	}
	
	public int setHeight() {
		layoutChildren();
		return 0;
	}

}
