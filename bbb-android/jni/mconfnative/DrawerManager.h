#ifndef _DRAWER_MANAGER_H_
#define _DRAWER_MANAGER_H_

#include <jni.h>

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_bbb_android_video_VideoSurface_initDrawer(JNIEnv *env, jobject obj, jint width, jint height);
jint Java_org_mconf_bbb_android_video_VideoSurface_enqueueFrame(JNIEnv *env, jobject obj, jbyteArray data, jint length);
jint Java_org_mconf_bbb_android_video_VideoRenderer_nativeRender(JNIEnv *env, jobject obj);
jint Java_org_mconf_bbb_android_video_VideoSurface_endDrawer(JNIEnv *env, jobject obj);

#ifdef __cplusplus
}
#endif

#endif
