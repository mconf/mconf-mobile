#http://groups.google.com/group/sipdroid-users/browse_thread/thread/a55382ec4eca0ac7
APP_PROJECT_PATH := $(call my-dir)/..
APP_MODULES  := OSNetworkSystem
APP_MODULES	 += speex_jni
APP_MODULES	 += bv16_jni
APP_MODULES	 += silkcommon
#APP_MODULES	 += silk8_jni
#APP_MODULES	 += silk16_jni
#APP_MODULES	 += silk24_jni
APP_MODULES	 += g722_jni
APP_MODULES	 += gsm_jni

APP_OPTIM        := release 
APP_CFLAGS       += -O3
