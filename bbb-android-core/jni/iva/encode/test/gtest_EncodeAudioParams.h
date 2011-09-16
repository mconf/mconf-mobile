#ifndef _GTEST_ENCODE_AUDIO_PARAMS_H_
#define _GTEST_ENCODE_AUDIO_PARAMS_H_

#include <gtest/gtest.h>
#include <common.h>
#include <EncodeAudioParams.h>

class EncodeAudioParamsTest : public ::testing::Test
{
protected:
    EncodeAudioParams * _params;

    virtual void SetUp()
    {
        _params = new EncodeAudioParams();
    }

    virtual void TearDown()
    {
        delete _params;
    }

};

#endif
