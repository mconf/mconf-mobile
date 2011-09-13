package org.mconf.bbb.android;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.video.CaptureConstants;
import org.mconf.bbb.android.video.VideoPublish;
import org.mconf.bbb.android.voip.VoiceModule;

import android.app.Application;
import android.content.pm.ApplicationInfo;

@ReportsCrashes(formKey="dHVlSktWX2lUb1pWMzFIb1kxcE5YblE6MQ")
public class BigBlueButton extends Application {
	private BigBlueButtonClient handler = null;
	private VoiceModule voice = null;
	private VideoPublish mVideoPublish = null;
	private boolean restartCaptureWhenAppResumes = false;
	
	private int frameRate = CaptureConstants.DEFAULT_FRAME_RATE;
    private int width = CaptureConstants.DEFAULT_WIDTH;
    private int height = CaptureConstants.DEFAULT_HEIGHT;
    private int bitRate = CaptureConstants.DEFAULT_BIT_RATE;
    private int GOP = CaptureConstants.DEFAULT_GOP;
	
	private int launchedBy = LAUNCHED_BY_NON_SPECIFIED;
	public static final int LAUNCHED_BY_NON_SPECIFIED = 0;
	public static final int LAUNCHED_USING_DEMO = 1;
	public static final int LAUNCHED_USING_URL = 2;
	
	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		if (!isDebug())
			ACRA.init(this);
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
					getHandler().getJoinService().getServerUrl()); 
		return voice;
	}
	
	public VideoPublish getVideoPublish() {
		if(mVideoPublish == null) {
			mVideoPublish = new VideoPublish(getHandler(), restartCaptureWhenAppResumes, frameRate, width, height, bitRate, GOP);
		}
		return mVideoPublish;
	}
	
	public VideoPublish deleteVideoPublish() {
		if(mVideoPublish != null){
			restartCaptureWhenAppResumes = mVideoPublish.restartWhenResume;
			
			frameRate = mVideoPublish.frameRate;
			width = mVideoPublish.width;
			height = mVideoPublish.height;
			bitRate = mVideoPublish.bitRate;
			GOP = mVideoPublish.GOP;
			
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
