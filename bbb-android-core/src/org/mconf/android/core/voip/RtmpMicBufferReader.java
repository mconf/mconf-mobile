package org.mconf.android.core.voip;

import org.sipdroid.codecs.Codec;
import org.sipdroid.codecs.Speex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.Metadata;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RtmpMicBufferReader implements RtmpReader {
	
	private static final Logger log = LoggerFactory.getLogger(RtmpMicBufferReader.class);
	
	private int frame_size;
	private int frame_rate;
	private int numOfReadShorts;
	private int initialPosition;
	private int ring = 0;
	private int delay = 0;
	private short[] audioData;
	
	private Codec codec = new Speex();
	byte[] audioEncoded;
	
	
	private AudioRecord record = null;
	private boolean muted = false;
	
	
	public RtmpMicBufferReader() 
	{
		int minBufferSize = AudioRecord.getMinBufferSize(codec.samp_rate(), 
														 AudioFormat.CHANNEL_CONFIGURATION_MONO, 
														 AudioFormat.ENCODING_PCM_16BIT);
		
		record = new AudioRecord(MediaRecorder.AudioSource.MIC, codec.samp_rate(), AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 
				minBufferSize);
		
		
		frame_size = 160; //initial frame size of the speex codec according to RtpStreamSender		
		updateFrameSize(minBufferSize);
		
		frame_rate = codec.samp_rate()/frame_size; 
		frame_rate *= 1.5;
		
		audioEncoded = new byte[frame_size];
		audioData = new short[frame_size*(frame_rate+1)];
		
		codec.init();
		
		record.startRecording();
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
		log.debug("\n");
		log.debug("hasNext");
		log.debug("\n");
		
		if(!isMuted())
		{
//			initialPosition = (ring+delay*frame_rate*frame_size)%(frame_size*(frame_rate+1));	
//			numOfReadShorts = record.read(audioData,initialPosition,frame_size);
			numOfReadShorts = record.read(audioData,0,frame_size);
			
			return (numOfReadShorts != AudioRecord.ERROR_BAD_VALUE &&
					numOfReadShorts != AudioRecord.ERROR_INVALID_OPERATION &&
					numOfReadShorts > 0);
		}
		
		return false;
	}
	
	
	@Override
	public RtmpMessage next()
	{
		log.debug("\n");
		log.debug("next");
		log.debug("\n");
		
//		int sizeEncodedAudio = codec.encode(audioData,
//											ring%(frame_size*(frame_rate+1)), 
//											audioEncoded, 
//											numOfReadShorts);
		
		// O que é o último parâmetro (int frames) ?
		int sizeEncodedAudio = codec.encode(audioData, 0, audioEncoded, 1);
		   
		ring += frame_size;
		
		String firstRawBytes = "";
   		for( int i =0; i < 10; i++ )
   			firstRawBytes += Short.toString(audioData[i]);
   		log.debug("\n Raw: {} \n", firstRawBytes);
   		
   		String firstEncodedBytes = "";
   		for( int i =0; i < sizeEncodedAudio+12; i++ )
	   		firstEncodedBytes += Byte.toString(audioEncoded[i]);
   		log.debug("\n Encoded: {} \n", firstEncodedBytes);
		   
		//usar sizeEncodedAudio e o array audioEncoded e fazer uma nova rmtp message	e mandar...
		final byte[] validBytes = new byte[sizeEncodedAudio];
		System.arraycopy(audioEncoded, 0, validBytes, 0, sizeEncodedAudio);
		
   		Audio audioPacket = new Audio(validBytes);
   
   		return audioPacket;										
	}


	@Override
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RtmpMessage[] getStartMessages() {
		// TODO Auto-generated method stub
		return new Audio[0];
	}


	@Override
	public void setAggregateDuration(int targetDuration) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public long getTimePosition() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long seek(long timePosition) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private void updateFrameSize(int min)
	{
		if (min == 640) {
			if (frame_size == 960) frame_size = 320;
			if (frame_size == 1024) frame_size = 160;
			min = 4096*3/2;
		} else if (min < 4096) {
			if (min <= 2048 && frame_size == 1024) frame_size /= 2;
			min = 4096*3/2;
		} else if (min == 4096) {
			min *= 3/2;
			if (frame_size == 960) frame_size = 320;
		} else {
			if (frame_size == 960) frame_size = 320;
			if (frame_size == 1024) frame_size *= 2;
			/**
			 * Modified by the Mconf team
			 * this parameter is to avoid the log message "RecordThread: buffer overflow"
			 */
			min *= 4;
		}		
	}
		
		
			
		
	}

