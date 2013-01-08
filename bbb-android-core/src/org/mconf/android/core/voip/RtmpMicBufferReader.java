package org.mconf.android.core.voip;

import org.sipdroid.codecs.Codec;
import org.sipdroid.codecs.Speex;

import com.flazr.io.flv.FlvReader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpReader;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RtmpMicBufferReader implements RtmpReader {
	
	private Codec codec = new Speex();
	private AudioRecord record = null;
	private boolean muted = true;
	
	//ver rtpStreamSender
	private short[] audioData = new short[tamanho?];
	
	public RtmpMicBufferReader() 
	{
		codec.init();
		int min = AudioRecord.getMinBufferSize(codec.samp_rate(), 
				AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		
		
		record = new AudioRecord(MediaRecorder.AudioSource.MIC, codec.samp_rate(), AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 
				min);
		
	}

	
	public void mute()
	{
		record.stop();
		muted = true;
	}
	
	
	public void unmute()
	{
		record.startRecording();
		muted = false;
	}
	
	
	public boolean isMuted()
	{
		return muted;
	}
	
	
	@Override
	public void close()
	{
		if(record != null)
		{
			record.stop();
			record.release();
			record = null;
			codec.close();
		}
	}
	
	
	@Override
	public boolean hasNext()
	{
		return !isMuted(); 
	}
	
	
	@Override
	public RtmpMessage next()
	{
		  
		//ver rtpStreamSender 		
	    int  = record.read(audioData,  int offsetInShorts, int sizeInShorts);
	    byte[] audioEncoded = new byte[tamanho?];
			
			
		// ver RtmpAudioPlayer, cuidar offset...
		int sizeEncodedAudio = codec.encode(audioData, offset?, audioEncoded, frames?);			
			
		//joga dados dentro do rtmpmessage	e retorná-lo		
		return;		
														
	}
		
		
			
		
	}

