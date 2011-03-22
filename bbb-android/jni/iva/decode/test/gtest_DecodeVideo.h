#ifndef _GTEST_DECODE_VIDEO_H_
#define _GTEST_DECODE_VIDEO_H_

#include <common.h>
#include <gtest/gtest.h>
#include <DecodeVideo.h>
#include <string.h>
using namespace std;

class DecodeVideoTest : public ::testing::Test
{
protected:
    static const int FRAME_TEST_SIZE = 44608;
    static const int TIMESTAMP = 10;
    string FILE_MPEG4_PATH;
    string FILE_H264_PATH;
    int CODEC_BASE;

    DecodeVideo * _decode;
    queue_t * _queue;
    queue_consumer_t * _consumer;
    uint8_t * _frame;

    void _FreeQueueElement();
    void _TestDecoding(string filename, int codecId);
    void _TestAutomaticDecoding(string filename, int codecId);

    virtual void SetUp()
    {
        FILE_MPEG4_PATH = "..\\..\\..\\..\\deps\\test\\decode\\web.m4v";
        FILE_H264_PATH = "..\\..\\..\\..\\deps\\test\\decode\\web.h264";
        CODEC_BASE = COMMON_CODEC_VIDEO_MPEG4;

        common_init();
        _decode = new DecodeVideo();
        _queue = queue_create();
        _consumer = queue_registerConsumer(_queue);
        _frame = NULL;
    }

    virtual void TearDown()
    {
        delete _decode;
        queue_unregisterConsumer(&_consumer);
        queue_destroy(&_queue);
        if (_frame) {
            free(_frame);
        }
        common_end();
    }

};

#endif
