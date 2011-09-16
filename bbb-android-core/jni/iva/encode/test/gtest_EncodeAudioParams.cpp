#include "gtest_EncodeAudioParams.h"

TEST_F(EncodeAudioParamsTest, SettersAndGetters)
{
    for (int i = -999; i < 9999; i += 3) {
        _params->setBitRate(i);
        ASSERT_EQ(_params->getBitRate(), i);
        _params->setChannels(i);
        ASSERT_EQ(_params->getChannels(), i);
        _params->setSampleRate(i);
        ASSERT_EQ(_params->getSampleRate(), i);
    }

    _params->setCodec(COMMON_CODEC_AUDIO_AAC);
    ASSERT_EQ(_params->getCodec(), COMMON_CODEC_AUDIO_AAC);
    _params->setCodec(COMMON_CODEC_AUDIO_MP2);
    ASSERT_EQ(_params->getCodec(), COMMON_CODEC_AUDIO_MP2);
}


