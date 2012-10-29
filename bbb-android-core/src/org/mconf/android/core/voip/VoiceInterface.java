package org.mconf.android.core.voip;

public interface VoiceInterface {
	
	public static final int E_OK = 0;
	public static final int E_INVALID_NUMBER = 1;
	public static final int E_TIMEOUT = 2; 
	
	public int start();
	public void stop();
	public boolean isOnCall();
	public void setListener(OnCallListener listener);
}
