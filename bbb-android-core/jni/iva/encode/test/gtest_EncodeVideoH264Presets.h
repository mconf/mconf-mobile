#ifndef _GTEST_ENCODE_VIDEO_H264_PRESETS_H_
#define _GTEST_ENCODE_VIDEO_H264_PRESETS_H_

#include <gtest/gtest.h>
#include <common.h>
#include <EncodeVideoH264Presets.h>

class EncodeVideoH264PresetsTest : public ::testing::Test
{
protected:
    EncodeVideoH264Presets * _presets;
    AVCodecContext * _codecCtx;
    string _presetFile;

    virtual void SetUp()
    {
        common_init();
        _presets = new EncodeVideoH264Presets();
        _codecCtx = avcodec_alloc_context();
        _presetFile = "../res/libx264-test.ffpreset";
    }

    virtual void TearDown()
    {
        avcodec_close(_codecCtx);
        delete _presets;
        common_end();
    }

};

#endif
