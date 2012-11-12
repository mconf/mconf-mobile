package org.mconf.android.core.voip;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.phone.BbbVoiceConnection;

import android.os.SystemClock;
import android.util.Log;

import com.flazr.rtmp.message.Audio;

public class VoiceOverRtmp implements VoiceInterface {

	private BbbVoiceConnection connection;
	private RtmpAudioPlayer audioReceiver = new RtmpAudioPlayer();
	protected boolean onCall = false;
	protected OnCallListener listener;
	
	public VoiceOverRtmp(BigBlueButtonClient bbb) {
		connection = new BbbVoiceConnection(bbb, null) {
			@Override
			protected void onAudio(Audio audio) {
				audioReceiver.onAudio(audio);
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
		{
			audioReceiver.start();
			return E_OK;
		}
		
	}

	@Override
	public void stop() {
		onCall = false;
		connection.stop();
		audioReceiver.stop();
		listener.onCallFinished();
	}

	@Override
	public boolean isOnCall() {
		return onCall;
	}

	@Override
	public boolean isMuted() {
		return true;
	}

	@Override
	public void muteCall(boolean mute) {
		//aqui tenho que dar um send.mute()
		
	}

	@Override
	public int getSpeaker() {
		return 0;
	}

	@Override
	public void setSpeaker(int mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setListener(OnCallListener listener) {
		this.listener = listener;	
	}

}
