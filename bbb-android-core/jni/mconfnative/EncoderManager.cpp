#include "EncoderManager.h"
#include "VideoEncoder.h"

VideoEncoder* videoEncoder = NULL;

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_android_core_video_VideoPublish_initEncoder(JNIEnv *env, jobject obj, jint width, jint height, jint frameRate, jint bitRate, jint GOP) {
	if (!videoEncoder) {
		videoEncoder = new VideoEncoder(env, obj, width, height, frameRate, bitRate, GOP);
	}
	return 0;
}

jint Java_org_mconf_android_core_video_VideoCapture_enqueueFrame(JNIEnv *env, jobject obj, jbyteArray data, jint length, jint width, jint height, jint rotation) {
	if (videoEncoder) {
		jbyte *javaData = env->GetByteArrayElements(data, 0);
		videoEncoder->enqueueFrame((uint8_t*) javaData, length, width, height, rotation);
		env->ReleaseByteArrayElements(data, javaData, JNI_ABORT);
	}
	return 0;
}

jint Java_org_mconf_android_core_video_VideoPublish_endEncoder(JNIEnv *env, jobject obj) {
	if (videoEncoder) {
		delete videoEncoder;
		videoEncoder = NULL;
	}
	return 0;
}

jint Java_org_mconf_android_core_video_VideoPublish_initSenderLoop(JNIEnv *env, jobject obj){
	if (videoEncoder) {
		videoEncoder->senderLoop(env, obj);
	}
}

#ifdef __cplusplus
}
#endif
