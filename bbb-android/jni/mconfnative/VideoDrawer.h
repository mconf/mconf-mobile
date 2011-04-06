#ifndef _VIDEO_DRAWER_H_
#define _VIDEO_DRAWER_H_

//#include <jni.h>

#include "mconfnative/opengl/opengl.h"
#include "iva/decode/DecodeVideo.h"

extern "C" {

class VideoDrawer {

private:
	DecodeVideo* video_dec;
	queue_t* encoded_video,
		* decoded_video;
	queue_consumer_t* consumer;

	int screenWidth, screenHeight;
	bool firstFrame, stopThread;

public:
	VideoDrawer(int screenWidth, int screenHeight) {
		Log("VideoDrawer() begin");

		this->screenWidth = screenWidth;
		this->screenHeight = screenHeight;

		firstFrame = true;
		stopThread = false;

		encoded_video = queue_create();
		decoded_video = queue_create();

		video_dec = new DecodeVideo();
		int ret = video_dec->open(COMMON_CODEC_VIDEO_FLV);
		if (ret != E_OK)
			Log("Error on opening the codec");

		consumer = queue_registerConsumer(decoded_video);
		if (consumer)
			Log("threadFunction() consumer registered");
		ret = video_dec->start(encoded_video, decoded_video);
		if (ret != E_OK)
			return;
		Log("threadFunction() decode started");

		Log("VideoDrawer() end");
	}

	~VideoDrawer() {
		Log("~VideoDrawer() begin");

		// \todo syncronize the finalize of the renderer to solve the end problem!
		Seconds(1).sleep();

		stopThread = true;
		queue_broadcast(decoded_video);

		video_dec->stop();
		queue_unregisterConsumer(&consumer);

		queue_destroy(&encoded_video);
		queue_destroy(&decoded_video);

		delete video_dec;

		Log("~VideoDrawer() end");
	}

	void enqueueFrame(uint8_t* data, int length) {
	    if (queue_enqueue(encoded_video, (uint8_t*)&data[1], length-1, Milliseconds().getTime(), NULL) != E_OK) {
	        Log("enqueueFrame() fail");
	        return;
	    }
	    Log("enqueueFrame() done");
	}

	void renderFrame() {
		Log("threadFunction() begin");

		uint32_t timestamp, bufferSize;
		uint8_t* buffer;
		QueueExtraData* extraData;

		Log("threadFunction() trying to dequeue");
		while (!stopThread) {
			if (queue_dequeueCond(consumer, &buffer, &bufferSize, &timestamp, &extraData) == E_OK) {
				break;
			}
		}
		if (stopThread) {
			Log("renderFrame() returning");
			return;
		}

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

		queue_free(consumer);
		Log("threadFunction() consumer free");

		Log("threadFunction() end");
	}
};

}

#endif
