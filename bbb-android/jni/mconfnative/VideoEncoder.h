#ifndef _VIDEO_ENCODER_H_
#define _VIDEO_ENCODER_H_

#include <jni.h>

#include "iva/encode/EncodeVideo.h"
#include "iva/encode/EncodeVideoParams.h"

class VideoEncoder {

private:
	EncodeVideo* video_enc;
	queue_t* decoded_video,
		* encoded_video;
	queue_consumer_t* consumer;

	bool stopThread;
	Thread<VideoEncoder> * _thread;
	Mutex _mutex;

	int8_t * sharedBuffer;
	jobject JavaSenderClass;
	jmethodID JavaOnReadyFrame;

	int pixels, halfpixels, quarterpixels, halfby;
	uint8_t *aux;

public:
	VideoEncoder(JNIEnv *env, jobject obj, jint width, jint height, jint frameRate) {
		Log("VideoEncoder() begin");

		EncodeVideoParams * paramsVideo = new EncodeVideoParams();
		setVideoParams(width, height, frameRate, &paramsVideo);

		initNV21toYUV420P(width, height); //use this if it is necessary to convert from yuv420sp to yuv420p before encoding

		decoded_video = queue_create();
		encoded_video = queue_create();

		video_enc = new EncodeVideo();
		int ret = video_enc->open(paramsVideo);
		if (ret != E_OK)
			Log("Error on opening the parameters");

		ret = video_enc->start(decoded_video, encoded_video);
		if (ret != E_OK)
			return;
		Log("threadFunction() encode started");

		Log("VideoEncoder() end");
	}

	~VideoEncoder() {
		Log("~VideoEncoder() begin");

		// \todo syncronize the finalize of the encoder to solve the end problem!
		Seconds(1).sleep();

		stopThread = true;
		queue_broadcast(encoded_video);

		//TODO Check if video_enc was created before we can stop it?
		video_enc->stop();
		delete video_enc;

		queue_destroy(&decoded_video);
		queue_destroy(&encoded_video);

		Log("~VideoEncoder() end");
	}

	//initialize the JNI variables and functions and assign the java array with the C++ array for the encoded frames
	int assignBuffers(JNIEnv *env, jobject obj){
		JavaSenderClass = env->NewGlobalRef(obj);
		jclass JavaSenderObject = NULL;
		JavaSenderObject = env->GetObjectClass(JavaSenderClass);
		JavaOnReadyFrame = NULL;
		JavaOnReadyFrame = env->GetMethodID(JavaSenderObject, "onReadyFrame", "(II)I");
		jmethodID JavaAssignBuffers = NULL;
		JavaAssignBuffers = env->GetMethodID(JavaSenderObject, "assignJavaBuffer", "()[B");
		jbyteArray javaBufferJNI = NULL;
		javaBufferJNI = (_jbyteArray*)env->CallObjectMethod( JavaSenderClass, JavaAssignBuffers );
		jboolean isCopy = JNI_TRUE;
		sharedBuffer = (int8_t*)env->GetByteArrayElements(javaBufferJNI, &isCopy); // this call assigns the C++ buffer with
																				 // the Java buffer that will be the encoded frame
		if( !sharedBuffer )
		{
			Log("ERROR: JNI::GetByteArrayElements() failed!");
			return -1;
		}
		if( isCopy == JNI_TRUE )
			Log("WARNING: JNI returns a copy of byte array - this is slow");
		return 0;
	}

	void senderLoop(JNIEnv *env, jobject obj){
		if(assignBuffers(env, obj) != 0){
			Log("Error initializing the JNI objects");
			// TODO Gian handle this error
//			return;
		}

		uint8_t * buffer;
		uint32_t timestamp, bufferSize;
		QueueExtraData * extraData;
		consumer = queue_registerConsumer(encoded_video);
		if (consumer)
			Log("consumer registered");
		stopThread = false;
		while (!stopThread) {
			if (queue_dequeueCond(consumer, &buffer, &bufferSize, &timestamp, &extraData) != E_OK) {
				continue;
			}
			queue_free(consumer);
			// TODO Gian we could avoid this memcpy if we passed the "&sharedBuffer" in the queue_dequeueCond
			// function in the place of the "&buffer". However, if we do that, the C++ buffer loses its link
			// with the Java buffer.
			// See if it is possible to do this without losing the link.
			// If it is not possible, then check if it is possible to avoid the memcpy by linking them again.
			memcpy(&sharedBuffer[1], buffer, bufferSize);
			sharedBuffer[0] = 18;
			sharedBuffer[0] = 34;
			sharedBuffer[0] = 50;
//			logBuffer(0,100);
			// The shared buffer has a new encoded frame. Lets callback java to sinalize it
			env->CallIntMethod( JavaSenderClass, JavaOnReadyFrame, (jint)(bufferSize+1), (jint)timestamp );
		}
		queue_unregisterConsumer(&consumer);
	}

	void enqueueFrame(uint8_t* data, int length) {
		//TODO Gian: check if ffmpeg encodes from yuv420sp to FLV directly.
		//If not, it may be necessary to convert from yuv420sp to yuv420p first
		//using the function NV21toYUV420P.
		//PS: for the mpeg4 codec this step is necessary.
		NV21toYUV420P(&data);
	    if (queue_enqueue(decoded_video, (uint8_t*)data, length, Milliseconds().getTime(), NULL) != E_OK) {
	        Log("enqueueFrame() fail");
	        return;
	    }
//	    Log("enqueueFrame() done");
	}

	void setVideoParams(jint width, jint height, jint frameRate, EncodeVideoParams ** paramsVideo){
		//		pixel format:
		//		default:IvaPixFmt::FMT_YUV420P
		//		possible:
		//	    FMT_NONE = 0,  ///< non valid pixel format
		//	    FMT_YUV420P,   ///< planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2 Y samples)
		//	    FMT_RGB24,     ///< packed RGB 8:8:8, 24bpp, RGBRGB...
		//	    FMT_RGB32,
		//	    FMT_BGR32,
		//	    FMT_YUV422P,   ///< planar YUV 4:2:2, 16bpp, (1 Cr & Cb sample per 2x1 Y samples)
		//	    FMT_YUV411P,   ///< planar YUV 4:1:1, 12bpp, (1 Cr & Cb sample per 4x1 Y samples)
		//	    FMT_RGB555,
		//	    FMT_DVSD,      ///< DV25
		//	    FMT_COUNT      ///< to do loops with the enum
		//		(*paramsVideo)->setPixelsFormat(IvaPixFmt::FMT_YUV420P);
		//TODO Gian create a iva constant to allow yuv420sp and check if ffmpeg encodes from yuv420sp to flv

		//		codec:
		//		default:COMMON_CODEC_NONE
		//		possible:
		//		COMMON_VIDEO_DEFAULT_CODEC            COMMON_CODEC_VIDEO_MPEG4  ///< Codec default
		//		COMMON_CODEC_NONE                   0        ///< Codec not specified
		//		COMMON_CODEC_VIDEO_MPEG2            10       ///< MPEG-2
		//		COMMON_CODEC_VIDEO_MPEG4            11       ///< MPEG-4
		//		COMMON_CODEC_VIDEO_H264             12       ///< H.264
		//		COMMON_CODEC_VIDEO_DV25             13       ///< DV25
				(*paramsVideo)->setCodec(COMMON_CODEC_VIDEO_FLV);

		//		bitrate:
		//		default:COMMON_VIDEO_DEFAULT_BITRATE
		//		possible:
		//		COMMON_VIDEO_DEFAULT_BITRATE          1400000                   ///< Default (bit/s)
		//		iva docs: any value between 100000 and 4000000
				(*paramsVideo)->setBitRate(256000);

		//		framerate:
		//		default:COMMON_VIDEO_DEFAULT_FPS
		//		possible:
		//		COMMON_VIDEO_DEFAULT_FPS              30                        ///< default
		//		COMMON_CODEC_VIDEO_MIN_FPS          2       ///< lowest
		//		COMMON_CODEC_VIDEO_MAX_FPS          60      ///< highest
		//		iva docs: any value between 1 and 60
				(*paramsVideo)->setFrameRate(frameRate);

		//		width:
		//		default:COMMON_VIDEO_DEFAULT_WIDTH
		//		possible:
		//		COMMON_VIDEO_DEFAULT_WIDTH            720                       ///< Default
		//		COMMON_CODEC_VIDEO_MIN_WIDTH        180     ///< lowest
		//		COMMON_CODEC_VIDEO_MAX_WIDTH        1440    ///< highest
		//		iva docs: 1280, 720 and 360 are allowed
				(*paramsVideo)->setWidth(width);

		//		heigth:
		//		default:COMMON_VIDEO_DEFAULT_HEIGHT
		//		possible:
		//		COMMON_VIDEO_DEFAULT_HEIGHT           480                       ///< default
		//		COMMON_CODEC_VIDEO_MIN_HEIGHT       120     ///< Lowest
		//		COMMON_CODEC_VIDEO_MAX_HEIGHT       960     ///< Highest
		//		iva docs: 720, 480 e 240 are allowed
				(*paramsVideo)->setHeight(height);

		//		gop:
		//		default:COMMON_VIDEO_DEFAULT_GOP
		//		possible:
		//		COMMON_VIDEO_DEFAULT_GOP              12                        ///< GOP default
				(*paramsVideo)->setGopSize(5);
	}

	// Initializes global vars to optimize the conversion.
	// This method must be called only once, from an initializing function.
	// This method should also be called if the w or h changes.
	void initNV21toYUV420P(int w, int h){
		pixels = w*h;
		halfpixels = pixels/2;
		quarterpixels = pixels/4;
		halfby = quarterpixels+pixels;

		if(aux){
			aux = NULL;
			free(aux);
		}
		aux = (uint8_t *) malloc(halfpixels);
	}

	// converts in (passed as reference) from nv21 (yuv420sp) to yuv420p.
	// initNV21toYUV420p must be called before using this function for the first time
	// and when w or h changes.
	void NV21toYUV420P(uint8_t **in){
		memcpy(aux,&(*in)[pixels],halfpixels);
		int count = -pixels;
		for(int i = pixels; i < halfby; i++){
			(*in)[i+quarterpixels] = aux[i+count];
			count++;
			(*in)[i] = aux[i+count];
		}
	}

	//prints the C buffer to the screen from initPos to finalPos
	void logBuffer(int initPos, int finalPos){
		LogData log;
		for(int i = initPos; i < finalPos; i++){
			log.clear();
			log << "cBuffer[" << i << "]" << " = " << (int)sharedBuffer[i] << endl;
			log.push();
		}
	}
};

#endif
