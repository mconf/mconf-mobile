package org.mconf.android.core.voip;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.phone.BbbVoiceConnection;

import android.os.SystemClock;
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
	public int start() {
		player.start();
		connection.start();
		
		int cont = 10;
		while (!onCall && cont > 0) {
			SystemClock.sleep(500);
			cont--;
		}
		if (cont == 0) {
			stop();
			return E_TIMEOUT;
		} 
		
		else 
			return E_OK;
		
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
