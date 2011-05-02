package org.mconf.bbb.android;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.voip.VoiceModule;

import android.app.Activity;

public class BigBlueButtonActivity extends Activity {
	
	protected BigBlueButton getGlobalContext() {
		return (BigBlueButton) getApplicationContext();
	}
	
	protected BigBlueButtonClient getBigBlueButton() {
		return getGlobalContext().getHandler();
	}
	
	protected VoiceModule getVoiceModule() {
		return getGlobalContext().getVoiceModule();
	}
		
}
