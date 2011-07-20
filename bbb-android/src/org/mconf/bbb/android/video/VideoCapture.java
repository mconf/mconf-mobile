package org.mconf.bbb.android.video;

import java.io.IOException;
import java.lang.reflect.Method;

import org.mconf.bbb.android.BackgroundManager;
import org.mconf.bbb.android.BigBlueButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class VideoCapture extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {
	
	private static final Logger log = LoggerFactory.getLogger(VideoCapture.class);
	
    SurfaceHolder mHolder;
    boolean isSurfaceCreated = false;
    public VideoPublish mVideoPublish;
    private boolean usingFaster, usingHidden;
	private Method mAcb;       // method for adding a pre-allocated buffer 
    private Object[] mArglist; // list of arguments
    private Context context;
	
	private static final int E_OK = 0;
	private static final int E_COULD_NOT_OPEN_CAMERA = -1;
	private static final int E_COULD_NOT_SET_PREVIEW_DISPLAY_R1 = -2;
	private static final int E_COULD_NOT_SET_PREVIEW_DISPLAY_R2 = -3;
	private static final int E_COULD_NOT_SET_PARAMETERS = -5;
	private static final int E_COULD_NOT_GET_BUFSIZE = -6;
	private static final int E_COULD_NOT_PREPARE_CALLBACK_R1 = -7;
	private static final int E_COULD_NOT_PREPARE_CALLBACK_R2 = -8;
	private static final int E_COULD_NOT_INIT_NATIVE_SIDE = -9;
	private static final int E_COULD_NOT_BEGIN_PREVIEW = -10;
	private static final int E_COULD_NOT_START_PUBLISHER_THREAD_R1 = -11;
	private static final int E_COULD_NOT_START_PUBLISHER_THREAD_R2 = -12;
	private static final int E_COULD_NOT_START_PUBLISHER_R1 = -13;
	private static final int E_COULD_NOT_START_PUBLISHER_R2 = -14;
	private static final int E_COULD_NOT_RESUME_CAPTURE = -15;
	private static final int E_COULD_NOT_INIT_HIDDEN = -16;
	private static final int E_COULD_NOT_SET_HIDDEN_R1 = -17;
	private static final int E_COULD_NOT_SET_HIDDEN_R2 = -18;
	private static final int E_COULD_NOT_ADD_HIDDEN = -19;
	private static final int E_COULD_NOT_SET_FR = -20;
	private static final int E_COULD_NOT_SET_W = -21;
	private static final int E_COULD_NOT_SET_H = -22;
	private static final int E_COULD_NOT_SET_BR = -23;
	private static final int E_COULD_NOT_SET_GOP = -24;
	private static final int E_COULD_NOT_CENTER = -25;
	private static final int E_COULD_NOT_GET_PUBLISHER = -26;
    
    public VideoCapture(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.context = context;
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
 
    public int setFrameRate(int fr){
    	if(mVideoPublish != null){
    		mVideoPublish.frameRate = fr;
    		return E_OK;
    	} else {
    		log.debug("Error: could not set frame rate");
    	    return E_COULD_NOT_SET_FR;
    	}    	
    }
    
    public int setWidth(int w){
    	if(mVideoPublish != null){
	    	mVideoPublish.width = w;
	    	return E_OK;
		} else {
			log.debug("Error: could not set width");
		    return E_COULD_NOT_SET_W;
		}    	
    }
    
    public int setHeight(int h){
    	if(mVideoPublish != null){
	    	mVideoPublish.height = h;
	    	return E_OK;
		} else {
			log.debug("Error: could not set height");
		    return E_COULD_NOT_SET_H;
		}    	
    }
    
    public int setBitRate(int br){
    	if(mVideoPublish != null){
	    	mVideoPublish.bitRate = br;
	    	return E_OK;
		} else {
			log.debug("Error: could not set bitrate");
		    return E_COULD_NOT_SET_BR;
		}    	
    }
    
    public int setGOP(int g){
    	if(mVideoPublish != null){
    		mVideoPublish.GOP = g;
    	return E_OK;
		} else {
			log.debug("Error: could not set gop");
		    return E_COULD_NOT_SET_GOP;
		}    	
    }
    
	// Centers the preview on the screen keeping the capture aspect ratio.
    // Remember to call this function after you change the width or height if you want to keep the aspect and the video centered
    public int centerPreview() {
    	if(mVideoPublish != null){
	    	VideoCentering mVideoCentering = new VideoCentering();
	    	mVideoCentering.setAspectRatio(mVideoPublish.width/(float)mVideoPublish.height);
	    	LayoutParams layoutParams = mVideoCentering.getVideoLayoutParams(mVideoCentering.getDisplayMetrics(this.getContext()), this.getLayoutParams());
			setLayoutParams(layoutParams);
			return E_OK;
    	} else {
    		log.debug("Error: could not center screen");
    		return E_COULD_NOT_CENTER;
    	}
	}
    
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
        	 return E_COULD_NOT_OPEN_CAMERA;
         }
         Camera.Parameters parameters = mVideoPublish.mCamera.getParameters();
         parameters.set("camera-id", 2);
         mVideoPublish.mCamera.setParameters(parameters);
         return E_OK;
    }
    
    private int openCamera(){
    	int err = E_OK;
    	
    	if (isAvailableSprintFFC()) { // this device has the specific HTC camera
        	try { // try opening the specific HTC camera
                Method method = Class.forName("android.hardware.HtcFrontFacingCamera").getDeclaredMethod("getCamera", null);
                mVideoPublish.mCamera = (Camera) method.invoke(null, null);
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
	      
	         	return E_COULD_NOT_SET_PREVIEW_DISPLAY_R1;
	    	}
    	} else {
    		log.debug("Error: setDisplay() called without an opened camera");
    		return E_COULD_NOT_SET_PREVIEW_DISPLAY_R2;
    	}
         
        return E_OK; 
    }
    
    private int setParameters(){
    	if(mVideoPublish.mCamera != null){
	    	Camera.Parameters parameters = mVideoPublish.mCamera.getParameters();
	    	log.debug("Setting the capture frame rate to {}", mVideoPublish.frameRate);
	    	parameters.setPreviewFrameRate(mVideoPublish.frameRate);
	    	log.debug("Setting the capture size to {}x{}", mVideoPublish.width, mVideoPublish.height);
	        parameters.setPreviewSize(mVideoPublish.width, mVideoPublish.height); 
	       	mVideoPublish.mCamera.setParameters(parameters);
	       	return E_OK;
    	} else {
    		log.debug("Error: setParameters() called without an opened camera");
    		return E_COULD_NOT_SET_PARAMETERS;
    	}
    }
    
    private int getBufferSize(){
    	if(mVideoPublish.mCamera != null){
	    	PixelFormat pixelFormat = new PixelFormat();
	 		PixelFormat.getPixelFormatInfo(mVideoPublish.mCamera.getParameters().getPreviewFormat(),pixelFormat);
	 		return mVideoPublish.width*mVideoPublish.height*pixelFormat.bitsPerPixel/8;
    	} else {
    		log.debug("Error: getBufferSize() called without an opened camera");
    		return E_COULD_NOT_GET_BUFSIZE;
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
        if(err != E_OK){
        	return err; 
        }

        //we call addCallbackBuffer twice to reduce the "Out of buffers, clearing callback!" problem
        byte[] buffer = new byte[mVideoPublish.bufSize];
        err = addCallbackBuffer_Android2p2(buffer);
        if(err != E_OK){
        	return err; 
        }        
        buffer = new byte[mVideoPublish.bufSize];
        err = addCallbackBuffer_Android2p2(buffer);
        if(err != E_OK){
        	return err; 
        }
        
        err = setPreviewCallbackWithBuffer_Android2p2();
        if(err != E_OK){
        	return err; 
        }
        
        log.debug("Using fast but hidden preview callback");
        return E_OK;
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
    		return E_COULD_NOT_PREPARE_CALLBACK_R1;
    	}
    	if(mVideoPublish.bufSize < E_OK || mVideoPublish.bufSize <= 0){
    		log.debug("Error: prepareCallback() called without a valid mVideoPublish.bufSize");
    		return E_COULD_NOT_PREPARE_CALLBACK_R2;
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
	    	if(setCallbackHidden() != E_OK){
	    		setCallbackSlow();
	    	}
	    } else {
	    	setCallbackSlow();     
	    }
		
		return E_OK;
    }
        
    private int beginPreview(){
    	if(mVideoPublish.mCamera != null){
    		mVideoPublish.mCamera.startPreview();
    		return E_OK;
    	} else {
    		log.debug("Error: beginPreview() called without an opened camera");
    		return E_COULD_NOT_BEGIN_PREVIEW;
    	}
    }
    
    private int initNativeSide(){    
    	if(mVideoPublish.bufSize < E_OK || mVideoPublish.bufSize <= 0){
    		log.debug("Error: initNativeSide() called without a valid mVideoPublish.bufSize");
    		return E_COULD_NOT_INIT_NATIVE_SIDE;
    	}
    	mVideoPublish.initNativeEncoder();
    	return E_OK;
    }
    
    private int startPublisherThread(){
    	if(!mVideoPublish.nativeEncoderInitialized){
    		log.debug("Error: startPublisherThread() called but native capture side not initialized");
    		return E_COULD_NOT_START_PUBLISHER_THREAD_R1;
    	}
    	if(mVideoPublish.isAlive()){
    		log.debug("Error: startPublisherThread() called but publisher thread already running");
    		return E_COULD_NOT_START_PUBLISHER_THREAD_R2;
    	}
	    mVideoPublish.start();
	    return E_OK;
    }
    
    private int startPublisher(){
    	if(!mVideoPublish.nativeEncoderInitialized){
    		log.debug("Error: startPublisher() called but native capture side not initialized");
    		return E_COULD_NOT_START_PUBLISHER_R1;
    	}
    	if(!mVideoPublish.isAlive()){
    		log.debug("Error: startPublisher() called but publisher thread not running");
    		return E_COULD_NOT_START_PUBLISHER_R2;
    	}
    	mVideoPublish.startPublisher();
    	return E_OK;
    }
    
    public int start(){   	
    	int err = E_OK;
    	if(mVideoPublish == null){
    		err = getPublisher();
    		if(err != E_OK){
    			return err;
    		}
    	}
    	
    	// acquires the camera
		err = openCamera();
    	if(err != E_OK){
    		return err; 
    	};
    	    	
    	// sets up the camera parameters
    	err = setParameters();
    	if(err != E_OK){
    		return err;
    	}
    	
    	// gets the size of a not encoded frame
    	mVideoPublish.bufSize = getBufferSize();
    	if(mVideoPublish.bufSize < E_OK){
    		return mVideoPublish.bufSize;
    	}
    	
		err = resume();
		if(err != E_OK){
			return err;
		}
		
		err = initNativeSide();
		if(err != E_OK){
			return err;
		}
    	
    	// start the publisher native thread and sets isCapturing to true
    	err = startPublisherThread();
    	if(err != E_OK){
    		return err;
    	}
    	
    	// start the publisher handler
    	err = startPublisher();
    	if(err != E_OK){
    		return err;
    	}
    	
    	return err;
    }
    
    public int resume(){
    	int err = E_OK;
    	if(!isSurfaceCreated){
    		err = E_COULD_NOT_RESUME_CAPTURE;
    		return err;
       	}
    	
    	// tells it where to draw (sets display for preview)
    	err = setDisplay();
    	if(err != E_OK){
    		return err;
    	}
    	
    	// prepares the callback
    	err = prepareCallback(); 
    	if(err != E_OK){
    		return err;
    	}
    	
    	// begins the preview.
	    err = beginPreview();
	    if(err != E_OK){
	    	return err;
	    }
    	
    	return err;
    }
    
    public void stop(){ 
    	if(mVideoPublish != null){
	    	pause();
	    	
	    	// Because the CameraDevice object is not a shared resource, it's very
	        // important to release it when it will not be used anymore
	    	if(mVideoPublish.mCamera != null){
	    		mVideoPublish.mCamera.release();
	        	mVideoPublish.mCamera = null;
	    	}
	    	
	    	if(mVideoPublish.isCapturing){
	    		mVideoPublish.stopPublisher();
		    	mVideoPublish.endNativeEncoding();
	    	}	 
	    	
	    	mVideoPublish = ((BigBlueButton) getContext().getApplicationContext()).deleteVideoPublish(); 
    	}
    }
    
    public void pause(){
    	if(mVideoPublish != null && mVideoPublish.mCamera != null){
	    	if(!usingFaster){
	    		mVideoPublish.mCamera.setPreviewCallback(null); //this is needed to avoid a crash (http://code.google.com/p/android/issues/detail?id=6201)
	    	}
	    	mVideoPublish.mCamera.stopPreview(); 
    	}
    }
    
    // Checks if addCallbackBuffer and setPreviewCallbackWithBuffer are written but hidden.
    // This method will look for all methods of the android.hardware.Camera class,
    // even the hidden ones.
    private boolean HiddenCallbackWithBuffer(){
    	int exist = 0;    	
    	try {
			Class c = Class.forName("android.hardware.Camera");
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
			Class mC = Class.forName("android.hardware.Camera");
		
			Class[] mPartypes = new Class[1];
			// variable that will hold parameters for a function call
			mPartypes[0] = (new byte[1]).getClass(); //There is probably a better way to do this.
			mAcb = mC.getMethod("addCallbackBuffer", mPartypes);

			mArglist = new Object[1];
		} catch (Exception e) {
			log.debug("Problem setting up for addCallbackBuffer: " + e.toString());
			return E_COULD_NOT_INIT_HIDDEN;
		}
		return E_OK;
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
    		return E_COULD_NOT_ADD_HIDDEN;
    	}
    	return E_OK;
    }
    
    // This method uses reflection to call the setPreviewCallbackWithBuffer method
    // Use this method instead of setPreviewCallback if you want to use manually allocated
    // buffers. Assumes that "this" implements Camera.PreviewCallback
    private int setPreviewCallbackWithBuffer_Android2p2(){ // this function is native since Android 2.2
    	try {
			Class c = Class.forName("android.hardware.Camera");
			Method spcwb = null;  // sets a preview with buffers
			//This way of finding our method is a bit inefficient, but I would have to waste
			// some time figuring out the right way to do it.
			// However, since this method is only called once, this should not cause performance issues			
			
			Method[] m = c.getMethods(); // get all methods of camera
			for(int i=0; i<m.length; i++){
				if(m[i].getName().compareTo("setPreviewCallbackWithBuffer") == 0){
					spcwb = m[i];
					break;
				}
			}
			
//			Class[] mPartypes = new Class[1];
//			mPartypes[0] = (new byte[1]).getClass(); //There is probably a better way to do this.
//			spcwb = c.getMethod("setPreviewCallbackWithBuffer", mPartypes);
			
			//If we were able to find the setPreviewCallbackWithBuffer method of Camera, 
			// we can now invoke it on our Camera instance, setting 'this' to be the
			// callback handler
			if(spcwb != null){
				Object[] arglist = new Object[1];
				arglist[0] = this; // receives a copy of a preview frame
				spcwb.invoke(mVideoPublish.mCamera, arglist);
				//Log.i("AR","setPreviewCallbackWithBuffer: Called method");
			} else {
				log.debug("setPreviewCallbackWithBuffer: Did not find method");
				return E_COULD_NOT_SET_HIDDEN_R1;
			}			
		} catch (Exception e) {
			log.debug("{}",e.toString());
			return E_COULD_NOT_SET_HIDDEN_R2;
    	}
    	return E_OK;
    }
    
    private int getPublisher(){
    	mVideoPublish = ((BigBlueButton) getContext().getApplicationContext()).getVideoPublish();
		if(mVideoPublish == null){
    		log.debug("Error: could not get or instantiate a VideoPublisher");
    		return E_COULD_NOT_GET_PUBLISHER;
    	}
		return E_OK;
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	log.debug("preview surface destroyed");
    	if(mVideoPublish != null && mVideoPublish.isCapturing){ // means that activity changed (because
			   // this surface will only be destroyed
			   // when the activity changes) and the
			   // camera was being captured and published    		
	    	if(BackgroundManager.isApplicationBroughtToBackground(context)){ // means that the next
	    			// doesn't belong to this application
	       		// stops the preview, the publish and releases the camera
	    		// to allow the camera to be used by another application
	    		mVideoPublish.restartWhenResume = true;
	    		stop();	    		
	    	} else {
	       		// pauses the preview and publish
	    		pause();
	    		
	    		// signalizes that the activity has changed and the
		        // camera was being captured //\TODO Gian improve this comment
	    		if(mVideoPublish.isReadyToResume){
	    			mVideoPublish.RequestResume();
	    		} else {
	    			mVideoPublish.allowResume = true;
	    		}
	    	}
    	}
    	isSurfaceCreated = false;
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	log.debug("preview surface changed");
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log.debug("preview surface created");        
        
        if(getPublisher() == E_OK){
        	isSurfaceCreated = true;
        	if(mVideoPublish.isCapturing){
        		if(!mVideoPublish.allowResume){
        			mVideoPublish.readyToResume(this);
        		} else {
        			resume();
        			mVideoPublish.allowResume = false;
        		}
        	} else {
        		if(mVideoPublish.restartWhenResume){
        			start();
        			mVideoPublish.restartWhenResume = false;
        		}
        	}
    	}
    }
    
    @Override
    public void onPreviewFrame (byte[] _data, Camera camera)
    {
    	if(mVideoPublish != null && mVideoPublish.isCapturing){
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