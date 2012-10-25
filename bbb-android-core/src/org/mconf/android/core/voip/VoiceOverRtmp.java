package org.mconf.android.core.voip;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.phone.BbbVoiceConnection;

import android.util.Log;

import com.flazr.rtmp.message.Audio;

public class VoiceOverRtmp implements VoiceInterface {

	private BbbVoiceConnection connection;
	private RtmpAudioPlayer player = new RtmpAudioPlayer();
	protected boolean onCall = false;
	protected OnCallListener listener;
	
	public VoiceOverRtmp(BigBlueButtonClient bbb) {
		connection = new BbbVoiceConnection(bbb, null) {
			@Override
			protected void onAudio(Audio audio) {
				player.onAudio(audio);
			}
			
			@Override
			protected void onConnectedSuccessfully() {
				onCall = true;
				listener.onCallStarted();
			}
		};
		
	}

	@Override
	public void start() {
		player.start();
		connection.start();
	}

	@Override
	public void stop() {
		onCall = false;
		connection.stop();
		player.stop();
		listener.onCallFinished();
	}

	@Override
	public boolean isOnCall() {
		return onCall;
	}
	
	@Override
	public void setListener(OnCallListener listener) {
		this.listener = listener;	
	}

}
