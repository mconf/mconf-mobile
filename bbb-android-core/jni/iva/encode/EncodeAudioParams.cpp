#include <CommonLeaks.h>
#include <common.h>
#include "EncodeAudioParams.h"
#include <CommonLeaksCpp.h>

EncodeAudioParams::EncodeAudioParams()
{
    _codec = COMMON_CODEC_NONE;
    _channels = COMMON_AUDIO_DEFAULT_CHANNELS;
    _bitRate = COMMON_AUDIO_DEFAULT_BITRATE;
    _sampleRate = COMMON_AUDIO_DEFAULT_FREQUENCY;
}

void EncodeAudioParams::setCodec(int value)
{
    _codec = value;
}

void EncodeAudioParams::setChannels(int value)
{
    _channels = value;
}

void EncodeAudioParams::setBitRate(int value)
{
    _bitRate = value;
}

void EncodeAudioParams::setSampleRate(int value)
{
    _sampleRate = value;
}

int EncodeAudioParams::getCodec()
{
    return _codec;
}

int EncodeAudioParams::getChannels()
{
    return _channels;
}

int EncodeAudioParams::getBitRate()
{
    return _bitRate;
}

int EncodeAudioParams::getSampleRate()
{
    return _sampleRate;
}
