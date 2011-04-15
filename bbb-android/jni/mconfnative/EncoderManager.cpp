#include "EncoderManager.h"
#include "VideoEncoder.h"

VideoEncoder* videoEncoder = NULL;

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_bbb_android_video_VideoCapture_initEncoder(JNIEnv *env, jobject obj) {
	if (!videoEncoder) {
		videoEncoder = new VideoEncoder(env, obj);
	}
	return 0;
}

jint Java_org_mconf_bbb_android_video_VideoCapture_enqueueFrame(JNIEnv *env, jobject obj, jbyteArray data, jint length) {
	if (videoEncoder) {
		jbyte *javaData = env->GetByteArrayElements(data, 0);
		videoEncoder->enqueueFrame((uint8_t*) javaData, length);
		env->ReleaseByteArrayElements(data, javaData, JNI_ABORT);
	}
	return 0;
}

jint Java_org_mconf_bbb_android_video_VideoCapture_endEncoder(JNIEnv *env, jobject obj) {
	if (videoEncoder) {
		delete videoEncoder;
		videoEncoder = NULL;
	}
	return 0;
}

#ifdef __cplusplus
}
#endif
