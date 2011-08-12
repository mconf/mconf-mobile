package org.mconf.bbb.android.video;

public class CaptureConstants {
	//errors:
	public static final int E_OK = 0;
	public static final int E_COULD_NOT_OPEN_CAMERA = -1;
	public static final int E_COULD_NOT_SET_PREVIEW_DISPLAY_R1 = -2;
	public static final int E_COULD_NOT_SET_PREVIEW_DISPLAY_R2 = -3;
	public static final int E_COULD_NOT_REQUEST_RESUME = -4;
	public static final int E_COULD_NOT_SET_PARAMETERS = -5;
	public static final int E_COULD_NOT_GET_BUFSIZE = -6;
	public static final int E_COULD_NOT_PREPARE_CALLBACK_R1 = -7;
	public static final int E_COULD_NOT_PREPARE_CALLBACK_R2 = -8;
	public static final int E_COULD_NOT_INIT_NATIVE_SIDE = -9;
	public static final int E_COULD_NOT_BEGIN_PREVIEW = -10;
	public static final int E_COULD_NOT_START_PUBLISHER_THREAD_R1 = -11;
	public static final int E_COULD_NOT_START_PUBLISHER_THREAD_R2 = -12;
	public static final int E_COULD_NOT_START_PUBLISHER_R1 = -13;
	public static final int E_COULD_NOT_START_PUBLISHER_R2 = -14;
	public static final int E_COULD_NOT_RESUME_CAPTURE = -15;
	public static final int E_COULD_NOT_INIT_HIDDEN = -16;
	public static final int E_COULD_NOT_SET_HIDDEN_R1 = -17;
	public static final int E_COULD_NOT_SET_HIDDEN_R2 = -18;
	public static final int E_COULD_NOT_ADD_HIDDEN = -19;
	public static final int E_COULD_NOT_SET_FR = -20;
	public static final int E_COULD_NOT_SET_W = -21;
	public static final int E_COULD_NOT_SET_H = -22;
	public static final int E_COULD_NOT_SET_BR = -23;
	public static final int E_COULD_NOT_SET_GOP = -24;
	public static final int E_COULD_NOT_CENTER = -25;
	public static final int E_COULD_NOT_GET_PUBLISHER = -26;
	public static final int E_COULD_NOT_GET_FR = -27;
	public static final int E_COULD_NOT_GET_W = -28;
	public static final int E_COULD_NOT_GET_H = -29;
	public static final int E_COULD_NOT_GET_BR = -30;
	public static final int E_COULD_NOT_GET_GOP = -31;
	
	//video parameters constants:
	public static final int DEFAULT_FRAME_RATE = 10;
	public static final int DEFAULT_WIDTH = 320;
	public static final int DEFAULT_HEIGHT = 240;
	public static final int DEFAULT_BIT_RATE = 128000;
	public static final int DEFAULT_GOP = 5;
	
	//constants to tell the state of the capture/encoding/publish:
	public static final int STOPPED = 0; // no video is being captured, the native threads are closed and the video publisher does not exist 
	public static final int RESUMED = 1; // video is being captured, encoded and published
	public static final int PAUSED = 2;	// no video is being captured, the native encoding thread is running and waiting for frames, and the video publisher exists and is waiting for frames
	public static final int ERROR = 3; // an error occured, and it is not possible to determine the state of the capture/encoding/publish
	
	//notification ID:
	public static final int VIDEO_PUBLISH_NOTIFICATION_ID = 57634;
}
