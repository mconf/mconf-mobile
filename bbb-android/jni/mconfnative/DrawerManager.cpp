#include "DrawerManager.h"
#include "VideoDrawer.h"

VideoDrawer* videoDrawer = NULL;
Mutex* mutex = new Mutex();

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_bbb_android_video_VideoSurface_initDrawer(JNIEnv *env, jobject obj, jint screenW, jint screenH, jint displayAreaW, jint displayAreaH, jint displayPositionX, jint displayPositionY) {
	if (!videoDrawer) {
		videoDrawer = new VideoDrawer(screenW, screenH);
		videoDrawer->setDisplayAreaW(displayAreaW);
		videoDrawer->setDisplayAreaH(displayAreaH);
		videoDrawer->setDisplayPositionX(displayPositionX);
		videoDrawer->setDisplayPositionY(displayPositionY);
	}
	return 0;
}

jint Java_org_mconf_bbb_android_video_VideoSurface_enqueueFrame(JNIEnv *env, jobject obj, jbyteArray data, jint length) {
	if (videoDrawer) {
		jbyte *javaData = env->GetByteArrayElements(data, 0);
		videoDrawer->enqueueFrame((uint8_t*) javaData, length);
		env->ReleaseByteArrayElements(data, javaData, JNI_ABORT);
	}
	return 0;
}

jint Java_org_mconf_bbb_android_video_VideoRenderer_nativeRender(JNIEnv *env, jobject obj) {
	int ret = 0;
	mutex->lock();
	if (videoDrawer){
		ret = videoDrawer->renderFrame();
	}
	mutex->unlock();
	return ret;
}

jint Java_org_mconf_bbb_android_video_VideoRenderer_nativeResize(JNIEnv *env, jobject obj, jint screenW, jint screenH, jint displayAreaW, jint displayAreaH, jint displayPositionX, jint displayPositionY){
	mutex->lock();
	if (videoDrawer) {
		videoDrawer->setDisplayAreaW(displayAreaW);
		videoDrawer->setDisplayAreaH(displayAreaH);
		videoDrawer->setDisplayPositionX(displayPositionX);
		videoDrawer->setDisplayPositionY(displayPositionY);
		videoDrawer->setScreenW(screenW);
		videoDrawer->setScreenH(screenH);
		videoDrawer->setFirstFrameFlag(true);
	}
	mutex->unlock();
	return 0;
}

jint Java_org_mconf_bbb_android_video_VideoRenderer_getVideoWidth(JNIEnv *env, jobject obj) {
	if (videoDrawer)
		return videoDrawer->getVideoW();
	else
		return 1;
}

jint Java_org_mconf_bbb_android_video_VideoRenderer_getVideoHeight(JNIEnv *env, jobject obj) {
	if (videoDrawer)
		return videoDrawer->getVideoH();
	else
		return 1;
}

jint Java_org_mconf_bbb_android_video_VideoSurface_endDrawer(JNIEnv *env, jobject obj) {
	if (videoDrawer) {
		delete videoDrawer;
		videoDrawer = NULL;
	}
	return 0;
}

#ifdef __cplusplus
}
#endif
