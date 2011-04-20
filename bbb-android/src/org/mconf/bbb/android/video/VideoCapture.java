package org.mconf.bbb.android.video;

import java.io.IOException;
import java.lang.reflect.Method;

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
    Camera mCamera;
    private byte[] sharedBuffer;
    boolean isAvailableSprintFFC;
    boolean usingHidden;
    boolean usingSlow;
    
    public VideoCapture(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void centerPreview(boolean inDialog) {
    	// Centers the preview on the screen
    	LayoutParams layoutParams = VideoCentering.getVideoLayoutParams(VideoCentering.getDisplayMetrics(this.getContext(), inDialog), this.getLayoutParams());
		setLayoutParams(layoutParams);   	
	}
    
    private void checkForSpecificHTCFFCCamera()
    {
        try {
            Class.forName("android.hardware.HtcFrontFacingCamera");
            isAvailableSprintFFC = true;
        } catch (Exception ex) {
            isAvailableSprintFFC = false;
        }
    }
    
    // VideoCapture is started or resumed
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.       
        checkForSpecificHTCFFCCamera();
        if (isAvailableSprintFFC) {
        	try {
                Method method = Class.forName("android.hardware.HtcFrontFacingCamera").getDeclaredMethod("getCamera", null);
                mCamera = (Camera) method.invoke(null, null);
            } catch (Exception ex) {
                log.debug(ex.toString());
                // TODO Gian better error handling here?
            }
        } else {
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("camera-id", 2);
            mCamera.setParameters(parameters);
        }    
        
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO Gian better error handling here?
       	}
        
        // Set up the camera parameters, init the native side and begin
        // the preview. 
    	Camera.Parameters parameters = mCamera.getParameters();
//        Log.v("Java", String.format("PICTURE FORMAT %d\n",parameters.getPictureFormat()));
//        Log.v("Java", String.format("PREVIEW FORMAT %d\n",parameters.getPreviewFormat()));
//        Log.v("Java", String.format("PREVIEW FRAMERATE %d\n",parameters.getPreviewFrameRate()));
		final int frameRate = 10;        
		final int widthCaptureResolution = 320;
	    final int heightCaptureResolution = 240;
        parameters.setPreviewFrameRate(frameRate);        
        parameters.setPreviewSize(widthCaptureResolution, heightCaptureResolution);        
        mCamera.setParameters(parameters);
//        Log.v("Java", String.format("PREVIEW FRAMERATE %d\n",parameters.getPreviewFrameRate()));
        
        PixelFormat pixelFormat = new PixelFormat();
		PixelFormat.getPixelFormatInfo(parameters.getPreviewFormat(),pixelFormat);
		final int bufSize = (widthCaptureResolution*heightCaptureResolution*pixelFormat.bitsPerPixel)/8;
		sharedBuffer = new byte[bufSize]; //the encoded frame will never be bigger than the not encoded	
		initEncoder(widthCaptureResolution, heightCaptureResolution, frameRate);        
        
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
        //In order to use it, we need to use Java Reflection.      
		if (Integer.parseInt(Build.VERSION.SDK) >= 8){ //if(2.2 or higher){
			log.debug("Using fast preview callback");
			usingHidden = false;
			usingSlow = false;
			byte[] buffer = new byte[bufSize];
			mCamera.addCallbackBuffer(buffer);
			mCamera.setPreviewCallbackWithBuffer(this);
	    } else if(HiddenCallbackWithBuffer()) { //} else if(has the methods hidden){
	    	log.debug("Using fast but hidden preview callback");
	        usingHidden = true;
			usingSlow = false;
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
	    	usingHidden = false;
	    	usingSlow = true;
	    	mCamera.setPreviewCallback(this);        
	    }
        
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
    	if(usingSlow){
    		mCamera.setPreviewCallback(null); //this is needed to avoid a crash (http://code.google.com/p/android/issues/detail?id=6201)
    	}
    	mCamera.stopPreview();
        endEncoder();  
        mCamera.release();
        mCamera = null;              
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	
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
    
    Method mAcb;       // method for adding a pre-allocated buffer 
    Object[] mArglist; // list of arguments

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
    	if(mArglist == null){
    		initForACB();
    	}

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
       	if(usingHidden){
       		addCallbackBuffer_Android2p2(_data);
       	}
       	enqueueFrame(_data,_data.length);
    }
    
    public byte[] assignJavaBuffer()
	{
    	return sharedBuffer;
	}
    
    public int onReadyFrame (int bufferSize)
    {
    	//sharedBuffer has the data of the encoded frame
    	//bufferSize has the length of the encoded frame (it is <= sharedBuffer.length)
    	return 0;
    }
    
    static {
    	System.loadLibrary("avutil");
    	System.loadLibrary("swscale");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("thread");
    	System.loadLibrary("common");
    	System.loadLibrary("queue");
    	System.loadLibrary("encode");
    	System.loadLibrary("mconfnativeencodevideo");  
        
    	log.debug("Video native libraries loaded");    
    }
    
    private native int initEncoder(int width, int height, int frameRate);
	private native int endEncoder();
    private native int enqueueFrame(byte[] data, int length);	
}