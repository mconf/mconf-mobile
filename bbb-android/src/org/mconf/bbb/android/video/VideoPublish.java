package org.mconf.bbb.android.video;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VideoPublish extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(VideoPublish.class);
   
    private byte[] sharedBuffer;
          
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
    
//	com.flazr.rtmp.message.Video video = null;
    public int onReadyFrame (int bufferSize)
    {    	
    	log.debug("bufferSize = {}", bufferSize);
//    	logBuffer(0,100);
    	
	   	//video = new Video(sharedBuffer.clone(), bufferSize, time);
    	
//		log.debug("SIZE = {}, LENGTH = {}", bufferSize, sharedBuffer.length);
//		log.debug("CODEC = {}", video.getCodec());
//		log.debug("FRAMETYPE = {}", video.getFrameType());
//		log.debug("HEADER = {}", video.getHeader());
//		log.debug("HEIGHT = {}", video.getHeight());
//		log.debug("MESSAGETYPE = {}", video.getMessageType());
//		log.debug("WIDTH = {}", video.getWidth());
//		log.debug("DATA = {}", video.toString());
    
//    	video.encode();
      	
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