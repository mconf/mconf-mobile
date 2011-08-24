package org.mconf.bbb.android;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.voip.VoiceModule;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
		
	public boolean isNetworkDown() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return !(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
				||  connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
	}
	
}
