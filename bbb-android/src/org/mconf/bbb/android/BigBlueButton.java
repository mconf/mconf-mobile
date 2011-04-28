package org.mconf.bbb.android;

import org.mconf.bbb.BigBlueButtonClient;

import android.app.Application;
import android.content.Context;

public class BigBlueButton extends Application {
	public static BigBlueButtonClient handler = new BigBlueButtonClient();

	public static final int LAUNCHED_BY_NON_SPECIFIED = 0;
	public static final int LAUNCHED_BY_APPLICATION = 1;
	public static final int LAUNCHED_BY_BROWSER = 2;
	public static int launchedBy = LAUNCHED_BY_NON_SPECIFIED;
	
//	public static BigBlueButton getApp(Context context) {
//		return (BigBlueButton) context.getApplicationContext();
//	}
}
