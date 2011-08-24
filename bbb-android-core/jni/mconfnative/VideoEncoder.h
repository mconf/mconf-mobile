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

	bool stopThread, encoding;

	jbyteArray javaBufferJNI;
	int8_t * sharedBuffer;
	jobject JavaSenderClass;
	jmethodID JavaOnReadyFrame;

	int pixels, halfpixels, quarterpixels, halfby;
	uint8_t *aux;

public:
	VideoEncoder(JNIEnv *env, jobject obj, jint width, jint height, jint frameRate, jint bitRate, jint GOP)
					: encoding(false) {
		Log("VideoEncoder() begin");

		EncodeVideoParams * paramsVideo = new EncodeVideoParams();
		setVideoParams(width, height, frameRate, bitRate, GOP, &paramsVideo);

		initNV21toYUV420P(width, height);

		decoded_video = queue_create();
		encoded_video = queue_create();

		video_enc = new EncodeVideo();
		int ret = video_enc->open(paramsVideo);
		if (ret != E_OK){
			Log("Error on opening the parameters");
			return;
		}

		ret = video_enc->start(decoded_video, encoded_video);
		if (ret != E_OK){
			Log("Error on starting the encoder");
			return;
		}

		Log("threadFunction() encode started");

		Log("VideoEncoder() end");
	}

	~VideoEncoder() {
		Log("~VideoEncoder() begin");

		stopThread = true;
		queue_broadcast(encoded_video);

		while(encoding){
			Milliseconds(100).sleep();
		}

		video_enc->stop();
		queue_unregisterConsumer(&consumer);

		queue_destroy(&decoded_video);
		queue_destroy(&encoded_video);

		delete video_enc;

		Log("~VideoEncoder() end");
	}

	//initialize the JNI variables and functions and assign the java array with the C++ array for the encoded frames
	int assignBuffers(JNIEnv *env, jobject obj){
		JavaSenderClass = env->NewGlobalRef(obj);
		jclass JavaSenderObject = env->GetObjectClass(JavaSenderClass);
		JavaOnReadyFrame = env->GetMethodID(JavaSenderObject, "onReadyFrame", "(II)I");
		jmethodID JavaAssignBuffers = env->GetMethodID(JavaSenderObject, "assignJavaBuffer", "()[B");
		javaBufferJNI = (_jbyteArray*)env->CallObjectMethod( JavaSenderClass, JavaAssignBuffers );
		jboolean isCopy;
		// this call assigns the C++ buffer with
		// the Java buffer that will be the encoded frame
		sharedBuffer = (int8_t*)env->GetByteArrayElements(javaBufferJNI, &isCopy);
		if( !sharedBuffer )
		{
			Log("ERROR: JNI::GetByteArrayElements() failed!");
			return -1;
		}
		if( isCopy == JNI_TRUE )
			Log("WARNING: JNI returns a copy of byte array - this is slow");
		return 0;
	}

//	int getFrameType(int *frametypeManager){
//		(*frametypeManager)++;
//		switch (*frametypeManager) {
//		  case 1:
//		  	  return 18; //0x12
//		  case 2:
//		  case 4:
//		  	  return 50; //0x32
//		  case 3:
//		  case 5:
//			  return 34; //0x22
//		  default:
//			*frametypeManager = 1;
//		    return 18;
//		}
//	}

	void senderLoop(JNIEnv *env, jobject obj){
		if(assignBuffers(env, obj) != 0){
			Log("Error initializing the JNI objects");
			return;
		}

		uint8_t * buffer;
		uint32_t timestamp, bufferSize;
		QueueExtraData * extraData;
		consumer = queue_registerConsumer(encoded_video);
		if (consumer){
			Log("consumer registered");
		} else {
			Log("Error registering the encode consumer");
			return;
		}

//		bool firstFrameIDetected = false;
//		int frametypeManager = 0;
		sharedBuffer[0] = 18;
		encoding = true;
		stopThread = false;
		while (!stopThread) {
			if (queue_dequeueCond(consumer, &buffer, &bufferSize, &timestamp, &extraData) != E_OK) {
				continue;
			}
			queue_free(consumer);
			// TODO we could avoid this memcpy if we passed the "&sharedBuffer" in the queue_dequeueCond
			// function in the place of the "&buffer". However, if we do that, the C++ buffer loses its link
			// with the Java buffer.
			// See if it is possible to do this without losing the link.
			// If it is not possible, then check if it is possible to avoid the memcpy by linking them again.
			memcpy(&sharedBuffer[1], buffer, bufferSize);
			buffer = NULL;
			free(buffer);

//			if(firstFrameIDetected){
//				sharedBuffer[0] = getFrameType(&frametypeManager);
//			}

			// The shared buffer has a new encoded frame. Lets callback java to sinalize it
			env->CallIntMethod( JavaSenderClass, JavaOnReadyFrame, (jint)(bufferSize+1), (jint)timestamp );
		}

		env->ReleaseByteArrayElements(javaBufferJNI, sharedBuffer, JNI_ABORT);
		env->DeleteGlobalRef(JavaSenderClass);
		sharedBuffer = NULL;
		free(sharedBuffer);
		javaBufferJNI = NULL;
		free(javaBufferJNI);
		JavaSenderClass = NULL;
		free(JavaSenderClass);

		encoding = false;
	}

	void enqueueFrame(uint8_t* data, int length) {
		NV21toYUV420P(&data);
	    if (queue_enqueue(decoded_video, (uint8_t*)data, length, Milliseconds().getTime(), NULL) != E_OK) {
	        Log("enqueueFrame() fail");
	        return;
	    }
//	    Log("enqueueFrame() done");
	}

	void setVideoParams(jint width, jint height, jint frameRate, jint bitRate, jint GOP, EncodeVideoParams ** paramsVideo){
		(*paramsVideo)->setCodec(COMMON_CODEC_VIDEO_FLV);

//		bitrate:
//		default:COMMON_VIDEO_DEFAULT_BITRATE
//		possible:
//		COMMON_VIDEO_DEFAULT_BITRATE          1400000                   ///< Default (bit/s)
//		iva docs: any value between 100000 and 4000000
		(*paramsVideo)->setBitRate(bitRate);

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
		(*paramsVideo)->setGopSize(GOP);
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
};

#endif
