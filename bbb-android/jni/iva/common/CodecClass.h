#ifndef _CODEC_CLASS_H_
#define _CODEC_CLASS_H_

extern "C" {
#include <libavcodec/avcodec.h>
};

class CodecClass
{
public:
    static enum CodecID ffmpegCodecId(int id)
    {
        switch (id) {
            case COMMON_CODEC_VIDEO_MPEG2:
                return CODEC_ID_MPEG2VIDEO;
            case COMMON_CODEC_VIDEO_MPEG4:
                return CODEC_ID_MPEG4;
            case COMMON_CODEC_VIDEO_H264:
                return CODEC_ID_H264;
            case COMMON_CODEC_AUDIO_MP2:
                return CODEC_ID_MP2;
            case COMMON_CODEC_AUDIO_AAC:
                return CODEC_ID_AAC;
            case COMMON_CODEC_VIDEO_DV25:
                return CODEC_ID_DVVIDEO;
            default:
                return CODEC_ID_NONE;
        }
    }
};

#endif
