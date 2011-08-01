package org.mconf.bbb.android.video;

import java.io.IOException;
import java.lang.reflect.Method;

import org.mconf.bbb.android.BackgroundManager;
import org.mconf.bbb.android.BigBlueButton;
import org.mconf.bbb.android.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class VideoCapture extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {
	
	private static final Logger log = LoggerFactory.getLogger(VideoCapture.class);	
	
    private SurfaceHolder mHolder;
    private VideoPublish mVideoPublish;
	private Method mAcb;       // method for adding a pre-allocated buffer 
    private Object[] mArglist; // list of arguments
    private Context context;
    private boolean isSurfaceCreated = false; // true when: surface is created AND mVideoPublish is correctly set
    										  // false when: surface is destroyed
    private boolean usingFaster, usingHidden;
	
	public VideoCapture(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.context = context;
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
	
    private int getPublisher(){
    	mVideoPublish = ((BigBlueButton) getContext().getApplicationContext()).getVideoPublish();
		if(mVideoPublish == null){
    		log.debug("Error: could not get or instantiate a VideoPublisher");
    		return CaptureConstants.E_COULD_NOT_GET_PUBLISHER;
    	}
		return CaptureConstants.E_OK;
    }
 
	public boolean isCapturing() { // returns true if the capture is running or is paused
								   // returns false if the capture is stopped or is in an error state
		if(mVideoPublish != null && 
		  (mVideoPublish.state == CaptureConstants.RESUMED ||
		   mVideoPublish.state == CaptureConstants.PAUSED)){
			return true;
		}		
		return false;
	}	
	
    public int setFrameRate(int fr){
    	if(mVideoPublish != null){
    		mVideoPublish.frameRate = fr;
    		return CaptureConstants.E_OK;
    	} else {
    		log.debug("Error: could not set frame rate");
    	    return CaptureConstants.E_COULD_NOT_SET_FR;
    	}    	
    }
    
    public int setWidth(int w){
    	if(mVideoPublish != null){
	    	mVideoPublish.width = w;
	    	return CaptureConstants.E_OK;
		} else {
			log.debug("Error: could not set width");
		    return CaptureConstants.E_COULD_NOT_SET_W;
		}    	
    }
    
    public int setHeight(int h){
    	if(mVideoPublish != null){
	    	mVideoPublish.height = h;
	    	return CaptureConstants.E_OK;
		} else {
			log.debug("Error: could not set height");
		    return CaptureConstants.E_COULD_NOT_SET_H;
		}    	
    }
    
    public int setBitRate(int br){
    	if(mVideoPublish != null){
	    	mVideoPublish.bitRate = br;
	    	return CaptureConstants.E_OK;
		} else {
			log.debug("Error: could not set bitrate");
		    return CaptureConstants.E_COULD_NOT_SET_BR;
		}    	
    }
    
    public int setGOP(int g){
    	if(mVideoPublish != null){
    		mVideoPublish.GOP = g;
    		return CaptureConstants.E_OK;
		} else {
			log.debug("Error: could not set gop");
		    return CaptureConstants.E_COULD_NOT_SET_GOP;
		}    	
    }
    
    public int getFrameRate(int fr){
    	if(mVideoPublish != null){
    		return mVideoPublish.frameRate;
       	} else {
    		log.debug("Error: could not get frame rate");
    	    return CaptureConstants.E_COULD_NOT_GET_FR;
    	}    	
    }
    
    public int getWidth(int w){
    	if(mVideoPublish != null){
	    	return mVideoPublish.width;
		} else {
			log.debug("Error: could not get width");
		    return CaptureConstants.E_COULD_NOT_GET_W;
		}    	
    }
    
    public int getHeight(int h){
    	if(mVideoPublish != null){
	    	return mVideoPublish.height;
		} else {
			log.debug("Error: could not get height");
		    return CaptureConstants.E_COULD_NOT_GET_H;
		}    	
    }
    
    public int getBitRate(int br){
    	if(mVideoPublish != null){
	    	return mVideoPublish.bitRate;
	    } else {
			log.debug("Error: could not get bitrate");
		    return CaptureConstants.E_COULD_NOT_GET_BR;
		}    	
    }
    
    public int getGOP(int g){
    	if(mVideoPublish != null){
    		return mVideoPublish.GOP;
		} else {
			log.debug("Error: could not get gop");
		    return CaptureConstants.E_COULD_NOT_GET_GOP;
		}    	
    }
    
	// Centers the preview on the screen keeping the capture aspect ratio.
    // Remember to call this function after you change the width or height if
    // you want to keep the aspect and the video centered
    // This function is useful for displaying the preview centered on fullscreen on an Activity
    // or centered on a Dialog, for example. If that is not the case, then it may be better to use
    // the VideoCaptureLayout class to handle the video preview position instead
    public int centerPreview() {
    	if(mVideoPublish != null){
	    	VideoCentering mVideoCentering = new VideoCentering();
	    	mVideoCentering.setAspectRatio(mVideoPublish.width/(float)mVideoPublish.height);
	    	LayoutParams layoutParams = mVideoCentering.getVideoLayoutParams(mVideoCentering.getDisplayMetrics(this.getContext(),40), this.getLayoutParams());
			setLayoutParams(layoutParams);
			return CaptureConstants.E_OK;
    	} else {
    		log.debug("Error: could not center screen");
    		return CaptureConstants.E_COULD_NOT_CENTER;
    	}
	}
    
    // This function is useful for hidden the preview that was being shown on fullscreen on an Activity
    // or centered on a Dialog, for example. If that is not the case, then it may be better to use
    // the VideoCaptureLayout class to handle the video preview hidding instead
    public void hidePreview() {
		VideoCentering mVideoCentering = new VideoCentering();
		LayoutParams layoutParams = mVideoCentering.hidePreview(this.getLayoutParams());   	
		setLayoutParams(layoutParams);
	}	
    
    private boolean isAvailableSprintFFC()
    {
        try {
            Class.forName("android.hardware.HtcFrontFacingCamera");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    private int openCameraNormalWay(){
    	 if(mVideoPublish.mCamera != null){
         	mVideoPublish.mCamera.release();
         	mVideoPublish.mCamera = null;
         }         
         
         mVideoPublish.mCamera = Camera.open();
         if(mVideoPublish.mCamera == null){
        	 log.debug("Error: could not open camera");
        	 return CaptureConstants.E_COULD_NOT_OPEN_CAMERA;
         }
         Camera.Parameters parameters = mVideoPublish.mCamera.getParameters();
         parameters.set("camera-id", 2);
         mVideoPublish.mCamera.setParameters(parameters);
         return CaptureConstants.E_OK;
    }
    
    private int openCamera(){
    	int err = CaptureConstants.E_OK;
    	
    	if (isAvailableSprintFFC()) { // this device has the specific HTC camera
        	try { // try opening the specific HTC camera
                Method method = Class.forName("android.hardware.HtcFrontFacingCamera").getDeclaredMethod("getCamera", (Class[])null);
                mVideoPublish.mCamera = (Camera) method.invoke((Object)null, (Object)null);
            } catch (Exception ex) { // it was not possible to open the specifica HTC camera,
            						 // so, lets open the camera using the normal way
                log.debug(ex.toString());
                
                err = openCameraNormalWay();
            }
        } else { // this device does not have the specific HTC camera,
        		 // so, lets open the camera using the normal way        	
            err = openCameraNormalWay();
        }         
    	
    	return err;
    }
    
    private int setDisplay(){
    	if(mVideoPublish.mCamera != null){
	        try {
	            mVideoPublish.mCamera.setPreviewDisplay(mHolder);
	        } catch (IOException exception) {
	        	log.debug("Error: could not set preview display"); 
	         	log.debug(exception.toString());
	     	
	         	mVideoPublish.mCamera.release();
	         	mVideoPublish.mCamera = null;
	      
	         	return CaptureConstants.E_COULD_NOT_SET_PREVIEW_DISPLAY_R1;
	    	}
    	} else {
    		log.debug("Error: setDisplay() called without an opened camera");
    		return CaptureConstants.E_COULD_NOT_SET_PREVIEW_DISPLAY_R2;
    	}
         
        return CaptureConstants.E_OK; 
    }
    
    private int setParameters(){
    	if(mVideoPublish.mCamera != null){
	    	Camera.Parameters parameters = mVideoPublish.mCamera.getParameters();
	    	log.debug("Setting the capture frame rate to {}", mVideoPublish.frameRate);
	    	parameters.setPreviewFrameRate(mVideoPublish.frameRate);
	    	log.debug("Setting the capture size to {}x{}", mVideoPublish.width, mVideoPublish.height);
	        parameters.setPreviewSize(mVideoPublish.width, mVideoPublish.height); 
	       	mVideoPublish.mCamera.setParameters(parameters);
	       	return CaptureConstants.E_OK;
    	} else {
    		log.debug("Error: setParameters() called without an opened camera");
    		return CaptureConstants.E_COULD_NOT_SET_PARAMETERS;
    	}
    }
    
    private int getBufferSize(){
    	if(mVideoPublish.mCamera != null){
	    	PixelFormat pixelFormat = new PixelFormat();
	 		PixelFormat.getPixelFormatInfo(mVideoPublish.mCamera.getParameters().getPreviewFormat(),pixelFormat);
	 		return mVideoPublish.width*mVideoPublish.height*pixelFormat.bitsPerPixel/8;
    	} else {
    		log.debug("Error: getBufferSize() called without an opened camera");
    		return CaptureConstants.E_COULD_NOT_GET_BUFSIZE;
    	}
    }
    
    private void setCallbackBest(){
		usingFaster = true;
		usingHidden = false;
		
		 //we call addCallbackBuffer twice to reduce the "Out of buffers, clearing callback!" problem
		byte[] buffer = new byte[mVideoPublish.bufSize];
		mVideoPublish.mCamera.addCallbackBuffer(buffer);		
		buffer = new byte[mVideoPublish.bufSize];
		mVideoPublish.mCamera.addCallbackBuffer(buffer);
		
		mVideoPublish.mCamera.setPreviewCallbackWithBuffer(this);
		
		log.debug("Using fast preview callback");
    }
    
    private int setCallbackHidden(){
    	int err;
        
    	usingFaster = true;
        usingHidden = true;
		
        //Must call this before calling addCallbackBuffer to get all the
        // reflection variables setup
        err = initForACB();
        if(err != CaptureConstants.E_OK){
        	return err; 
        }

        //we call addCallbackBuffer twice to reduce the "Out of buffers, clearing callback!" problem
        byte[] buffer = new byte[mVideoPublish.bufSize];
        err = addCallbackBuffer_Android2p2(buffer);
        if(err != CaptureConstants.E_OK){
        	return err; 
        }        
        buffer = new byte[mVideoPublish.bufSize];
        err = addCallbackBuffer_Android2p2(buffer);
        if(err != CaptureConstants.E_OK){
        	return err; 
        }
        
        err = setPreviewCallbackWithBuffer_Android2p2(false);
        if(err != CaptureConstants.E_OK){
        	return err; 
        }
        
        log.debug("Using fast but hidden preview callback");
        return CaptureConstants.E_OK;
    }
	
	private void setCallbackSlow(){
		usingFaster = false;
    	usingHidden = false;
    	
    	mVideoPublish.mCamera.setPreviewCallback(this);
    	
    	log.debug("Using slow preview callback");
	}
    
    private int prepareCallback(){
    	if(mVideoPublish.mCamera == null){
    		log.debug("Error: prepareCallback() called without an opened camera");
    		return CaptureConstants.E_COULD_NOT_PREPARE_CALLBACK_R1;
    	}
    	if(mVideoPublish.bufSize < CaptureConstants.E_OK || mVideoPublish.bufSize <= 0){
    		log.debug("Error: prepareCallback() called without a valid mVideoPublish.bufSize");
    		return CaptureConstants.E_COULD_NOT_PREPARE_CALLBACK_R2;
    	}
		//java reflection (idea from http://code.google.com/p/android/issues/detail?id=2794):
        //This kind of java reflection is safe to be used as explained in the official android documentation
        //on (http://developer.android.com/resources/articles/backward-compatibility.html).
        //Explanation: The method setPreviewCallback exists since Android's API level 1.
        //An alternative method is the setPreviewCallbackWithBuffer, which exists since API level 8 (Android 2.2).
        //The setPreviewCallbackWithBuffer method is much better than the setPreviewCallback method
        //in terms of performance, because the onPreviewFrame method returns a copy of the frame 
        //in a newly allocated memory when using the setPreviewCallback method, causing the
        //Garbage Collector to perform, which takes about 80-100ms.
        //Instead, when the setPreviewCallbackWithBuffer is used, the byte array is overwritten,
        //avoiding the GC to perform.
        //In mconf we want compatibility with API levels lower than 8.
        //The setPreviewCallbackWithBuffer method is implemented on a Debug class on API levels lower than 8.
        //In order to use it on API levels lower than 8, we need to use Java Reflection.      
		if (Integer.parseInt(Build.VERSION.SDK) >= 8){ //if(2.2 or higher){
			setCallbackBest();
	    } else if(HiddenCallbackWithBuffer()) { //} else if(has the methods hidden){
	    	if(setCallbackHidden() != CaptureConstants.E_OK){
	    		setCallbackSlow();
	    	}
	    } else {
	    	setCallbackSlow();     
	    }
		
		return CaptureConstants.E_OK;
    }
        
    private int beginPreview(){
    	if(mVideoPublish.mCamera != null){
    		mVideoPublish.mCamera.startPreview();
    		return CaptureConstants.E_OK;
    	} else {
    		log.debug("Error: beginPreview() called without an opened camera");
    		return CaptureConstants.E_COULD_NOT_BEGIN_PREVIEW;
    	}
    }
    
    private int initNativeSide(){    
    	if(mVideoPublish.bufSize < CaptureConstants.E_OK || mVideoPublish.bufSize <= 0){
    		log.debug("Error: initNativeSide() called without a valid mVideoPublish.bufSize");
    		return CaptureConstants.E_COULD_NOT_INIT_NATIVE_SIDE;
    	}
    	mVideoPublish.initNativeEncoder();
    	return CaptureConstants.E_OK;
    }
    
    private int startPublisherThread(){
    	if(!mVideoPublish.nativeEncoderInitialized){
    		log.debug("Error: startPublisherThread() called but native capture side not initialized");
    		return CaptureConstants.E_COULD_NOT_START_PUBLISHER_THREAD_R1;
    	}
    	if(mVideoPublish.isAlive()){
    		log.debug("Error: startPublisherThread() called but publisher thread already running");
    		return CaptureConstants.E_COULD_NOT_START_PUBLISHER_THREAD_R2;
    	}
	    mVideoPublish.start();
	    return CaptureConstants.E_OK;
    }
    
    private int startPublisher(){
    	if(!mVideoPublish.nativeEncoderInitialized){
    		log.debug("Error: startPublisher() called but native capture side not initialized");
    		return CaptureConstants.E_COULD_NOT_START_PUBLISHER_R1;
    	}
    	if(!mVideoPublish.isAlive()){
    		log.debug("Error: startPublisher() called but publisher thread not running");
    		return CaptureConstants.E_COULD_NOT_START_PUBLISHER_R2;
    	}
    	mVideoPublish.startPublisher();
    	return CaptureConstants.E_OK;
    }
    
    private void clearCallbackBest(){
		mVideoPublish.mCamera.setPreviewCallbackWithBuffer(null);
    }
    
    private int clearCallbackHidden(){
    	int err;
		
        err = setPreviewCallbackWithBuffer_Android2p2(true);
        if(err != CaptureConstants.E_OK){
        	return err; 
        }

        return CaptureConstants.E_OK;
    }
	
	private void clearCallbackSlow(){
    	mVideoPublish.mCamera.setPreviewCallback(null);
	}
    
    private void resetBuffersAndCallbacks(){
    	if(usingHidden){ 
			clearCallbackHidden();
		} else if(usingFaster){
			clearCallbackBest();
		} else {
			clearCallbackSlow();
		}
    }
    
    public int startCapture(){   	
    	int err = CaptureConstants.E_OK;
    	if(mVideoPublish == null){
    		err = getPublisher();
    		if(err != CaptureConstants.E_OK){
    			mVideoPublish.state = CaptureConstants.ERROR;
    			return err;
    		}
    	}
    	
    	mVideoPublish.state = CaptureConstants.RESUMED;
    	
    	mVideoPublish.restartWhenResume = false;
    	
    	// acquires the camera
		err = openCamera();
    	if(err != CaptureConstants.E_OK){
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return err; 
    	};
    	    	
    	// sets up the camera parameters
    	err = setParameters();
    	if(err != CaptureConstants.E_OK){
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return err;
    	}
    	
    	// gets the size of a non encoded frame
    	mVideoPublish.bufSize = getBufferSize();
    	if(mVideoPublish.bufSize < CaptureConstants.E_OK){
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return mVideoPublish.bufSize;
    	}
    	
		err = resumeCapture();
		if(err != CaptureConstants.E_OK){
			mVideoPublish.state = CaptureConstants.ERROR;
			return err;
		}
		
		// creates the shared buffer, inits the native side and sets the streamId 
		err = initNativeSide();
		if(err != CaptureConstants.E_OK){
			mVideoPublish.state = CaptureConstants.ERROR;
			return err;
		}
    	
    	// start the publisher native thread and sets isCapturing to true
    	err = startPublisherThread();
    	if(err != CaptureConstants.E_OK){
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return err;
    	}
    	
    	// start the publisher handler
    	err = startPublisher();
    	if(err != CaptureConstants.E_OK){
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return err;
    	}
    	
    	activateNotification();
    	    	
    	return err;
    }
    
    public int resumeCapture(){    	
    	int err = CaptureConstants.E_OK;
    	if(!isSurfaceCreated || mVideoPublish == null){
    		err = CaptureConstants.E_COULD_NOT_RESUME_CAPTURE;
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return err;
       	}
    	
    	mVideoPublish.state = CaptureConstants.RESUMED;
    	   	
    	mVideoPublish.lastSurfaceDestroyed = false; // set to false because the 2 surfaces conflict has ended
    	mVideoPublish.nextSurfaceCreated = false; // set to false because the 2 surfaces conflict has ended
    	
    	// tells it where to draw (sets display for preview)
    	err = setDisplay();
    	if(err != CaptureConstants.E_OK){
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return err;
    	}
    	
    	// prepares the callback
    	err = prepareCallback(); 
    	if(err != CaptureConstants.E_OK){
    		mVideoPublish.state = CaptureConstants.ERROR;
    		return err;
    	}
    	
    	// begins the preview.
	    err = beginPreview();
	    if(err != CaptureConstants.E_OK){
	    	mVideoPublish.state = CaptureConstants.ERROR;
	    	return err;
	    }
	     	
    	return err;
    }
    
    public void stopCapture(){ 
    	if(mVideoPublish != null){
	    	pauseCapture();
	    	mVideoPublish.state = CaptureConstants.STOPPED;
	    	
	    	// Because the CameraDevice object is not a shared resource, it's very
	        // important to release it when it may not be used anymore
	    	if(mVideoPublish.mCamera != null){
	    		mVideoPublish.mCamera.release();
	        	mVideoPublish.mCamera = null;
	    	}
	    	
	    	mVideoPublish.endNativeEncoder();
	    	mVideoPublish.stopPublisher();
	    		    	
	    	mVideoPublish = ((BigBlueButton) getContext().getApplicationContext()).deleteVideoPublish(); 
	    	
	    	cancelNotification();
    	}
    }
    
    private void pauseCapture(){
    	if(mVideoPublish != null && mVideoPublish.mCamera != null && 
    			!(mVideoPublish.state == CaptureConstants.PAUSED)){
    		mVideoPublish.state = CaptureConstants.PAUSED;
    		    		
    		mVideoPublish.mCamera.stopPreview();
    		
    		resetBuffersAndCallbacks();
    		
    		try {
				mVideoPublish.mCamera.setPreviewDisplay(null);
			} catch (IOException e) {
				log.debug("Warning: error when trying to remove the preview display");
				e.printStackTrace();
			}
    	}
    }
    
    private void activateNotification(){
    	String contentTitle = getResources().getString(R.string.publishing_video);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon_bbb, contentTitle, System.currentTimeMillis());

		Intent notificationIntent = null;
		notificationIntent = new Intent(context, VideoCapture.class);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, null, contentIntent);
		notificationManager.notify(CaptureConstants.VIDEO_PUBLISH_NOTIFICATION_ID, notification);	

    }
    
    private void cancelNotification(){
    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(CaptureConstants.VIDEO_PUBLISH_NOTIFICATION_ID);
    }
    
    // Checks if addCallbackBuffer and setPreviewCallbackWithBuffer are written but hidden.
    // This method will look for all methods of the android.hardware.Camera class,
    // even the hidden ones.
    private boolean HiddenCallbackWithBuffer(){
    	int exist = 0;    	
    	try {
			Class<?> c = Class.forName("android.hardware.Camera");
			Method[] m = c.getMethods();
			for(int i=0; i<m.length; i++){
				//log.debug("  method:"+m[i].toString());
				String method = m[i].toString();
				if(method.indexOf("setPreviewCallbackWithBuffer") != -1 || method.indexOf("addCallbackBuffer") != -1){
					exist++; 
				}				
			}
		} catch (Exception e) {
			log.debug(e.toString());
			return false;
		}
		if(exist == 2){
			return true;
		} else {
			return false;
		}
    }

    private int initForACB(){
    	try {
			Class<?> mC = Class.forName("android.hardware.Camera");
		
			Class<?>[] mPartypes = new Class[1];
			// variable that will hold parameters for a function call
			mPartypes[0] = (new byte[1]).getClass(); //There is probably a better way to do this.
			mAcb = mC.getMethod("addCallbackBuffer", mPartypes);

			mArglist = new Object[1];
		} catch (Exception e) {
			log.debug("Problem setting up for addCallbackBuffer: " + e.toString());
			return CaptureConstants.E_COULD_NOT_INIT_HIDDEN;
		}
		return CaptureConstants.E_OK;
    }
    
    // This method uses reflection to call the addCallbackBuffer method
    // It allows you to add a byte buffer to the queue of buffers to be used by preview.
    // Real addCallbackBuffer implementation: http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/android/hardware/Camera.java;hb=9db3d07b9620b4269ab33f78604a36327e536ce1
    // @param b The buffer to register. Size should be width * height * bitsPerPixel / 8.
    private int addCallbackBuffer_Android2p2(byte[] b){ //  this function is native since Android 2.2
    	//Check to be sure initForACB has been called to setup
    	// mVideoPublish.mAcb and mVideoPublish.mArglist
//    	if(mVideoPublish.mArglist == null){
//    		initForACB();
//    	}

    	mArglist[0] = b;
    	try {
    		mAcb.invoke(mVideoPublish.mCamera, mArglist);
    	} catch (Exception e) {
    		log.debug("invoking addCallbackBuffer failed: " + e.toString());
    		return CaptureConstants.E_COULD_NOT_ADD_HIDDEN;
    	}
    	return CaptureConstants.E_OK;
    }
    
    // This method uses reflection to call the setPreviewCallbackWithBuffer method
    // Use this method instead of setPreviewCallback if you want to use manually allocated
    // buffers. Assumes that "this" implements Camera.PreviewCallback
    private int setPreviewCallbackWithBuffer_Android2p2(boolean clear){ // this function is native since Android 2.2
    	try {
			Class<?> c = Class.forName("android.hardware.Camera");
			Method spcwb = null;  // sets a preview with buffers
			//This way of finding our method is a bit inefficient
			// However, since this method is only called once, this should not cause performance issues					
//			Method[] m = c.getMethods(); // get all methods of camera
//			for(int i=0; i<m.length; i++){
//				if(m[i].getName().compareTo("setPreviewCallbackWithBuffer") == 0){
//					spcwb = m[i];
//					break;
//				}
//			}
			
//			This is a faster way to find the hidden method 			
			Class<?>[] mPartypes = new Class[1];
//			variable that will hold parameters for a function call
			mPartypes[0] = (new byte[1]).getClass(); //There is probably a better way to do this.
			spcwb = c.getMethod("setPreviewCallbackWithBuffer", mPartypes);
			
			//If we were able to find the setPreviewCallbackWithBuffer method of Camera, 
			// we can now invoke it on our Camera instance, setting 'this' to be the
			// callback handler
			if(spcwb != null){
				Object[] arglist = new Object[1];
				if(clear){
					arglist[0] = null;
				} else {
					arglist[0] = this; // receives a copy of a preview frame
				}
				spcwb.invoke(mVideoPublish.mCamera, arglist);
				//Log.i("AR","setPreviewCallbackWithBuffer: Called method");
			} else {
				log.debug("setPreviewCallbackWithBuffer: Did not find method");
				return CaptureConstants.E_COULD_NOT_SET_HIDDEN_R1;
			}			
		} catch (Exception e) {
			log.debug("{}",e.toString());
			return CaptureConstants.E_COULD_NOT_SET_HIDDEN_R2;
    	}
    	return CaptureConstants.E_OK;
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	log.debug("preview surface destroyed");
    	
    	isSurfaceCreated = false;
    	
    	if(mVideoPublish != null && 
    			(mVideoPublish.state == CaptureConstants.RESUMED 
    		  || mVideoPublish.state == CaptureConstants.PAUSED)){ // means that the activity or the orientation
    		   // changed and the camera was being captured and published 
    		   // (because, in the strategy we are using, this surface will only be destroyed
			   // when the activity or the orientation changes) 	
	    	if(BackgroundManager.isApplicationBroughtToBackground(context)){ // means that the next
	    			// activity doesn't belong to this application. So, we need to:
	    			// 1) stop the capture, because we won't have a surface
	    			// 2) consequently, we have to stop the publish
	    			// 3) release the camera, because the user may want to use
	    			//    the camera on another application
	    		mVideoPublish.restartWhenResume = true;
	    		
	    		// stops the preview, the publish and releases the camera
	    		stopCapture();	    		
	    	} else { // means that the next activity belongs to this application	    		
	       		// pauses the preview and publish
	    		pauseCapture();
	    		
	    		// signalizes that the activity has changed and the
		        // camera was being captured
	    		if(mVideoPublish.nextSurfaceCreated){ // means that the surface of the next activity or
	    			    // of the next orientation has
	    				// already been created
	    			mVideoPublish.RequestResume();
	    		} else { // means that the surface of the next activity has not been created yet
	    			mVideoPublish.lastSurfaceDestroyed = true; // set to true because the current surface has been destroyed
	    		}
	    	}
    	}
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	log.debug("preview surface changed");
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log.debug("preview surface created");        
        
        if(getPublisher() == CaptureConstants.E_OK){
        	isSurfaceCreated = true;
        	
        	if(mVideoPublish.state == CaptureConstants.RESUMED ||
        	   mVideoPublish.state == CaptureConstants.PAUSED){
        		if(!mVideoPublish.lastSurfaceDestroyed){ // means that the last preview surface used to
        										// capture the video is still active (not destroyed)
        										// and the capture is not paused yet.
        										// So, we can't resume the capture right now
        			mVideoPublish.nextSurfaceCreated = true;
        			
        			mVideoPublish.readyToResume(this);
        		} else { // means that the last preview surface used to capture the video has already been
        				 // destroyed and the capture is paused
        			resumeCapture();        			
        		}
        	} else if(mVideoPublish.state == CaptureConstants.STOPPED){
        		if(mVideoPublish.restartWhenResume){ // means that the following happened:
        											 // a publish was running, then the application went to
        											 // background, then it is now back to foreground.
        											 // So, if we want to keep the previous state,
        											 // lets start the capture
        			startCapture();        			
        		}
        	}
    	}
    }
    
    @Override
    public void onPreviewFrame (byte[] _data, Camera camera)
    {
    	if(mVideoPublish != null && mVideoPublish.state == CaptureConstants.RESUMED){
    		if(usingHidden){ 
    			addCallbackBuffer_Android2p2(_data);
    		} else if(usingFaster && mVideoPublish.mCamera != null){
    			mVideoPublish.mCamera.addCallbackBuffer(_data);
    		}
    		enqueueFrame(_data,_data.length);
    	}
    }
    
    static {
    	String path = "/data/data/org.mconf.bbb.android/lib/";
    	try {
	    	System.load(path + "libavutil.so");
	    	System.load(path + "libswscale.so");
	        System.load(path + "libavcodec.so");
	        System.load(path + "libavformat.so");
	        System.load(path + "libthread.so");
	    	System.load(path + "libcommon.so");
	    	System.load(path + "libqueue.so");
	    	System.load(path + "libencode.so");
	    	System.load(path + "libmconfnativeencodevideo.so");  
        
	    	log.debug("Native libraries loaded");    
    	} catch (SecurityException e) {
    		log.debug("Native libraries failed");  
    	}
    }
    
	private native int enqueueFrame(byte[] data, int length);
}