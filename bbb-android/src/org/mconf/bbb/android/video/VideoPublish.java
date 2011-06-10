package org.mconf.bbb.android.video;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.video.IVideoListener;
import org.mconf.bbb.video.IVideoPublishListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpEncoder;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpPublisher;
import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.Metadata;
import com.flazr.rtmp.message.Video;

public class VideoPublish extends Thread implements RtmpReader {
	private class VideoPublishHandler extends IVideoPublishListener {
		
		public VideoPublishHandler(int userId, String streamName, RtmpReader reader, BigBlueButtonClient context) {			
			super(userId, streamName, reader, context);
		}
				
	}
	
	private static final Logger log = LoggerFactory.getLogger(VideoPublish.class);
   
    private byte[] sharedBuffer;
    
    private int firstTimeStamp = 0;
	private int lastTimeStamp = 0;
	
	private VideoPublishHandler videoPublishHandler;
	        
    public VideoPublish(int userId, int bufSize, int widthCaptureResolution, int heightCaptureResolution, int frameRate) {
    	sharedBuffer = new byte[bufSize]; //the encoded frame will never be bigger than the not encoded
    	initEncoder(widthCaptureResolution, heightCaptureResolution, frameRate);
    	
    	String streamId = widthCaptureResolution+"x"+heightCaptureResolution+userId; 
    		
    	videoPublishHandler = new VideoPublishHandler(userId, streamId, this, Client.bbb);
    	videoPublishHandler.start();
    }
    
    /** Runs it in a new Thread. */
    @Override
    public void run() {
    	initSenderLoop();
    }
       
    public byte[] assignJavaBuffer()
	{
    	return sharedBuffer;
	}	
    
    public int onReadyFrame (int bufferSize, int timeStamp)
    {    	
    	if(firstTimeStamp == 0){
    		firstTimeStamp = timeStamp;
    	}    	
    	timeStamp = timeStamp - firstTimeStamp;
    	int interval = timeStamp - lastTimeStamp;
    	lastTimeStamp = timeStamp;
    	
    	byte[] aux = new byte[bufferSize];
    	System.arraycopy(sharedBuffer, 0, aux, 0, bufferSize);
    	
       	Video video = new Video(timeStamp, aux, bufferSize);
    	
//		log.debug("SIZE = {}, LENGTH = {}", bufferSize, sharedBuffer.length);
	   	
	 // video.getHeader().setChannelId(5); ja esta correto
		 video.getHeader().setDeltaTime(interval);
		// video.getHeader().setHeaderType(/*var*/); //da sempre large, mas o do bbb depende do tamanho do array aparentemente
//		video.getHeader().setMessageType(/*var*/); ja esta correto, VIDEO
//		video.getHeader().setSize(size); ja esta correto
		video.getHeader().setStreamId(1); // nao seta automaticamente. nao sei como determinar o valor correto.
//		video.getHeader().setTime(time); // ja esta correto
	   		
//		log.debug("video.toString()={}",video.toString()); // ok
//		log.debug("video.getBody().toString()={}", video.getBody().toString()); // imprimindo sempre o mesmo valor, mas o bbb imprime sempre valores diferentes, mas nao sei o que eh isso
//		log.debug("video.getBody().length={}", video.getBody().length); //deveria imprimir o tamanho do frame, mas imprime o tamanho do array
//		log.debug("video.getCodec()={}", video.getCodec()); // esta certo
//		log.debug("video.getFrameType()={}", video.getFrameType()); //depende do primeiro byte (0x12, 0x22, 0x32)
//		log.debug("video.getHeader().getChannelId()={}", video.getHeader().getChannelId()); //esta correto (5) mas nao sei o q eh
//		log.debug("video.getHeader().getDeltaTime()={}", video.getHeader().getDeltaTime()); //ok
//		log.debug("video.getHeader().getHeaderType().name()={}", video.getHeader().getHeaderType().name()); //da sempre large, mas o do bbb depende do tamanho do array aparentemente
//		log.debug("video.getHeader().getMessageType().name()={}", video.getHeader().getMessageType().name()); //ok
//		log.debug("video.getHeader().getSize()={}", video.getHeader().getSize()); //ok
//		log.debug("video.getHeader().getStreamId()={}", video.getHeader().getStreamId()); //nao seta automaticamente. nao sei como determinar o valor correto, mas o do bbb apareceu 1
//		log.debug("video.getHeader().getTime()={}", video.getHeader().getTime()); //ok
//		log.debug("video.getHeader().getTinyHeader().toString()={}", video.getHeader().getTinyHeader().toString()); // nao sei o que eh mas parece estar ok
//		log.debug("video.getHeader().getTinyHeader().length={}", video.getHeader().getTinyHeader().length); //ok
//		log.debug("video.getFrameType={}", video.getWidth()); //ok
//		log.debug("video.getHeight()={}", video.getHeight()); //ok
//		log.debug("video.getMessageType().name()={}", video.getMessageType().name()); //ok
		
//		Channel channel;
//		MessageEvent mMessageEvent;
//		channel = mMessageEvent.getChannel();
		
//		RtmpPublisher publisher = new RtmpPublisher(reader, 1, 1, false, false);
//		publisher.start(channel, seekTimeRequested, messages)
//		publisher.start(channel, seekTime, playLength, messages)

//		RtmpEncoder mRtmpEncoder = new RtmpEncoder();
//		mRtmpEncoder.encode(video);		
		
		if(this.videoPublishHandler.videoConnection.channel != null && this.videoPublishHandler.videoConnection.publisher != null){
			RtmpPublisher publisher = this.videoPublishHandler.videoConnection.publisher;
			publisher.writeToStream(this.videoPublishHandler.videoConnection.channel, video);
		}
		
    	return 0;
    }
    
    public int endEncoding(){
    	videoPublishHandler.stop(Client.bbb);
    	
    	endEncoder();
    	return 0;
    }
    
    private void logBuffer(int initPos, int finalPos){
    	for(int i = initPos; i < finalPos; i++){
    		log.debug("sharedBuffer[{}] = {}", i, sharedBuffer[i]);
    	}
    }
    
    private native int initEncoder(int width, int height, int frameRate);
    private native int endEncoder();
    private native int initSenderLoop();

	@Override
	public void close() {
		
	}

	@Override
	public Metadata getMetadata() {
		return null;
	}

	@Override
	public RtmpMessage[] getStartMessages() {
		return new RtmpMessage[] {
	        new Video(),
	    };
	}

	@Override
	public long getTimePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RtmpMessage next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long seek(long timePosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAggregateDuration(int targetDuration) {
		// TODO Auto-generated method stub
		
	}
}