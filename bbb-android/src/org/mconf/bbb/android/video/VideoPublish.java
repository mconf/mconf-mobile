package org.mconf.bbb.android.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpEncoder;
import com.flazr.rtmp.RtmpPublisher;
import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.Video;

public class VideoPublish extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(VideoPublish.class);
   
    private byte[] sharedBuffer;
    
    private int firstTimeStamp = 0;
	private int lastTimeStamp = 0;
          
    public VideoPublish(int bufSize, int widthCaptureResolution, int heightCaptureResolution, int frameRate) {
    	sharedBuffer = new byte[bufSize]; //the encoded frame will never be bigger than the not encoded
    	initEncoder(widthCaptureResolution, heightCaptureResolution, frameRate); 
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
    	
       	Video video = new Video(timeStamp, sharedBuffer, bufferSize);
    	
//		log.debug("SIZE = {}, LENGTH = {}", bufferSize, sharedBuffer.length);
	   	
	 // video.getHeader().setChannelId(5); ja esta correto
		 video.getHeader().setDeltaTime(interval);
		// video.getHeader().setHeaderType(/*var*/); //da sempre large, mas o do bbb depende do tamanho do array aparentemente
//		video.getHeader().setMessageType(/*var*/); ja esta correto, VIDEO
//		video.getHeader().setSize(size); ja esta correto
		video.getHeader().setStreamId(1); // nao seta automaticamente. nao sei como determinar o valor correto.
//		video.getHeader().setTime(time); // ja esta correto
	   		
		log.debug("video.toString()={}",video.toString()); // ok
		log.debug("video.getBody().toString()={}", video.getBody().toString()); // imprimindo sempre o mesmo valor, mas o bbb imprime sempre valores diferentes, mas nao sei o que eh isso
		log.debug("video.getBody().length={}", video.getBody().length); //deveria imprimir o tamanho do frame, mas imprime o tamanho do array
		log.debug("video.getCodec()={}", video.getCodec()); // esta certo
		log.debug("video.getFrameType()={}", video.getFrameType()); //depende do primeiro byte (0x12, 0x22, 0x32)
		log.debug("video.getHeader().getChannelId()={}", video.getHeader().getChannelId()); //esta correto (5) mas nao sei o q eh
		log.debug("video.getHeader().getDeltaTime()={}", video.getHeader().getDeltaTime()); //ok
		log.debug("video.getHeader().getHeaderType().name()={}", video.getHeader().getHeaderType().name()); //da sempre large, mas o do bbb depende do tamanho do array aparentemente
		log.debug("video.getHeader().getMessageType().name()={}", video.getHeader().getMessageType().name()); //ok
		log.debug("video.getHeader().getSize()={}", video.getHeader().getSize()); //ok
		log.debug("video.getHeader().getStreamId()={}", video.getHeader().getStreamId()); //nao seta automaticamente. nao sei como determinar o valor correto, mas o do bbb apareceu 1
		log.debug("video.getHeader().getTime()={}", video.getHeader().getTime()); //ok
		log.debug("video.getHeader().getTinyHeader().toString()={}", video.getHeader().getTinyHeader().toString()); // nao sei o que eh mas parece estar ok
		log.debug("video.getHeader().getTinyHeader().length={}", video.getHeader().getTinyHeader().length); //ok
		log.debug("video.getFrameType={}", video.getWidth()); //ok
		log.debug("video.getHeight()={}", video.getHeight()); //ok
		log.debug("video.getMessageType().name()={}", video.getMessageType().name()); //ok
		
//		publisher = new RtmpPublisher(reader, 1, 1, false, false);
//		publisher.start(channel, seekTimeRequested, messages)
//		publisher.start(channel, seekTime, playLength, messages)

		RtmpEncoder mRtmpEncoder = new RtmpEncoder();
		mRtmpEncoder.encode(video);		
		
    	return 0;
    }
    
    public int endEncoding(){
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
}