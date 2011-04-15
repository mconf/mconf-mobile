#ifndef _ENCODER_MANAGER_H_
#define _ENCODER_MANAGER_H_

#include <jni.h>

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_bbb_android_video_VideoCapture_initEncoder(JNIEnv *env, jobject obj);
jint Java_org_mconf_bbb_android_video_VideoCapture_enqueueFrame(JNIEnv *env, jobject obj, jbyteArray data, jint length);
jint Java_org_mconf_bbb_android_video_VideoCapture_endEncoder(JNIEnv *env, jobject obj);

#ifdef __cplusplus
}
#endif

#endif
