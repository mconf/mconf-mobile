package org.mconf.bbb.android;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ListView;

// creates a list view with scrolling disabled
public class CustomListview extends ListView {

	public CustomListview(Context context, AttributeSet attrs) {
		super(context,attrs);
		
		setDivider(getResources().getDrawable(R.color.title_background_onfocus));
		setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		ViewGroup.LayoutParams params = getLayoutParams();
		if (getChildCount() > 0
				&& getChildAt(0) != null)
			params.height = getCount() * getChildAt(0).getMeasuredHeight() + getDividerHeight() * (getCount() - 1);
		else
			params.height = 0;
		setLayoutParams(params); 
		requestLayout();
	}
	
	public void updateLayout() {
	}
	
	
	public int setHeight() {
		layoutChildren();
		return 0;
	}

}
