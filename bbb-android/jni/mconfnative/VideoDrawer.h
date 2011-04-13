#ifndef _VIDEO_DRAWER_H_
#define _VIDEO_DRAWER_H_

#include <GLES/gl.h>
#include <GLES/glext.h>

#include "iva/decode/DecodeVideo.h"

#define CHECK_GL_ERROR() { GLint e = glGetError(); if (e != GL_NO_ERROR) { LogData log; log << "opengl error " << e << " at VideoDrawer.h:" << __LINE__; log.push(); } }

class VideoDrawer {

private:
	DecodeVideo* video_dec;
	queue_t* encoded_video,
		* decoded_video;
	queue_consumer_t* consumer;

	int screenW, screenH,
		videoW, videoH,
		displayAreaW, displayAreaH,
		displayPositionX, displayPositionY;
	bool firstFrame, stopThread;

public:
	VideoDrawer(int screenWidth, int screenHeight) {
		Log("VideoDrawer() begin");

		screenW = screenWidth;
		screenH = screenHeight;
		displayPositionX = 0;
		displayPositionY = 0;

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
//	    Log("enqueueFrame() done");
	}

	int renderFrame() {
//		Log("threadFunction() begin");

		uint32_t timestamp, bufferSize;
		uint8_t* buffer;
		QueueExtraData* extraData;

//		Log("threadFunction() trying to dequeue");
		while (!stopThread) {
			if (queue_dequeueCond(consumer, &buffer, &bufferSize, &timestamp, &extraData) == E_OK) {
				break;
			}
		}
		if (stopThread) {
//			Log("renderFrame() returning");
			return 0;
		}

//		Log("threadFunction() dequeued");

		QueueExtraDataVideo* extraDataVideo;
		extraDataVideo = (QueueExtraDataVideo*) extraData;
//		LogData log;
		videoW = extraDataVideo->getWidth();
		videoH = extraDataVideo->getHeight();
//		log << "video resolution = " << w << " " << h << endl;
//		log.push();

		int ret = 0;

		if (firstFrame) {
//			Log("threadFunction() firstFrame");
//			Log("threadFunction() gl initialized");
			createTexture(videoW, videoH);

			firstFrame = false;

			if (videoW * displayAreaH != videoH * displayAreaW) {
				float videoAspect = videoW / (float) videoH;
				float displayAspect = displayAreaW / (float) displayAreaH;
				if (videoAspect < displayAspect)
					displayAreaW = displayAreaH * videoAspect;
				else
					displayAreaH = displayAreaW / (float) videoAspect;

				ret = 1;
			}
		}

//		Log("threadFunction() updating frame begin");

		updateFrame(buffer, videoW, videoH);

//		Log("threadFunction() updating frame end");

		queue_free(consumer);
//		Log("threadFunction() consumer free");

//		Log("threadFunction() end");

		return ret;
	}
	
	static uint32_t next_power_of_two(uint32_t k) {
		if (k == 0)
				return 1;
		k--;
		for (int i=1; i < 32; i<<=1)
				k = k | k >> i;
		return k+1;
	}

	void createTexture(int width, int height) {
		LogData log;
		log << "GL_VENDOR = " <<  glGetString(GL_VENDOR) << endl;
		log << "GL_RENDERER = " << glGetString(GL_RENDERER) << endl;
		log << "GL_VERSION = " << glGetString(GL_VERSION) << endl;
		log << "GL_EXTENSIONS = " << glGetString(GL_EXTENSIONS);
		log.push();

		GLint crop[4] = { 0, height, width, -height };
		glBindTexture(GL_TEXTURE_2D, 0); CHECK_GL_ERROR();
		glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, crop); CHECK_GL_ERROR();
		glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); CHECK_GL_ERROR();
		glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); CHECK_GL_ERROR();
		glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); CHECK_GL_ERROR();
		glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); CHECK_GL_ERROR();
		glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE); CHECK_GL_ERROR();
		glEnable(GL_TEXTURE_2D); CHECK_GL_ERROR();
		glColor4f(1,1,1,1); CHECK_GL_ERROR();

		int tmpW = next_power_of_two(width);
		int tmpH = next_power_of_two(height);
		uint32_t tmpSize = avpicture_get_size(PIX_FMT_RGB565LE, tmpW, tmpH);
		uint8_t* tmpBuffer = (uint8_t*) malloc(tmpSize);
		memset(tmpBuffer, '\0', tmpSize);
//		Log("threadFunction() applying first texture");
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, tmpW, tmpH, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, tmpBuffer); CHECK_GL_ERROR();
//		Log("threadFunction() first texture applied");
		free(tmpBuffer);
//		Log("threadFunction() buffer released");	

		log.clear();
		log << "video resolution: " << videoW << "x" << videoH << endl;
		log << "first texture resolution: " << tmpW << "x" << tmpH << endl;
		log << "first texture buffer size: " << tmpSize << endl;
		log.push();
	}

	void updateFrame(uint8_t* buffer, int width, int height) {
		glClear(GL_COLOR_BUFFER_BIT); CHECK_GL_ERROR();
		glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, buffer); CHECK_GL_ERROR();
		glDrawTexiOES(displayPositionX, displayPositionY, 0, displayAreaW, displayAreaH); CHECK_GL_ERROR();
	}
	
	int getVideoW() { return videoW; }
	int getVideoH() { return videoH; }
	float getAspectRatio() { return videoW / (float) videoH; }
	void setDisplayAreaW(int w) { displayAreaW = w; }
	void setDisplayAreaH(int h) { displayAreaH = h; }
	void setDisplayPositionX(int x) { displayPositionX = x; }
	void setDisplayPositionY(int y) { displayPositionY = y; }
	void setScreenW(int w) { screenW = w; }
	void setScreenH(int h) { screenH = h; }
};

#endif
