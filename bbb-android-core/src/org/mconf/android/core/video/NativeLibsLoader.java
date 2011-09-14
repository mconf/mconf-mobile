package org.mconf.android.core.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeLibsLoader {
	private static final Logger log = LoggerFactory.getLogger(NativeLibsLoader.class);
	private static boolean captureLibsLoaded = false;
	private static boolean capturePlaybackLoaded = false;
	
	public static void loadCaptureLibs(String packageName) throws SecurityException {
		if (captureLibsLoaded)
			return;
		
		String path = "/data/data/" + packageName + "/lib/";
    	System.load(path + "libavutil.so");
    	System.load(path + "libswscale.so");
        System.load(path + "libavcodec.so");
        System.load(path + "libavformat.so");
        System.load(path + "libthread.so");
    	System.load(path + "libcommon.so");
    	System.load(path + "libqueue.so");
    	System.load(path + "libencode.so");
    	System.load(path + "libmconfnativeencodevideo.so");  
    
    	log.debug("Native capture libraries loaded");
    	captureLibsLoaded = true;
	}
	
	public static void loadPlaybackLibs(String packageName) throws SecurityException {
		if (capturePlaybackLoaded)
			return;
		
		String path = "/data/data/" + packageName + "/lib/";
		System.load(path + "libavutil.so");
		System.load(path + "libswscale.so");
		System.load(path + "libavcodec.so");
		System.load(path + "libthread.so");
		System.load(path + "libcommon.so");
		System.load(path + "libqueue.so");
		System.load(path + "libdecode.so");
		System.load(path + "libmconfnativeshowvideo.so");
    
    	log.debug("Native playback libraries loaded");
    	capturePlaybackLoaded = true;
	}
}
