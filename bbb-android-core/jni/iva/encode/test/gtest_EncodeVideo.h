#ifndef _GTEST_ENCODE_VIDEO_H_
#define _GTEST_ENCODE_VIDEO_H_

#include <common.h>
#include <gtest/gtest.h>
#include <EncodeVideo.h>

class EncodeVideoTest : public ::testing::Test
{
protected:
    static const int FRAME_TEST_SIZE = 518400;
    static const int TIMESTAMP = 10;

    EncodeVideo * _encode;
    queue_t * _queue;
    queue_consumer_t * _consumer;
    AVFrame * _frame;
    unsigned char * _buffer;
    EncodeVideoParams _params;

    void _CreateFrame();
    void _2FramesEncoding();
    void _AutomaticEncoding();

    virtual void SetUp()
    {
        common_init();
        _encode = new EncodeVideo();
        _queue = queue_create();
        _consumer = queue_registerConsumer(_queue);
        _frame = avcodec_alloc_frame();
        _buffer = NULL;

        _params.setCodec(COMMON_VIDEO_DEFAULT_CODEC);
        _params.setPixelsFormat(IvaPixFmt(IvaPixFmt::FMT_YUV420P));
        _params.setBitRate(COMMON_VIDEO_DEFAULT_BITRATE);
        _params.setFrameRate(COMMON_VIDEO_DEFAULT_FPS);
        _params.setWidth(COMMON_VIDEO_DEFAULT_WIDTH);
        _params.setHeight(COMMON_VIDEO_DEFAULT_HEIGHT);
        _params.setGopSize(COMMON_VIDEO_DEFAULT_GOP);
        //_params.setPresetFile("");
    }

    virtual void TearDown()
    {
        delete _encode;
        queue_unregisterConsumer(&_consumer);
        queue_destroy(&_queue);
        if (_frame) {
            av_free(_frame);
        }
        if (_buffer) {
            free(_buffer);
        }
        common_end();
    }

};

#endif
