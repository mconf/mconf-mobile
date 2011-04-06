#include "DrawerManager.h"
#include "VideoDrawer.h"

VideoDrawer* videoDrawer = NULL;

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_bbb_android_VideoSurfaceView_initDrawer(JNIEnv *env, jobject obj, jint width, jint height) {
//	videoDrawer = new VideoDrawer(env, obj, width, height);
	videoDrawer = new VideoDrawer(width, height);
}

jint Java_org_mconf_bbb_android_VideoSurfaceView_startDrawer(JNIEnv *env, jobject obj) {
	if (videoDrawer)
		videoDrawer->start();
}

jint Java_org_mconf_bbb_android_VideoSurfaceView_stopDrawer(JNIEnv *env, jobject obj) {
	if (videoDrawer)
		videoDrawer->stop();
}

jint Java_org_mconf_bbb_android_VideoSurfaceView_enqueueFrame(JNIEnv *env, jobject obj, jbyteArray data, jint length) {
	if (videoDrawer) {
		jbyte *javaData = env->GetByteArrayElements(data, 0);
		videoDrawer->enqueueFrame((uint8_t*) javaData, length);
//		env->ReleaseByteArrayElements(data, javaData, JNI_ABORT);
	}
}

jint Java_org_mconf_bbb_android_VideoSurfaceView_endDrawer(JNIEnv *env, jobject obj) {
	if (videoDrawer) {
		videoDrawer->stop();
		delete videoDrawer;
	}
}

#ifdef __cplusplus
}
#endif
