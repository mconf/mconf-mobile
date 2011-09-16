#this directory variable declaration
LOCAL_PATH := $(call my-dir)

#mconfnativeshowvideo module START
include $(CLEAR_VARS)

LOCAL_MODULE    := mconfnativeshowvideo

LOCAL_SRC_FILES := \
				   mconfnative/DrawerManager.cpp \
				   mconfnative/VideoDrawer.cpp
				  
LOCAL_C_INCLUDES := \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/thread
					
LOCAL_SHARED_LIBRARIES := queue thread common decode avcodec
LOCAL_LDLIBS := -lGLESv1_CM
							   
include $(BUILD_SHARED_LIBRARY)
#mconfnativeshowvideo module END

#mconfnativeencodevideo module START
include $(CLEAR_VARS)

LOCAL_MODULE    := mconfnativeencodevideo

LOCAL_SRC_FILES := \
				   mconfnative/EncoderManager.cpp \
				   mconfnative/VideoEncoder.cpp
				  
LOCAL_C_INCLUDES := \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/thread
									   
LOCAL_SHARED_LIBRARIES := queue thread common encode
							   
include $(BUILD_SHARED_LIBRARY)
#mconfnativeencodevideo module END


#thread module
include $(CLEAR_VARS)

LOCAL_MODULE    := thread

LOCAL_CPPFLAGS	:= -std=gnu++0x

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common

LOCAL_SRC_FILES :=  \
                    iva/thread/ConditionVariable.cpp \
                    iva/thread/Interval.cpp \
                    iva/thread/Interval_linux.cpp \
                    iva/thread/Microseconds.cpp \
                    iva/thread/Milliseconds.cpp \
                    iva/thread/Mutex.cpp \
                    iva/thread/Seconds.cpp \
                    iva/thread/Thread.cpp \
                    iva/thread/Timer.cpp
			   
include $(BUILD_SHARED_LIBRARY)
#end of thread module

#common module
include $(CLEAR_VARS)

LOCAL_MODULE    := common

LOCAL_SHARED_LIBRARIES := thread avcodec avutil
 
LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES :=  \
                    iva/common/AVConfigs.cpp \
                    iva/common/CommonColor.cpp \
                    iva/common/CommonRect.cpp \
                    iva/common/Directory.cpp \
                    iva/common/Directory_linux.cpp \
                    iva/common/ErrorController.cpp \
                    iva/common/ErrorData.cpp \
                    iva/common/ErrorStack.cpp \
                    iva/common/ErrorVector.cpp \
                    iva/common/Folders.cpp \
                    iva/common/IPV4.cpp \
                    iva/common/IvaOutBuffer.cpp \
                    iva/common/IvaOutController.cpp \
                    iva/common/IvaOutLogFile.cpp \
                    iva/common/IvaOutStream.cpp \
                    iva/common/IvaPixFmt.cpp \
                    iva/common/IvaPixFmt_linux.cpp \
                    iva/common/IvaString.cpp \
                    iva/common/IvaTime.cpp \
                    iva/common/IvaVideoFrame.cpp \
                    iva/common/Location.cpp \
                    iva/common/LogData.cpp \
                    iva/common/SysInfo_linux.cpp

include $(BUILD_SHARED_LIBRARY)
#end of common module

#queue module
include $(CLEAR_VARS)

LOCAL_MODULE    := queue

LOCAL_SHARED_LIBRARIES := thread common

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue
					
LOCAL_SRC_FILES :=	iva/queue/queue.cpp \
					iva/queue/QueueBuffer.cpp \
					iva/queue/QueueDiscard.cpp \
					iva/queue/QueueDiscardPolicy.cpp \
					iva/queue/QueueExtraData.cpp \
					iva/queue/QueueExtraDataAudio.cpp \
					iva/queue/QueueExtraDataVideo.cpp  
								   
include $(BUILD_SHARED_LIBRARY)
#end of queue module

#decode module
include $(CLEAR_VARS)

LOCAL_MODULE    := decode

LOCAL_SHARED_LIBRARIES := thread common queue avcodec avutil swscale

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/decode

LOCAL_SRC_FILES :=	iva/decode/Decode.cpp \
					iva/decode/DecodeAudio.cpp \
					iva/decode/DecodeVideo.cpp	
												   
include $(BUILD_SHARED_LIBRARY)
#end of decode module

#encode module
include $(CLEAR_VARS)

LOCAL_MODULE    := encode

LOCAL_SHARED_LIBRARIES := thread common queue avcodec avformat avutil swscale

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/encode

LOCAL_SRC_FILES :=	iva/encode/Encode.cpp \
					iva/encode/EncodeAudio.cpp \
					iva/encode/EncodeAudioParams.cpp \
					iva/encode/EncodeVideo.cpp \
					iva/encode/EncodeVideoH264Opt.cpp \
					iva/encode/EncodeVideoH264Presets.cpp \
					iva/encode/EncodeVideoParams.cpp \
					iva/encode/VideoLoader.cpp	
												   
include $(BUILD_SHARED_LIBRARY)
#end of encode module
