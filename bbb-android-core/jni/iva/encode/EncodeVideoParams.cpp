#include <CommonLeaks.h>
#include <common.h>
#include "EncodeVideoParams.h"
#include <CommonLeaksCpp.h>

EncodeVideoParams::EncodeVideoParams() :
    _pixFmt(IvaPixFmt::FMT_YUV420P),
    _codec(COMMON_CODEC_NONE),
    _bitRate(COMMON_VIDEO_DEFAULT_BITRATE),
    _frameRate(COMMON_VIDEO_DEFAULT_FPS),
    _width(COMMON_VIDEO_DEFAULT_WIDTH),
    _height(COMMON_VIDEO_DEFAULT_HEIGHT),
    _gopSize(COMMON_VIDEO_DEFAULT_GOP)
{
}

void EncodeVideoParams::setCodec(int value)
{
    _codec = value;
}

void EncodeVideoParams::setPixelsFormat(IvaPixFmt &value)
{
    _pixFmt = value;
}

void EncodeVideoParams::setBitRate(int value)
{
    _bitRate = value;
}

void EncodeVideoParams::setFrameRate(int value)
{
    _frameRate = value;
}

void EncodeVideoParams::setWidth(int value)
{
    _width = value;
}

void EncodeVideoParams::setHeight(int value)
{
    _height = value;
}

void EncodeVideoParams::setGopSize(int value)
{
    _gopSize = value;
}

void EncodeVideoParams::setPresetFile(string value)
{
    _presetFile = value;
}

int EncodeVideoParams::getCodec()
{
    return _codec;
}

IvaPixFmt& EncodeVideoParams::getPixelsFormat()
{
    return _pixFmt;
}

int EncodeVideoParams::getBitRate()
{
    return _bitRate;
}

int EncodeVideoParams::getFrameRate()
{
    return _frameRate;
}

int EncodeVideoParams::getWidth()
{
    return _width;
}

int EncodeVideoParams::getHeight()
{
    return _height;
}

int EncodeVideoParams::getGopSize()
{
    return _gopSize;
}

string EncodeVideoParams::getPresetFile()
{
    return _presetFile;
}

