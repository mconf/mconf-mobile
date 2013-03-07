package org.mconf.android.core.voip;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffers;
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

public class AudioPublish extends Thread implements RtmpReader {
	
	private static final Logger log = LoggerFactory.getLogger(AudioPublish.class);
	
	private final int SHORT_SIZE_IN_BYTES = 2;
	
	private List<Audio> audioBuffer;
	
	private Codec codec;

	private AudioRecord recorder;
	
	private short[] recordBuffer;
	private byte[] encodedBuffer;
	
	private int sampSize; // bytes
	private int sampRate; // Hertz
	
	private int frameSize; // samples
	private int frameRate; // FPS
	
	private int frameDuration; // ms
	
	private int frameSizeInShorts;
	
	private boolean running;
	
	private boolean muted = true;
	
	private int currentTimestamp;
	private int lastTimestamp;
	
	public AudioPublish() {
		
		running = false;
		
		codec = new Speex();
		
		
		codec.init();
		
		sampRate = codec.samp_rate(); // samples per second
		sampSize = 2; //2 bytes per sample => ENCODING PCM 16BIT
		
		frameSize = codec.frame_size(); //number of SAMPLES which a FRAME has
		
		frameRate = sampRate / frameSize;		
		frameDuration =  1000/frameRate;
		
		frameSizeInShorts = frameSize*(sampSize/SHORT_SIZE_IN_BYTES);
		
		int minBufferSize =	AudioRecord.getMinBufferSize(sampRate,
							AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
		
		minBufferSize *= 2;
		
		recorder =	new AudioRecord(AudioSource.MIC, sampRate,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
		
		
		recordBuffer = new short[frameSizeInShorts];
		encodedBuffer = new byte[12 + frameSize*sampSize];
		
		//setFirstAudioPacket();
		currentTimestamp = 0;		
		lastTimestamp = 0;
		audioBuffer = new ArrayList<Audio>();
	}
	
	@Override
	public void run() {
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

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
					e.printStackTrace();
				}
			}
			
			
			//necessary to end this thread properly: 
			//user may have closed the audio service while the loop above was active
			if(!running)
				break;
				
			startTime = System.currentTimeMillis();
		
			//reading 160 samples (1 frame)
			readShorts = recorder.read(recordBuffer, 0, frameSizeInShorts);
			
			if(readShorts != AudioRecord.ERROR_BAD_VALUE &&
			   readShorts != AudioRecord.ERROR_INVALID_OPERATION &&
			   readShorts > 0)
			{
					encodedSize = codec.encode(recordBuffer, 0, encodedBuffer, readShorts);
					
					int offset = 1;
					
					final byte[] dataToSend = new byte[encodedSize+offset];
					System.arraycopy(encodedBuffer, 12, dataToSend, offset, encodedSize);
					
					Audio audioPacket = new Audio(dataToSend);
					
					audioPacket.getHeader().setTime(currentTimestamp);		
					
					int interval = currentTimestamp - lastTimestamp;
					audioPacket.getHeader().setDeltaTime(interval);
					
					lastTimestamp = currentTimestamp;
					currentTimestamp += frameDuration;		
										
					audioBuffer.add(audioPacket);
					
					synchronized(this) {
						this.notify();}
										
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
		
		//ending thread
		releaseResources();
		log.debug("\n\n\nAll resources of the audio capture released!\n\n\n");
		log.debug("");
		
	}
	
//	private void setFirstAudioPacket()
//	{
//		//timestamps iniciais...talvez tenha que mudar os valores
//		currentTimestamp = 200;		
//		lastTimestamp = 200;
//		
//		Audio firstAudio = Audio.empty();			
//		firstAudio.getHeader().setTime(currentTimestamp);
//		
//		int interval = currentTimestamp - lastTimestamp;
//		firstAudio.getHeader().setDeltaTime(interval);
//		
//		currentTimestamp += frameDuration;				
//		
//		audioBuffer = new ArrayList<Audio>();
//		audioBuffer.add(firstAudio);		
//	}
	
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
	
    private void releaseResources()
    {
    	if(audioBuffer != null) {
    		audioBuffer.clear();
    		audioBuffer = null;
    	}
    	
		if(recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		
		if(codec != null) {
			codec.close();
			codec = null; 
		}   		
		
    }
	
	
	@Override
	public Metadata getMetadata() {

		return null;
	}

	@Override
	public RtmpMessage[] getStartMessages() {
		
		return new Audio[0];
	}

	@Override
	public void setAggregateDuration(int targetDuration) {
		
	}

	@Override
	public long getTimePosition() {
		
		return 0;
	}

	@Override
	public long seek(long timePosition) {
		
		return 0;
	}

	@Override
	public void close() {
		
		log.debug("\n\nCalling close on audio capture...\n\n");
		running = false;		
	}

	@Override
	public boolean hasNext() {
		
		if(audioBuffer != null && audioBuffer.isEmpty())
		{
			try {
				
				this.wait();
				
			} catch (InterruptedException e) {
				log.debug("\n\n\n\nException on AudioPublish , hasNext method, threw by this.wait() line\n\n\n\n");
				return false;
			}
		}
			
		return running;
	}

	@Override
	public RtmpMessage next() {
		
		if(audioBuffer != null)
		{
			if(audioBuffer.isEmpty())
			{
				Audio emptyAudio = Audio.empty();
				
				//emptyAudio = setAudioPacketTimestamp(emptyAudio,frameSize); 
				//log.debug("#######mandando audio VAZIO########\n\n\n");
				return emptyAudio;
			}
			
			//log.debug("#######mandando audio########\n\n\n");
			return audioBuffer.remove(0);
		}
		
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
}
