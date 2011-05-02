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

	private VoiceModule voice;
	
	public AudioBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private void hide() {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = 0;
		setLayoutParams(params);
	}
	
	private void show() {
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
	
	public void setListeners(final VoiceModule voice) {
		this.voice = voice;
		
		Button tapToSpeak = (Button) findViewById(R.id.taptospeak);	
		Button lockSpeak = (Button) findViewById(R.id.lockspeak);

		tapToSpeak.setOnTouchListener(new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (voice != null)
						voice.muteCall(false);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (voice != null)
						voice.muteCall(true);
				}
				return false;
			}
		});

		lockSpeak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (voice != null) {
					boolean muted = !voice.isMuted();
					voice.muteCall(muted);
					setLockSpeak(!muted);
				}
			}
		});

		updateUI();
	}
	
	public void updateUI() {
		if (voice != null && voice.isOnCall()) {
			show();
			setLockSpeak(!voice.isMuted());
		} else
			hide();
	}
}
