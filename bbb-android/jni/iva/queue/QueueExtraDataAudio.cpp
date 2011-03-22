#include <CommonLeaks.h>
#include "QueueExtraDataAudio.h"
#include <CommonLeaksCpp.h>

QueueExtraDataAudio::QueueExtraDataAudio(void) :
    QueueExtraData()
{
    _codecId = COMMON_CODEC_NONE;
    _audioFlags = AUDIO_ID_NONE;
    _bitrate = 0;
}

QueueExtraDataAudio::~QueueExtraDataAudio(void)
{
}

QueueExtraData * QueueExtraDataAudio::clone()
{
    return (new QueueExtraDataAudio(*this));
}

uint8_t QueueExtraDataAudio::getCodecId()
{
    return _codecId;
}

uint8_t QueueExtraDataAudio::getAudioFlags()
{
    return _audioFlags;
}

uint32_t QueueExtraDataAudio::getBitrate()
{
    return _bitrate;
}

void QueueExtraDataAudio::setCodecId(uint8_t value)
{
    _codecId = value;
}

void QueueExtraDataAudio::setAudioFlags(uint8_t value)
{
    _audioFlags = value;
}

void QueueExtraDataAudio::setBitrate(uint32_t value)
{
    _bitrate = value;
}

uint8_t QueueExtraDataAudio::getFlagByPreview(int preview)
{
    switch (preview) {
        case 0: return AUDIO_ID_1;
        case 1: return AUDIO_ID_2;
        case 2: return AUDIO_ID_3;
        case 3: return AUDIO_ID_4;
        case 4: return AUDIO_ID_5;
        case 5: return AUDIO_ID_6;
        default: return AUDIO_ID_NONE;
    }
}

