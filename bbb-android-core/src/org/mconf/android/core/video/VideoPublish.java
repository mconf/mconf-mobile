package org.mconf.android.core.video;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.api.ApplicationService;
import org.mconf.bbb.video.BbbVideoPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.hardware.Camera;

import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.message.Metadata;
import com.flazr.rtmp.message.Video;

public class VideoPublish extends Thread implements RtmpReader {

	private static final Logger log = LoggerFactory.getLogger(VideoPublish.class);
	
	public int frameRate = CaptureConstants.DEFAULT_FRAME_RATE;
    public int width = CaptureConstants.DEFAULT_WIDTH;
    public int height = CaptureConstants.DEFAULT_HEIGHT;
    public int bitRate = CaptureConstants.DEFAULT_BIT_RATE;
    public int GOP = CaptureConstants.DEFAULT_GOP;
    	
	private List<Video> framesList = new ArrayList<Video>();
	
	private BbbVideoPublisher videoPublishHandler;
	
	private BigBlueButtonClient context;
	
	private VideoCapture mVideoCapture;	
	
	private byte[] sharedBuffer;
	
	public int bufSize;
	
	public Camera mCamera;
	
	private int firstTimestamp = 0;
	private int lastTimestamp = 0;
	    
    public int state = CaptureConstants.STOPPED;
        
    public boolean nextSurfaceCreated = false; // used when:
    										// the activity or the orientation changes and 
    										// the video was being captured (except if 
	   // we are faking a destruction - see the VideoCapture.fakeDestroyed variable for more info). 
    										// In this moment,
    										// there are 2 surfaces conflicting, and we need to know
    										// if/when they are destroyed and created.
    										// true when: the next surface has already been created
    										// false when: the next surface has not been created yet OR 
    										//             there isn't a 2 surfaces conflict
    public boolean lastSurfaceDestroyed = false; // used when:
    										// same situation as the "nextSurfaceCreated" variable
    										// true when: the last preview surface has already been destroyed
    										// false when: the last preview surface is still active
    
    public boolean nativeEncoderInitialized = false; // used to prevent errors.
    												 // true when the native class VideoEncoder is not NULL
    												 // false when the native class VideoEncoder is NULL
    
    public boolean restartWhenResume; // used in the following situation:
    								  // the user put the application in background.
    								  // now the user put the application in foreground again.
    								  // in this situation, this boolean is true if the camera was being 
    								  // captured when the application went to background, and false if the
    								  // camera was not being captured.
    								  // So, this boolean allows to keep the previous state (capturing or not)
    								  // when the application resumes.
    
    private boolean framesListAvailable = false; // set to true when the RtmpPublisher starts seeking
    										     // for video messages. When true, this boolean allows the addition
    											 // of video frames to the list.
    											 // Set to false right when the RtmpPublisher decides to 
    											 // close the reader. When false, this boolean prevents the
    											 // addition of new frames to the list.
    
    private boolean firstFrameWrote = true;

	public int cameraId = -1;
	        
    public VideoPublish(BigBlueButtonClient context, boolean restartWhenResume, int frameRate, int width, int height, int bitRate, int GOP) {
    	this.context = context;    	 
    	 	
    	this.restartWhenResume = restartWhenResume;
    	
    	this.frameRate = frameRate;
    	this.width = width;
    	this.height = height;
    	this.bitRate = bitRate;
    	this.GOP = GOP;
    }
    
    public void startPublisher() {
    	String streamName = width + "x" + height + context.getMyUserId();
    	if (context.getJoinService().getApplicationService().getVersion().equals(ApplicationService.VERSION_0_7)) {
    		// do nothing
    	} else
    		streamName += "-" + new Date().getTime();
    	videoPublishHandler = new BbbVideoPublisher(context, this, streamName);
    	videoPublishHandler.start();
    }        	
    
    public void stopPublisher(){
    	synchronized(this) {
			this.notifyAll();
		}
    	if(videoPublishHandler != null){
    		videoPublishHandler.stop();
    	}
    }
    
    public void readyToResume(VideoCapture videoCapture) {
    	mVideoCapture = videoCapture;
	}
    
    public int RequestResume() {
    	if(mVideoCapture == null){
    		log.debug("Error: resume requested but there is not a VideoCapture class available");
    		return CaptureConstants.E_COULD_NOT_REQUEST_RESUME;
    	}
    	mVideoCapture.resumeCapture();
    	mVideoCapture = null;
    	return CaptureConstants.E_OK;
	}
    
    public void initNativeEncoder(){
    	sharedBuffer = new byte[bufSize]; // the encoded frame will never be bigger than the not encoded
    									  //\TODO Usually the encoded frame is much smaller than the not encoded.
    										    //So it would be good if we find out the biggest encoded
    											//frame size possible (given the encoding parameters)
    											//in order to initialize the sharedBuffer array as a byte[]
    											//of the smaller size as possible, to allocate less memory.
    	
    	initEncoder(width, height, frameRate, bitRate, GOP);
    	
    	nativeEncoderInitialized = true;
    }
    
    public void endNativeEncoder(){
    	nativeEncoderInitialized = false;
        	
    	endEncoder();
    }
    
    @Override
    public void run() {       	
    	initSenderLoop();
    }
    
    public byte[] assignJavaBuffer()
	{
    	return sharedBuffer;
	}
    
    public int onReadyFrame (int bufferSize, int timestamp)
    {    	
    	if(firstTimestamp == 0){
    		firstTimestamp = timestamp;
    	}    	
    	timestamp = timestamp - firstTimestamp;
    	int interval = timestamp - lastTimestamp;
    	lastTimestamp = timestamp;
    	
    	byte[] aux = new byte[bufferSize];
    	System.arraycopy(sharedBuffer, 0, aux, 0, bufferSize); //\TODO see if we can avoid this copy
    	
       	Video video = new Video(timestamp, aux, bufferSize);
   	    video.getHeader().setDeltaTime(interval);

   	    if (framesListAvailable) {
   	    	framesList.add(video);
			if (firstFrameWrote) {
				firstFrameWrote = false;
				videoPublishHandler.fireFirstFrame();
			}
			synchronized(this) {
				this.notifyAll();
			}
   	    }
    	return 0;
    }

	@Override
	public void close() {		
		framesListAvailable = false;
		if(framesList != null){
			framesList.clear();
		}
		framesList = null;
	}

	@Override
	public Metadata getMetadata() {
		return null;
	}

	@Override
	public Video[] getStartMessages() {
		framesListAvailable = true;
		Video[] startMessages = new Video[0];
        return startMessages;
	}

	@Override
	public long getTimePosition() {
		return 0;
	}

	@Override
	public boolean hasNext() {
		if((state == CaptureConstants.RESUMED || state == CaptureConstants.PAUSED)
				&& framesListAvailable && framesList != null && framesList.isEmpty()){
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if((state == CaptureConstants.RESUMED || state == CaptureConstants.PAUSED)
				&& framesListAvailable && framesList != null){ // means that the framesList is not empty
			return true;
		} else { // means that the framesList is empty or we should not get next frames
			return false;
		}
	}

	@Override
	public Video next() {
		if(framesListAvailable && framesList != null && !framesList.isEmpty()){
			return framesList.remove(0);
		} else {
			Video emptyVideo = new Video();
	        return emptyVideo;
		}
	}

	@Override
	public long seek(long timePosition) {
		return 0;
	}

	@Override
	public void setAggregateDuration(int targetDuration) {
	}
	
	private native int initEncoder(int width, int height, int frameRate, int bitRate, int GOP);
	private native int endEncoder();
    private native int initSenderLoop();

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