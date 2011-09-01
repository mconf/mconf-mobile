package org.mconf.bbb.android;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.voip.VoiceModule;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

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
	
	public void makeToast(final int resId) {
		makeToast(getResources().getString(resId));
	}

	public void makeToast(final String s) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
