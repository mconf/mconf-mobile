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
	private long baseDelay; // ms
	
	private boolean running;
	
	public RtmpAudioPublisher() {
		
		running = false;
		
		codec = new Speex();
		codec.init();

		sampRate = 8000;
		frameSize = 160;
		
		/*
		 * sampling rate * sample size / frame size
		 */
		frameRate = ( sampRate * 2 ) / frameSize;
		
		/*
		 * Base delay used in the audio reading loop
		 */
		baseDelay = (long) 1000/frameRate;
		
		int minBufferSize =	AudioRecord.getMinBufferSize(sampRate,
							AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
		
		minBufferSize *= 2;
		
		recorder =	new AudioRecord(AudioSource.MIC, sampRate,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
		
		messageBuffer = new ArrayList<Audio>();
		
		recordBuffer = new short[frameSize];
		encodedBuffer = new byte[frameSize];
	}
	
	@Override
	public void run() {
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		recorder.startRecording();
		
		running = true;
		
		int readShorts = 0;
		int encodedSize = 0;
		
		long startTime = 0;
		long delayToUse = 0;
		
		while(running)
		{
			startTime = System.currentTimeMillis();
			
			readShorts = recorder.read(recordBuffer, 0, frameSize);
			encodedSize = codec.encode(recordBuffer, 0, encodedBuffer, readShorts);
			
			final byte[] dataToSend = new byte[encodedSize];
			System.arraycopy(encodedBuffer, 12, dataToSend, 0, encodedSize);
			
			Audio audioPacket = new Audio(dataToSend);
			
			/* Cabeçalho e tal... Tem que ver */
			audioPacket.getHeader().setDeltaTime(42);
			
			messageBuffer.add(audioPacket);
			
			delayToUse = baseDelay-(System.currentTimeMillis()-startTime);
			
			if(delayToUse > 0) {
				try {
					Thread.sleep(delayToUse);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		stopAudioCapture();
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
		running = false;
	}

	@Override
	public boolean hasNext() {
		log.debug("\n");
		log.debug("Calling hasNext()");
		log.debug("\n");
		
		return true;
		//return messageBuffer.isEmpty() ? false : true;
	}

	@Override
	public RtmpMessage next() {
		
		log.debug("\n");
		log.debug("Calling next()");
		log.debug("\n");
		
		if(!messageBuffer.isEmpty()) {
			
			return messageBuffer.remove(0);
		}
		else{
			
			return new Audio(new byte[0]);	
		}
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
