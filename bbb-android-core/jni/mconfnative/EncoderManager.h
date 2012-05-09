#ifndef _ENCODER_MANAGER_H_
#define _ENCODER_MANAGER_H_

#include <jni.h>

#ifdef __cplusplus
extern "C"{
#endif

jint Java_org_mconf_android_core_video_VideoPublish_initEncoder(JNIEnv *env, jobject obj, jint width, jint height, jint frameRate, jint bitRate, jint GOP);
jint Java_org_mconf_android_core_video_VideoCapture_enqueueFrame(JNIEnv *env, jobject obj, jbyteArray data, jint length, jint width, jint height, jint rotation);
jint Java_org_mconf_android_core_video_VideoPublish_endEncoder(JNIEnv *env, jobject obj);
jint Java_org_mconf_android_core_video_VideoPublish_initSenderLoop(JNIEnv *env, jobject obj);

#ifdef __cplusplus
}
#endif

#endif
