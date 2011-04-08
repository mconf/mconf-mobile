#include "DrawerManager.h"
#include "VideoDrawer.h"

VideoDrawer* videoDrawer = NULL;

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_bbb_android_video_VideoSurface_initDrawer(JNIEnv *env, jobject obj, jint screenW, jint screenH, jint displayAreaW, jint displayAreaH) {
	if (!videoDrawer) {
		videoDrawer = new VideoDrawer(screenW, screenH);
		videoDrawer->setDisplayAreaW(displayAreaW);
		videoDrawer->setDisplayAreaH(displayAreaH);
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
	if (videoDrawer)
		videoDrawer->renderFrame();
	return 0;
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
