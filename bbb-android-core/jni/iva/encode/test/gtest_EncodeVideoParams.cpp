#include "gtest_EncodeVideoParams.h"

TEST_F(EncodeVideoParamsTest, SettersAndGetters)
{
    for (int i = -999; i < 9999; i += 3) {
        _params->setBitRate(i);
        ASSERT_EQ(_params->getBitRate(), i);
        _params->setFrameRate(i);
        ASSERT_EQ(_params->getFrameRate(), i);
        _params->setWidth(i);
        ASSERT_EQ(_params->getWidth(), i);
        _params->setHeight(i);
        ASSERT_EQ(_params->getHeight(), i);
        _params->setGopSize(i);
        ASSERT_EQ(_params->getGopSize(), i);
    }

    _params->setCodec(COMMON_CODEC_VIDEO_MPEG2);
    ASSERT_EQ(_params->getCodec(), COMMON_CODEC_VIDEO_MPEG2);
    _params->setCodec(COMMON_CODEC_VIDEO_H264);
    ASSERT_EQ(_params->getCodec(), COMMON_CODEC_VIDEO_H264);

    _params->setPixelsFormat(IvaPixFmt(IvaPixFmt::FMT_YUV422P));
    ASSERT_TRUE(_params->getPixelsFormat() == IvaPixFmt(IvaPixFmt::FMT_YUV422P));
    _params->setPixelsFormat(IvaPixFmt(IvaPixFmt::FMT_RGB32));
    ASSERT_TRUE(_params->getPixelsFormat() == IvaPixFmt(IvaPixFmt::FMT_RGB32));
}


