#ifndef _GTEST_ENCODE_AUDIO_H_
#define _GTEST_ENCODE_AUDIO_H_

#include <common.h>
#include <gtest/gtest.h>
#include <EncodeAudio.h>

class EncodeAudioTest : public ::testing::Test
{
protected:
    static const int FRAME_TEST_SIZE = 4608;
    static const int TIMESTAMP = 10;

    EncodeAudio * _encode;
    queue_t * _queue;
    queue_consumer_t * _consumer;
    unsigned char * _frame;
    EncodeAudioParams _params;

    virtual void SetUp()
    {
        common_init();
        _encode = new EncodeAudio();
        _queue = queue_create();
        _consumer = queue_registerConsumer(_queue);
        _frame = NULL;

        _params.setBitRate(COMMON_AUDIO_DEFAULT_BITRATE);
        _params.setChannels(COMMON_AUDIO_DEFAULT_CHANNELS);
        _params.setCodec(COMMON_CODEC_AUDIO_MP2);
        _params.setSampleRate(COMMON_AUDIO_DEFAULT_FREQUENCY);
    }

    virtual void TearDown()
    {
        delete _encode;
        queue_unregisterConsumer(&_consumer);
        queue_destroy(&_queue);
        if (_frame) {
            free(_frame);
        }
        common_end();
    }

};

#endif
