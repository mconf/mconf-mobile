package org.mconf.bbb.android.voip;

import org.mconf.bbb.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
	
	public void show(boolean muted) {
		setLockSpeak(!muted);
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		setLayoutParams(params);
	}

	private void setLockSpeak(boolean lock) {
		Button tapToSpeak = (Button) findViewById(R.id.taptospeak);	
		Button lockSpeak = (Button) findViewById(R.id.lockspeak);
		if (lock) {
			lockSpeak.setCompoundDrawablesWithIntrinsicBounds(0,android.R.drawable.button_onoff_indicator_on,0,0);
			tapToSpeak.setEnabled(false);
		} else {
			lockSpeak.setCompoundDrawablesWithIntrinsicBounds(0,android.R.drawable.button_onoff_indicator_off,0,0);
			tapToSpeak.setEnabled(true);
		}
	}
	
	public interface Listener {
		public boolean isOnCall();
		public boolean isMuted();
		public void muteCall(boolean mute);
	}
	
	public void initListener(final AudioBarLayout.Listener listener) {
		final Button tapToSpeak = (Button) findViewById(R.id.taptospeak);
		final Button lockSpeak = (Button) findViewById(R.id.lockspeak);

		tapToSpeak.setOnTouchListener(new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					listener.muteCall(false);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					listener.muteCall(true);
				return false;
			}
		});

		lockSpeak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tapToSpeak.isEnabled()) {
					setLockSpeak(true);
					listener.muteCall(false);
				} else {
					setLockSpeak(false);
					listener.muteCall(true);
				}
			}
		});
		
		if (listener.isOnCall()) {
			show(listener.isMuted());
		} else
			hide();
	}
	
}
