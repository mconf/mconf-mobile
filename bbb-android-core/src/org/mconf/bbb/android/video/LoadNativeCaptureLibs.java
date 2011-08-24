package org.mconf.bbb.android.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadNativeCaptureLibs {
	private static final Logger log = LoggerFactory.getLogger(LoadNativeCaptureLibs.class);
	
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
}
