#ifndef _GTEST_DECODE_AUDIO_H_
#define _GTEST_DECODE_AUDIO_H_

#include <common.h>
#include <gtest/gtest.h>
#include <DecodeAudio.h>
#include <string.h>
using namespace std;

class DecodeAudioTest : public ::testing::Test
{
protected:
    static const int FRAME_TEST_SIZE = 44608;
    static const int TIMESTAMP = 10;
    string FILE_MP2_PATH;
    int CODEC_BASE;

    DecodeAudio * _decode;
    queue_t * _queue;
    queue_consumer_t * _consumer;
    uint8_t * _frame;

    void _FreeQueueElement();
    void _TestDecoding(string filename, int codecId);
    void _TestAutomaticDecoding(string filename, int codecId);

    virtual void SetUp()
    {
        FILE_MP2_PATH = "..\\..\\..\\..\\deps\\test\\decode\\final.mp2";
        CODEC_BASE = COMMON_CODEC_AUDIO_MP2;

        common_init();
        _decode = new DecodeAudio();
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
