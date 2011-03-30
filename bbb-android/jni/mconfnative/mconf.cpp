#include <android/log.h>
#include <jni.h>

#include "mconfnative/opengl/opengl.h"

#include "iva/decode/DecodeVideo.h"

#ifdef __cplusplus
extern "C"{
#endif

#define DEBUG 1

/* flag to close this native thread */
bool flagReceive = true;

DecodeVideo *video_dec;
queue_t *frames_video; //encoded frames queue
queue_t *frames; //decoded frames queue
QueueExtraData * extraData;
uint32_t timestamp, outbufSize;
uint8_t * outbuffer;
queue_consumer_t *_consumer;
QueueExtraDataVideo * extraDataIn;

jbyte *DataC;
static JNIEnv* JavaEnv = NULL;
static jobject JavaRenderer = NULL;
static jclass JavaRendererClass = NULL;
static jmethodID JavaSwapBuffers = NULL;
int w, h, dw, dh, wmax=0, hmax=0;

jint Java_org_mconf_bbb_android_Renderermconf_nativeVideoInitJavaCallbacks(JNIEnv *jniEnv, jobject thiz){
	/* inicializa callbacks */
	JavaEnv = jniEnv;
	JavaRenderer = thiz;
	JavaRendererClass = JavaEnv->GetObjectClass(thiz);
	JavaSwapBuffers = JavaEnv->GetMethodID(JavaRendererClass, "swapBuffers", "()I");

	frames = queue_create(); //queue that will receive the decoded frames from ffmpeg
	frames_video = queue_create(); //queue that will receive the encoded frames from bbb

	video_dec = new DecodeVideo();
	int ret = video_dec->open(COMMON_CODEC_VIDEO_H263);
	if (ret != E_OK) {
		__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "error on opening the codec, error %d\n", ret);
	}

	return 1;
}


jint Java_org_mconf_bbb_android_Renderermconf_nativeRender(JNIEnv *env, jobject obj,jint width,jint height){
	/* inicializa variaveis de benchmark */
	if(DEBUG){
		__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "Screen size: w = %d h = %d \n",width,height);
	}
	flagReceive = true;

	wmax = width;
	hmax = height;

	_consumer = queue_registerConsumer(frames);

	video_dec->start(frames_video, frames);
	if(DEBUG){
		__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "Decode started!\n");
	}
	/* found_frame:
	 * 0: no decoded frame was found yet
	 * 1: a decoded video frame was already found and the video rendering was already initialized
	 */
	int found_frame = 0;

	int wp2, hp2, Size;
	uint8_t *aux;

	w=0;h=0,dw=0,dh=0;

	/* first loop (rendering not initialized yet):
	 * wait until the first decoded frame is found,
	 * initializes the rendering
	 * and exits the loop */
	while(flagReceive){
		if (queue_dequeue(_consumer, &aux, &outbufSize, &timestamp, &extraData) != E_OK) {
			continue;
		}
		//first decoded video frame was found
		if(DEBUG){
			__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "first decoded video frame found\n");
		}
		QueueExtraDataVideo * extraDataVideo;
		extraDataVideo = (QueueExtraDataVideo *)extraData;
		w = extraDataVideo->getWidth();
		h = extraDataVideo->getHeight();
		dw = (wmax - w)/2;
		dh = (hmax - h)/2;
		if(DEBUG){
			__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "wmax = %d, hmax = %d\n",wmax,hmax);
		}
		wp2 = next_power_of_2(w);
		hp2 = next_power_of_2(h);
		if(DEBUG){
			__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "w = %d, h = %d, wp2 = %d, hp2 = %d\n", w, h, wp2, hp2);
		}
		initGL(w,h);
		Size = avpicture_get_size(PIX_FMT_RGB565LE, wp2, hp2);
		outbuffer = (uint8_t *) malloc(Size);
		memset(outbuffer, '\0', Size);
		aplly_first_texture(outbuffer, wp2, hp2);
		free(outbuffer);
		found_frame = 1;

		queue_free(_consumer);
		aux = NULL;
		free(aux);
		break;
	}


	/* second (and last) loop
	 * a decoded video frame was already found
	 * and the rendering variables and functions were already initialized
	 * so lets start rendering the frames
	 */
	while(flagReceive){
		if (queue_dequeue(_consumer, &aux, &outbufSize, &timestamp, &extraData) != E_OK) {
			continue;
		}
		update_frame(aux, w, h, dw, dh);
		JavaEnv->CallIntMethod( JavaRenderer, JavaSwapBuffers );

		queue_free(_consumer);
		aux = NULL;
		free(aux);
	}

	video_dec->close();
	if(DEBUG){
		__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "Video decoding stoped\n");
	}
	//this native thread will close now
	return 1;
}

void Java_org_mconf_bbb_android_ShowVideo_enqueueEncoded(JNIEnv *env,jobject obj,jbyteArray Data, jint length)
{
	DataC = env->GetByteArrayElements(Data, 0);

	Milliseconds ts;
	ts.setTimestamp();
	uint32_t time = ts.getTime();

	if(queue_length(frames_video) < 5){
		if(queue_enqueue(frames_video, (uint8_t*)&DataC[1], length-1, time, extraDataIn) != E_OK){
			__android_log_print(ANDROID_LOG_DEBUG, "mconf.cpp","error on enqueue. Timestamp = %d", time);
		}
	}

	env->ReleaseByteArrayElements(Data, DataC, JNI_ABORT);
}

void Java_org_mconf_bbb_android_ShowVideo_stopThreads(JNIEnv *env,jobject obj)
{
	if(DEBUG==1){
		__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "Stoping native code\n");
	}

	flagReceive = false;
}

void Java_org_mconf_bbb_android_ShowVideo_changeOrientation(JNIEnv *env,jobject obj,jint width,jint height)
{
	wmax=width;
	hmax=height;

	if(w>0){
		if(width>w){
			dw = (width - w)/2;
		}
		else{
			dw = 0;
		}
		if(height>h){
			dh = (height - h)/2;
		}
		else{
			dh = 0;
		}
	}
	if(DEBUG==1){
		__android_log_print(ANDROID_LOG_DEBUG,  "mconf.cpp", "wmax=%d, hmax=%d --> w=%d dw=%d h=%d dh=%d\n",wmax,hmax,w,dw,h,dh);
	}
}

#ifdef __cplusplus
}
#endif
