package org.mconf.android.core.voip;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.phone.BbbVoiceConnection;

import android.os.SystemClock;
import android.util.Log;

import com.flazr.rtmp.message.Audio;

public class VoiceOverRtmp implements VoiceInterface {

	private BbbVoiceConnection connection;
	private RtmpAudioPlayer audioPlayer = new RtmpAudioPlayer();
	//private RtmpMicBufferReader micBufferReader = new RtmpMicBufferReader();
	private RtmpAudioPublisher micBufferReader = new RtmpAudioPublisher();
	protected boolean onCall = false;
	protected OnCallListener listener;
	
	public VoiceOverRtmp(BigBlueButtonClient bbb) {
		
		micBufferReader.start();	
		
		connection = new BbbVoiceConnection(bbb, micBufferReader) {
			@Override
			protected void onAudio(Audio audio) {
				audioPlayer.onAudio(audio);
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
			audioPlayer.start();
			return E_OK;
		}
		
	}

	@Override
	public void stop() {
		onCall = false;
		micBufferReader.stopRunning();
		connection.stop();
		audioPlayer.stop();
		listener.onCallFinished();
	}

	@Override
	public boolean isOnCall() {
		return onCall;
	}

	@Override
	public boolean isMuted() {
		return micBufferReader.isMuted();
	}

	@Override
	public void muteCall(boolean mute) {
		if(mute)
			micBufferReader.mute();
		else
			micBufferReader.unmute();
		
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
