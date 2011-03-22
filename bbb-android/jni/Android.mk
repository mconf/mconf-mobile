#this directory variable declaration
LOCAL_PATH := $(call my-dir)

#mconfnative module START
include $(CLEAR_VARS)

LOCAL_MODULE    := mconfnative

LOCAL_SRC_FILES := mconfnative/mconf.cpp \
				   mconfnative/opengl/opengl.cpp
				  
LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/decode
									   
LOCAL_LDLIBS += -llog \
				-lGLESv1_CM \
				$(LOCAL_PATH)/../libsFFMPEG/libavcodec.so \
				$(LOCAL_PATH)/../libsFFMPEG/libavformat.so \
				$(LOCAL_PATH)/../libsFFMPEG/libavutil.so \
				$(LOCAL_PATH)/../libsFFMPEG/libswscale.so \
				$(LOCAL_PATH)/../libsIVA/libthread.so \
				$(LOCAL_PATH)/../libsIVA/libcommon.so \
				$(LOCAL_PATH)/../libsIVA/libqueue.so \
				$(LOCAL_PATH)/../libsIVA/libdecode.so
							   
include $(BUILD_SHARED_LIBRARY)
#mconfnative module END