package org.mconf.bbb.android.video;

import java.io.IOException;
import java.lang.reflect.Method;

import org.mconf.bbb.android.BigBlueButton;
import org.mconf.bbb.android.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
    SurfaceHolder mHolder;
    Camera mCamera;
    boolean usingFaster, usingHidden;
    boolean forceDefaultParams = true;
    boolean isSurfaceCreated = false;
    public VideoPublish mVideoPublish;
    int bufSize;
    
    private Method mAcb;       // method for adding a pre-allocated buffer 
    private Object[] mArglist; // list of arguments
    
    public static final int DEFAULT_FRAME_RATE = 15;
    public static final int DEFAULT_WIDTH = 320;
    public static final int DEFAULT_HEIGHT = 240;
    public static final int DEFAULT_BIT_RATE = 512000;
    public static final int DEFAULT_GOP = 5;

	private static final int E_OK = 0;
	private static final int E_COULD_NOT_OPEN_CAMERA = -1;
	private static final int E_COULD_NOT_SET_PREVIEW_DISPLAY_R1 = -2;
	private static final int E_COULD_NOT_SET_PREVIEW_DISPLAY_R2 = -3;
	private static final int E_COULD_NOT_START_CAPTURE = -4;
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
	
    private int frameRate = DEFAULT_FRAME_RATE;
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private int bitRate = DEFAULT_BIT_RATE;
    private int GOP = DEFAULT_GOP;
    
    public VideoCapture(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        mVideoPublish = ((BigBlueButton) getContext().getApplicationContext()).getVideoPublish();
    }
 
    public void setFrameRate(int fr){
    	frameRate = fr;
    }
    
    public void setWidth(int w){
    	width = w;
    }
    
    public void setHeight(int h){
    	height = h;
    }
    
    public void setBitRate(int br){
    	bitRate = br;
    }
    
    public void setGOP(int g){
    	GOP = g;
    }
    
	// Centers the preview on the screen keeping the capture aspect ratio.
    // Remember to call this function after you change the width or height if you want to keep the aspect and the video centered
    public void centerPreview() {
    	VideoCentering mVideoCentering = new VideoCentering();
    	mVideoCentering.setAspectRatio(width/(float)height);
    	LayoutParams layoutParams = mVideoCentering.getVideoLayoutParams(mVideoCentering.getDisplayMetrics(this.getContext()), this.getLayoutParams());
		setLayoutParams(layoutParams);   	
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
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log.debug("preview surface created");
        isSurfaceCreated = true;        
    }
    
    private int openCameraNormalWay(){
    	 if(mCamera != null){
         	mCamera.release();
         	mCamera = null;
         }         
         
         mCamera = Camera.open();
         if(mCamera == null){
        	 log.debug("Error: could not open camera");
        	 return E_COULD_NOT_OPEN_CAMERA;
         }
         Camera.Parameters parameters = mCamera.getParameters();
         parameters.set("camera-id", 2);
         mCamera.setParameters(parameters);
         return E_OK;
    }
    
    private int openCamera(){
    	int err = E_OK;
    	
    	if (isAvailableSprintFFC()) { // this device has the specific HTC camera
        	try { // try opening the specific HTC camera
                Method method = Class.forName("android.hardware.HtcFrontFacingCamera").getDeclaredMethod("getCamera", null);
                mCamera = (Camera) method.invoke(null, null);
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
    	if(mCamera != null){
	        try {
	            mCamera.setPreviewDisplay(mHolder);
	        } catch (IOException exception) {
	        	log.debug("Error: could not set preview display"); 
	         	log.debug(exception.toString());
	     	
	         	mCamera.release();
	         	mCamera = null;
	      
	         	return E_COULD_NOT_SET_PREVIEW_DISPLAY_R1;
	    	}
    	} else {
    		log.debug("Error: setDisplay() called without an opened camera");
    		return E_COULD_NOT_SET_PREVIEW_DISPLAY_R2;
    	}
         
        return E_OK; 
    }
    
    private int setParameters(){
    	if(mCamera != null){
	    	Camera.Parameters parameters = mCamera.getParameters();
	    	log.debug("Setting the capture frame rate to {}", frameRate);
	    	parameters.setPreviewFrameRate(frameRate);
	    	log.debug("Setting the capture size to {}x{}", width, height);
	        parameters.setPreviewSize(width, height); 
	       	mCamera.setParameters(parameters);
	       	return E_OK;
    	} else {
    		log.debug("Error: setParameters() called without an opened camera");
    		return E_COULD_NOT_SET_PARAMETERS;
    	}
    }
    
    private int getBufferSize(){
    	if(mCamera != null){
	    	PixelFormat pixelFormat = new PixelFormat();
	 		PixelFormat.getPixelFormatInfo(mCamera.getParameters().getPreviewFormat(),pixelFormat);
	 		return width*height*pixelFormat.bitsPerPixel/8;
    	} else {
    		log.debug("Error: getBufferSize() called without an opened camera");
    		return E_COULD_NOT_GET_BUFSIZE;
    	}
    }
    
    private int prepareCallback(){
    	if(mCamera == null){
    		log.debug("Error: prepareCallback() called without an opened camera");
    		return E_COULD_NOT_PREPARE_CALLBACK_R1;
    	}
    	if(bufSize < E_OK || bufSize <= 0){
    		log.debug("Error: prepareCallback() called without a valid bufSize");
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
			log.debug("Using fast preview callback");
			usingFaster = true;
			usingHidden = false;
			byte[] buffer = new byte[bufSize];
			mCamera.addCallbackBuffer(buffer);
			buffer = new byte[bufSize];
			mCamera.addCallbackBuffer(buffer);
			mCamera.setPreviewCallbackWithBuffer(this);
	    } else if(HiddenCallbackWithBuffer()) { //} else if(has the methods hidden){
	    	log.debug("Using fast but hidden preview callback");
	        usingFaster = true;
	        usingHidden = true;
			 //Must call this before calling addCallbackBuffer to get all the
	        // reflection variables setup
	        initForACB();
	        //we call addCallbackBuffer twice to reduce the "Out of buffers, clearing callback!" problem
	        byte[] buffer = new byte[bufSize];
	        addCallbackBuffer_Android2p2(buffer);   
	        buffer = new byte[bufSize];
	        addCallbackBuffer_Android2p2(buffer);
	        setPreviewCallbackWithBuffer_Android2p2();
	    } else {
	    	log.debug("Using slow preview callback");
	    	usingFaster = false;
	    	usingHidden = false;
	    	mCamera.setPreviewCallback(this);        
	    }
		
		return E_OK;
    }
        
    private int beginPreview(){
    	if(mCamera != null){
    		mCamera.startPreview();
    		return E_OK;
    	} else {
    		log.debug("Error: beginPreview() called without an opened camera");
    		return E_COULD_NOT_BEGIN_PREVIEW;
    	}
    }
    
    private int initNativeSide(){    
    	if(bufSize < E_OK || bufSize <= 0){
    		log.debug("Error: initNativeSide() called without a valid bufSize");
    		return E_COULD_NOT_INIT_NATIVE_SIDE;
    	}
    	mVideoPublish.initNativeEncoder(bufSize, width, height, frameRate, bitRate, GOP);
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
    	
		// acquires the camera
		err = openCamera();
    	if(err != E_OK){
    		return err; 
    	};
    	
    	// tells it where to draw (sets display for preview)
    	err = setDisplay();
    	if(err != E_OK){
    		return err;
    	}
	    	
    	// sets up the camera parameters
    	err = setParameters();
    	if(err != E_OK){
    		return err;
    	}
    	    	
    	// gets the size of a not encoded frame
    	bufSize = getBufferSize();
    	if(bufSize < E_OK){
    		return bufSize;
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
    	pause();
    	
    	if(mVideoPublish.isCapturing){
    		mVideoPublish.stopPublisher();
	    	mVideoPublish.endNativeEncoding();
    	}	 
    }
    
    public void pause(){
    	if(mCamera != null){
	    	if(!usingFaster){
	    		mCamera.setPreviewCallback(null); //this is needed to avoid a crash (http://code.google.com/p/android/issues/detail?id=6201)
	    	}
	    	mCamera.stopPreview();   
	    	    	
	    	// Because the CameraDevice object is not a shared resource, it's very
	        // important to release it when it will not be used anymore
	    	mCamera.release();
	        mCamera = null;   
    	}
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	log.debug("preview surface destroyed");
    	if(mVideoPublish.isCapturing){ // means that activity changed (because
    								   // this surface will only be destroyed
    								   // when the activity changes) and the
    								   // camera was being captured and published
    		// pauses the preview and publish
    		pause();
    		
    		// signalizes that the activity has changed and the
	        // camera was being captured
    		Intent intent = new Intent(Client.CLOSE_VIDEO_CAPTURE);
	        getContext().sendBroadcast(intent);
    	}
    	isSurfaceCreated = false;
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	log.debug("preview surface changed");
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

    private void initForACB(){
    	try {
			Class mC = Class.forName("android.hardware.Camera");
		
			Class[] mPartypes = new Class[1];
			// variable that will hold parameters for a function call
			mPartypes[0] = (new byte[1]).getClass(); //There is probably a better way to do this.
			mAcb = mC.getMethod("addCallbackBuffer", mPartypes);

			mArglist = new Object[1];
		} catch (Exception e) {
			log.debug("Problem setting up for addCallbackBuffer: " + e.toString());
		}
    }
    
    // This method uses reflection to call the addCallbackBuffer method
    // It allows you to add a byte buffer to the queue of buffers to be used by preview.
    // Real addCallbackBuffer implementation: http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/android/hardware/Camera.java;hb=9db3d07b9620b4269ab33f78604a36327e536ce1
    // @param b The buffer to register. Size should be width * height * bitsPerPixel / 8.
    private void addCallbackBuffer_Android2p2(byte[] b){ //  this function is native since Android 2.2
    	//Check to be sure initForACB has been called to setup
    	// mAcb and mArglist
//    	if(mArglist == null){
//    		initForACB();
//    	}

    	mArglist[0] = b;
    	try {
    		mAcb.invoke(mCamera, mArglist);
    	} catch (Exception e) {
    		log.debug("invoking addCallbackBuffer failed: " + e.toString());
    	}
    }
    
    // This method uses reflection to call the setPreviewCallbackWithBuffer method
    // Use this method instead of setPreviewCallback if you want to use manually allocated
    // buffers. Assumes that "this" implements Camera.PreviewCallback
    private void setPreviewCallbackWithBuffer_Android2p2(){ // this function is native since Android 2.2
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
				spcwb.invoke(mCamera, arglist);
				//Log.i("AR","setPreviewCallbackWithBuffer: Called method");
			} else {
				log.debug("setPreviewCallbackWithBuffer: Did not find method");
			}
			
		} catch (Exception e) {
			log.debug("{}",e.toString());
		}
    }
    
    @Override
    public void onPreviewFrame (byte[] _data, Camera camera)
    {
    	if(mVideoPublish.isCapturing){
    		if(usingHidden){ 
    			addCallbackBuffer_Android2p2(_data);
    		} else if(usingFaster){
    			mCamera.addCallbackBuffer(_data);
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