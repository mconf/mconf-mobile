#include <CommonLeaks.h>
#include "QueueExtraDataVideo.h"
#include <CommonLeaksCpp.h>

QueueExtraDataVideo::QueueExtraDataVideo(void) :
    QueueExtraData(),
    _pixFmt(IvaPixFmt::FMT_NONE), _codecId(COMMON_CODEC_NONE),
    _fps(0), _width(0), _height(0), _bitrate(0), _videoId(VIDEO_ID_NONE)
{
}

QueueExtraDataVideo::~QueueExtraDataVideo(void)
{
}

QueueExtraData * QueueExtraDataVideo::clone()
{
    return (new QueueExtraDataVideo(*this));
}

uint8_t QueueExtraDataVideo::getCodecId()
{
    return _codecId;
}

uint8_t QueueExtraDataVideo::getFps()
{
    return _fps;
}

uint16_t QueueExtraDataVideo::getWidth()
{
    return _width;
}

uint16_t QueueExtraDataVideo::getHeight()
{
    return _height;
}

uint32_t QueueExtraDataVideo::getBitrate()
{
    return _bitrate;
}

uint16_t QueueExtraDataVideo::getVideoId()
{
    return _videoId;
}

IvaPixFmt& QueueExtraDataVideo::getPixelFmt()
{
    return _pixFmt;
}

void QueueExtraDataVideo::setCodecId(uint8_t value)
{
    _codecId = value;
}

void QueueExtraDataVideo::setFps(uint8_t value)
{
    _fps = value;
}

void QueueExtraDataVideo::setWidth(uint16_t value)
{
    _width = value;
}

void QueueExtraDataVideo::setHeight(uint16_t value)
{
    _height = value;
}

void QueueExtraDataVideo::setBitrate(uint32_t value)
{
    _bitrate = value;
}

void QueueExtraDataVideo::setVideoId(uint16_t value)
{
    _videoId = value;
}

void QueueExtraDataVideo::setPixelFmt(const IvaPixFmt& value)
{
    _pixFmt = value;
}

uint16_t QueueExtraDataVideo::getVideoIdByPreview(int preview)
{
    switch (preview) {
        case 0: return VIDEO_ID_1;
        case 1: return VIDEO_ID_2;
        case 2: return VIDEO_ID_3;
        case 3: return VIDEO_ID_4;
        case 4: return VIDEO_ID_5;
        case 5: return VIDEO_ID_6;
        default: return VIDEO_ID_NONE;
    }
}

