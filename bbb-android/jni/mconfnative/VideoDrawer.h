#ifndef _VIDEO_DRAWER_H_
#define _VIDEO_DRAWER_H_

//#include <jni.h>

#include "mconfnative/opengl/opengl.h"
#include "iva/decode/DecodeVideo.h"

extern "C" {

class VideoDrawer : protected Runnable {

private:
	DecodeVideo* video_dec;
	queue_t* encoded_video,
		* decoded_video;

	int screenWidth, screenHeight;
	bool stopThread;

//	jni stuff
//	jbyte *javaData;
//	JNIEnv* javaEnv;
//	jobject javaRenderer;
//	jclass javaRendererClass;
//	jmethodID javaSwapBuffers;

public:
//	VideoDrawer(JNIEnv* env, jobject obj, int screenWidth, int screenHeight) {
	VideoDrawer(int screenWidth, int screenHeight) {
		Log("VideoDrawer() begin");

//		javaEnv = env;
//		javaRenderer = obj;
//		javaRendererClass = javaEnv->GetObjectClass(javaRenderer);
//		javaSwapBuffers = javaEnv->GetMethodID(javaRendererClass, "swapBuffers", "()I");

		this->screenWidth = screenWidth;
		this->screenHeight = screenHeight;

		encoded_video = queue_create();
		decoded_video = queue_create();

		video_dec = new DecodeVideo();
		int ret = video_dec->open(COMMON_CODEC_VIDEO_FLV);
		if (ret != E_OK)
			Log("Error on opening the codec");

		Log("VideoDrawer() end");
	}

	~VideoDrawer() {
		Log("~VideoDrawer() begin");

		stop();

		queue_destroy(&encoded_video);
		queue_destroy(&decoded_video);

		delete video_dec;

		Log("~VideoDrawer() end");
	}

	void start() {
		Log("start() begin");

		if (isRunning())
			return;
		stopThread = false;
		run(true);

		Log("start() end");
	}

	void stop() {
		Log("stop() begin");

		if (!isRunning())
			return;
		stopThread = true;
		queue_broadcast(encoded_video);
		join();

		Log("stop() end");
	}

	void enqueueFrame(uint8_t* data, int length) {
	    if (queue_enqueue(encoded_video, (uint8_t*)&data[1], length-1, Milliseconds().getTime(), NULL) != E_OK) {
	        Log("enqueueFrame() fail");
	        return;
	    }
	    Log("enqueueFrame() done");
	}

protected:

	void threadFunction() {
		Log("threadFunction() begin");

		queue_consumer_t* consumer = queue_registerConsumer(decoded_video);
		if (consumer)
			Log("threadFunction() consumer registered");
		int ret = video_dec->start(encoded_video, decoded_video);
		if (ret != E_OK)
			return;
		Log("threadFunction() decode started");

		uint32_t timestamp, bufferSize;
		uint8_t* buffer;
		QueueExtraData* extraData;
		bool firstFrame = true;
		while (!stopThread) {
			Log("threadFunction() trying to dequeue");
			if (queue_dequeueCond(consumer, &buffer, &bufferSize, &timestamp, &extraData) != E_OK)
				continue;
			Log("threadFunction() dequeued");

			QueueExtraDataVideo* extraDataVideo;
			extraDataVideo = (QueueExtraDataVideo*) extraData;
			LogData log;
			int w = extraDataVideo->getWidth();
			int h = extraDataVideo->getHeight();
			log << "video resolution = " << w << " " << h << endl;
			log.push();

			if (firstFrame) {
				Log("threadFunction() firstFrame");
				initGL(w, h);
				Log("threadFunction() gl initialized");

				int tmpW = next_power_of_2(screenWidth);
				int tmpH = next_power_of_2(screenHeight);
				uint32_t tmpSize = avpicture_get_size(PIX_FMT_RGB565LE, tmpW, tmpH);
				uint8_t* tmpBuffer = (uint8_t*) malloc(tmpSize);
				memset(tmpBuffer, '\0', tmpSize);
				Log("threadFunction() applying first texture");
				apply_first_texture(tmpBuffer, tmpW, tmpH);
				Log("threadFunction() first texture applied");
				free(tmpBuffer);
				Log("threadFunction() buffer released");

				firstFrame = false;
			}

			Log("threadFunction() updating frame begin");
			update_frame(buffer, w, h, (screenWidth - w) / 2, (screenHeight - h) / 2);
			Log("threadFunction() updating frame end");
//			javaEnv->CallIntMethod( javaRenderer, javaSwapBuffers );

			queue_free(consumer);
			Log("threadFunction() consumer free");
		}

		video_dec->stop();
		queue_unregisterConsumer(&consumer);

		Log("threadFunction() end");
	}
};

}

#endif
