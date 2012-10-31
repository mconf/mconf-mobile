package org.mconf.android.core.voip;

import org.sipdroid.codecs.Codec;
import org.sipdroid.codecs.Speex;
import org.sipdroid.sipua.ui.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import com.flazr.rtmp.message.Audio;

public class RtmpAudioPlayer {
	private static final Logger log = LoggerFactory.getLogger(RtmpAudioPlayer.class);
	/** Size of the read buffer */
	public static final int BUFFER_SIZE = 1024;

	private Codec codec = new Speex();
	private AudioTrack audioTrack;
	private int mu, maxjitter;
	private boolean running = false;
	private short[] decodedBuffer = new short[BUFFER_SIZE];
	private byte[] pktBuffer = new byte[BUFFER_SIZE + 12];
//	private RtpPacket pkt = new RtpPacket(pktBuffer, 0);
	
	public void start() {
		codec.init();
		mu = codec.samp_rate()/8000;
		maxjitter = AudioTrack.getMinBufferSize(codec.samp_rate(), 
				AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		if (maxjitter < 2*2*BUFFER_SIZE*3*mu)
			maxjitter = 2*2*BUFFER_SIZE*3*mu;
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, codec.samp_rate(), AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
				maxjitter, AudioTrack.MODE_STREAM);
//		AudioManager am = (AudioManager) Receiver.mContext.getSystemService(Context.AUDIO_SERVICE);
//		if (Integer.parseInt(Build.VERSION.SDK) >= 5)
//			am.setSpeakerphoneOn(true);
//		else
//			am.setMode(AudioManager.MODE_NORMAL);
		audioTrack.play();

		running = true;
	}
	
	private void write(short a[],int b,int c) {
		synchronized (this) {
			audioTrack.write(a,b,c);
		}
	}
	
	public void stop() {
		running = false;
		
		codec.close();
		
		if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED)
				audioTrack.stop();
		audioTrack.release();
	}
	
	public void onAudio(Audio audio) {
		if (running) {
			byte[] audioData = audio.getByteArray();
			
			int offset = 1;

//			byte[] tmpBuffer = new byte[audioData.length - offset];
//			System.arraycopy(audioData, offset, tmpBuffer, 0, tmpBuffer.length);
//			pkt.setPayload(tmpBuffer, tmpBuffer.length);
//			int decodedSize = codec.decode(pktBuffer, decodedBuffer, pkt.getPayloadLength());
			
			System.arraycopy(audioData, offset, pktBuffer, 12, audioData.length - offset);
			int decodedSize = codec.decode(pktBuffer, decodedBuffer, audioData.length - offset);
			
			write(decodedBuffer, 0, decodedSize);
		}
	}
}
