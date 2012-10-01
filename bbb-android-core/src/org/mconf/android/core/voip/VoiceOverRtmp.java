package org.mconf.android.core.voip;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.phone.BbbVoiceConnection;

import com.flazr.rtmp.message.Audio;

public class VoiceOverRtmp implements VoiceInterface {

	private BbbVoiceConnection connection;
	private RtmpAudioPlayer player = new RtmpAudioPlayer();
	private boolean onCall = false;
	
	public VoiceOverRtmp(BigBlueButtonClient bbb) {
		connection = new BbbVoiceConnection(bbb, null) {
			@Override
			protected void onAudio(Audio audio) {
				player.onAudio(audio);
			}
		};
		
	}

	@Override
	public void start() {
		player.start();
		connection.start();
		onCall = true;
	}

	@Override
	public void stop() {
		onCall = false;
		connection.stop();
		player.stop();		
	}

	@Override
	public boolean isOnCall() {
		return onCall;
	}

}
