package org.mconf.bbb.android.video;

import java.io.IOException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoCapture extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {
	
	private static final Logger log = LoggerFactory.getLogger(VideoCapture.class);
    SurfaceHolder mHolder;
    Camera mCamera;
    private byte[] mJavaBuffer;
    private int bufSize;
    
    public VideoCapture(Context context) {
        super(context);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    // VideoCapture is started or resumed
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();
        try {
           //mCamera.setPreviewDisplay(holder);
        	mCamera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
      	}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        
        endEncoder();
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	// Now that the size is known, set up the camera parameters, init the native side and begin
        // the preview.        
        Camera.Parameters parameters = mCamera.getParameters();
//        Log.v("Java", String.format("PICTURE FORMAT %d\n",parameters.getPictureFormat()));
//        Log.v("Java", String.format("PREVIEW FORMAT %d\n",parameters.getPreviewFormat()));
//        Log.v("Java", String.format("PREVIEW FRAMERATE %d\n",parameters.getPreviewFrameRate()));
        int frameRate = 15;
        parameters.setPreviewFrameRate(frameRate);        
        parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
//        Log.v("Java", String.format("PREVIEW FRAMERATE %d\n",parameters.getPreviewFrameRate()));
        
        PixelFormat p = new PixelFormat();
		PixelFormat.getPixelFormatInfo(parameters.getPreviewFormat(),p);
//		log.debug("Pixel Format {}", parameters.getPreviewFormat());
//	    log.debug("Bytes per pixel {}, Bits per pixel {}", p.bytesPerPixel, p.bitsPerPixel);
		bufSize = (w*h*p.bitsPerPixel)/8;
		mJavaBuffer = new byte[bufSize];
		initEncoder(w, h, frameRate);        
        
        //hack (idea from http://code.google.com/p/android/issues/detail?id=2794):
        //This kind of hack is safe to be used as explained in the official android documentation
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
        //In order to use it, we need to use Java Reflection (and this is what this hack consists on).
        // TODO Gian: until now we know that the setPreviewCallbackWithBuffer method can be called 
        //using Java reflection on 2.0.1 and higher android versions. We have to test this hack on emulator
        //on lower android versions to determine what is the lower version which we can use this.
        //Then, the idea is to verify the version on the fly like this:
        //if(version >= 2.2){then call setPreviewCallbackWithBuffer normally}
        //elseif(version < 2.2 AND version supports the hack){then use the hack}
        //else{use the setPreviewCallback method or look for an alternative if the performance is too low}        
//	    if(2.2 or higher){
//	    	mCamera.addCallbackBuffer(callbackBuffer);
//			mCamera.setPreviewCallbackWithBuffer(this);
//	    } else if(supports hack) {
			 //Must call this before calling addCallbackBuffer to get all the
	        // reflection variables setup
	        initForACB();
	        //we call addCallbackBuffer twice to reduce the "Out of buffers, clearing callback!" problem
	        byte[] buffer = new byte[bufSize];
	        addCallbackBuffer_Android2p2(buffer);   
	        buffer = new byte[bufSize];
	        addCallbackBuffer_Android2p2(buffer);
	        setPreviewCallbackWithBuffer_Android2p2();
//    	} else {
//	        mCamera.setPreviewCallback(this);        
//    	}
        
        mCamera.startPreview();
    }
    
    // This method will list all methods of the android.hardware.Camera class,
    // even the hidden ones. With the information it provides, you can use the same
    // approach below to expose methods that were written but hidden
    private void listAllCameraMethods(){
    	try {
			Class c = Class.forName("android.hardware.Camera");
			Method[] m = c.getMethods();
			for(int i=0; i<m.length; i++){
				log.debug("  method:"+m[i].toString());
			}
		} catch (Exception e) {
			log.debug("{}",e.toString());
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
    
    public void onPreviewFrame (byte[] _data, Camera camera)
    {
       	addCallbackBuffer_Android2p2(_data);
       	enqueueFrame(_data,_data.length);
    }
    
    public byte[] assignJavaBuffer()
	{
    	return mJavaBuffer;
	}
    
    public int onReadyFrame ()
    {
    	//mJavaBuffer has the value of the decoded frame
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