package org.mconf.android.core;

import org.mconf.android.core.voip.VoiceInterface;
import org.mconf.android.core.voip.VoiceModule;
import org.mconf.bbb.BigBlueButtonClient;

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
	
	protected VoiceInterface getVoiceInterface()
	{
		return getGlobalContext().getVoiceInterface(getBigBlueButton());
	}
		
	public boolean isNetworkDown() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		//getNetworkInfo(MOBILE) returns null in xoom, so check null first
		NetworkInfo mobile_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		return !( (mobile_info!=null && mobile_info.getState() == NetworkInfo.State.CONNECTED)
		|| (wifi_info!=null && wifi_info.getState() == NetworkInfo.State.CONNECTED) );
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
