package org.mconf.android.core.voip;

import java.util.ArrayList;
import java.util.List;

import org.sipdroid.codecs.Codec;
import org.sipdroid.codecs.Speex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.Metadata;

public class RtmpAudioPublisher extends Thread implements RtmpReader {
	
	private static final Logger log = LoggerFactory.getLogger(RtmpAudioPublisher.class);
	
	private List<Audio> messageBuffer;
	
	private Codec codec;

	private AudioRecord recorder;
	
	private short[] recordBuffer;
	private byte[] encodedBuffer;
	
	private int sampRate; // Hertz
	private int frameSize; // byte
	private int frameRate; // FPS
	private long frameDuration; // ms
	
	private boolean running;
	
	private boolean muted = true;
	
	private int currentTimestamp = 0;
	private int lastTimestamp = 0;
	private int interval;
	
	public RtmpAudioPublisher() {
		
		running = false;
		
		codec = new Speex();
		codec.init();

		sampRate = codec.samp_rate();
		frameSize = codec.frame_size();
		
		/*
		 * sampling rate * sample size / frame size
		 */
		frameRate = ( sampRate * 2 ) / frameSize;
		
		/*
		 * Base delay used in the audio reading loop
		 */
		frameDuration = (long) 1000/frameRate;
		
		int minBufferSize =	AudioRecord.getMinBufferSize(sampRate,
							AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
		
		minBufferSize *= 2;
		
		recorder =	new AudioRecord(AudioSource.MIC, sampRate,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
		
		messageBuffer = new ArrayList<Audio>();
		
		recordBuffer = new short[frameSize];
		encodedBuffer = new byte[12 + frameSize];
	}
	
	@Override
	public void run() {
		
		//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int readShorts = 0;
		int encodedSize = 0;
		
		long startTime = 0;
		long delayToUse = 0;
		
		running = true;
		recorder.startRecording();
		
		while(running)
		{
			
			while(muted && running)
			{
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			//necessary to end this thread properly: 
			//user may have closed the audio service while the loop above was active
			if(!running)
				break;
				
			startTime = System.currentTimeMillis();
			
			readShorts = recorder.read(recordBuffer, 0, frameSize);
			
			if(readShorts != AudioRecord.ERROR_BAD_VALUE &&
			   readShorts != AudioRecord.ERROR_INVALID_OPERATION &&
			   readShorts > 0)
			{
					encodedSize = codec.encode(recordBuffer, 0, encodedBuffer, readShorts);
					
					final byte[] dataToSend = new byte[encodedSize];
					System.arraycopy(encodedBuffer, 12, dataToSend, 0, encodedSize);
					
					Audio audioPacket = new Audio(dataToSend);
					
					audioPacket.getHeader().setTime(currentTimestamp);
					
					interval = currentTimestamp - lastTimestamp;
					audioPacket.getHeader().setDeltaTime(interval);
					
					lastTimestamp = currentTimestamp;
					currentTimestamp += frameSize;
					
					log.debug("@@@@@@@@@@@@@@ adding new audio on the list... @@@@@@@@@@@@@@@@@@");
					messageBuffer.add(audioPacket);
					
					delayToUse = frameDuration-(System.currentTimeMillis()-startTime);					
					if(delayToUse > 0) {
						try {
							Thread.sleep(delayToUse);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}		
						
			}
		}
		
		messageBuffer.clear();
		messageBuffer = null;
		
	}
	
	public void mute()
	{
		muted = true;
	}
	
	public void unmute()
	{
		muted = false;
	}
	
	public boolean isMuted()
	{
		return muted;
	}
	
	public void stopRunning()
	{
		running = false;
	}
	
	
	@Override
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RtmpMessage[] getStartMessages() {
		
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
	public void close() {
		stopAudioCapture();
	}

	@Override
	public boolean hasNext() {
		return running;
	}

	@Override
	public RtmpMessage next() {
		
		if(messageBuffer != null)
		{
			if(messageBuffer.isEmpty())
			{
				Audio foo = new Audio(new byte[0]);
				
				foo.getHeader().setTime(currentTimestamp);
				
				interval = currentTimestamp - lastTimestamp;
				foo.getHeader().setDeltaTime(interval);
				
				lastTimestamp = currentTimestamp;
				currentTimestamp += frameSize;
				
				log.debug("**********sending dummy**********");
				return foo;
			}
			
			log.debug("#########sending audio##########");
			return messageBuffer.remove(0);
		}
		
		//log.debug("\n\n\n###############publisher stopped running => send null to finish connection");	
		return null;
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
	
	private void stopAudioCapture() {
		
		if(recorder != null) {
			
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		
		codec.close();
	}
	
	
}
