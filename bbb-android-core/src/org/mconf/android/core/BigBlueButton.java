package org.mconf.android.core;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.mconf.android.core.video.CaptureConstants;
import org.mconf.android.core.video.VideoPublish;
import org.mconf.android.core.voip.VoiceModule;
import org.mconf.bbb.BigBlueButtonClient;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;

@ReportsCrashes(formKey="dHVlSktWX2lUb1pWMzFIb1kxcE5YblE6MQ")
public class BigBlueButton extends Application {
	private BigBlueButtonClient handler = null;
	private VoiceModule voice = null;
	private VideoPublish mVideoPublish = null;
	private boolean restartCaptureWhenAppResumes = false;
	
	private int framerate = CaptureConstants.DEFAULT_FRAME_RATE;
    private int width = CaptureConstants.DEFAULT_WIDTH;
    private int height = CaptureConstants.DEFAULT_HEIGHT;
    private int bitrate = CaptureConstants.DEFAULT_BIT_RATE;
    private int gop = CaptureConstants.DEFAULT_GOP;
	
	private int launchedBy = LAUNCHED_BY_NON_SPECIFIED;
	public static final int LAUNCHED_BY_NON_SPECIFIED = 0;
	public static final int LAUNCHED_USING_DEMO = 1;
	public static final int LAUNCHED_USING_URL = 2;
	
	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		if (!isDebug())
			ACRA.init(this);
		
		// http://stackoverflow.com/questions/2879455/android-2-2-and-bad-address-family-on-socket-connect
		System.setProperty("java.net.preferIPv6Addresses", "false");
        super.onCreate();
	}
	
	public boolean isDebug() {
		return ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0);
	}
	
	public BigBlueButtonClient getHandler() {
		if (handler == null)
			handler = new BigBlueButtonClient();
		return handler;
	}
	
	public VoiceModule getVoiceModule() {
		// the application could call getVoiceModule before connect to a meeting, so additional tests must be applied
		if (voice == null 
				&& getHandler().getJoinService() != null
				&& getHandler().getJoinService().getJoinedMeeting() != null
				&& getHandler().getJoinService().getJoinedMeeting().getReturncode().equals("SUCCESS"))
			voice = new VoiceModule(this,
					getHandler().getJoinService().getJoinedMeeting().getFullname(),
					getHandler().getJoinService().getApplicationService().getServerUrl()); 
		return voice;
	}
	
	public VideoPublish getVideoPublish() {
		if(mVideoPublish == null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			mVideoPublish = new VideoPublish(getHandler(), restartCaptureWhenAppResumes, framerate, width, height, bitrate, gop, Integer.parseInt(prefs.getString("video_rotation", "0")));
		}
		return mVideoPublish;
	}
	
	public VideoPublish deleteVideoPublish() {
		if(mVideoPublish != null){
			restartCaptureWhenAppResumes = mVideoPublish.restartWhenResume;
			
			framerate = mVideoPublish.getFramerate();
			width = mVideoPublish.getWidth();
			height = mVideoPublish.getHeight();
			bitrate = mVideoPublish.getBitrate();
			gop = mVideoPublish.getGop();
			
			mVideoPublish = null;
		}
		return mVideoPublish;
	}

	public void invalidateVoiceModule() {
		if (voice != null)
			voice.hang();
		voice = null;
	}
	
	/*
	 *	GETTERS AND SETTERS
	 */
	
	public void setLaunchedBy(int launchedBy) {
		this.launchedBy = launchedBy;
	}

	public int getLaunchedBy() {
		return launchedBy;
	}
}
