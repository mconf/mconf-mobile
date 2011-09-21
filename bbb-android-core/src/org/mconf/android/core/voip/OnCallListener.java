package org.mconf.android.core.voip;

public interface OnCallListener {

	public void onCallStarted();
	public void onCallFinished();
	public void onCallRefused();
}
