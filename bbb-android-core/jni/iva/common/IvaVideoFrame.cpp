#include "IvaVideoFrame.h"

IvaVideoFrame::IvaVideoFrame() :
    _data(NULL), _dataSize(0), _dataFree(false),
    _frame(NULL), _frameWidth(0), _frameHeight(0),
    _timestamp(0), _pixelFormat(IvaPixFmt::FMT_NONE)//, _mediaSubtype(0)
{
}

IvaVideoFrame::~IvaVideoFrame()
{
}

void IvaVideoFrame::freeInternalData()
{
    if (_frame) {
        avpicture_free((AVPicture *)_frame);
        av_free(_frame);
        _frame = NULL;
    }
    if (_data) {
        if (_dataFree) {
            free(_data);
        }
        _data = NULL;
    }
    _frameWidth = 0;
    _frameHeight = 0;
    _dataSize = 0;
    _dataFree = false;
    _timestamp = 0;
    _pixelFormat.clear();
}

void IvaVideoFrame::clear()
{
    _frame = NULL;
    _data = NULL;
    _frameWidth = 0;
    _frameHeight = 0;
    _dataSize = 0;
    _timestamp = 0;
    _pixelFormat.clear();
}

long int IvaVideoFrame::getDataSize() const
{
    return _dataSize;
}

unsigned char * IvaVideoFrame::getData() const
{
    return _data;
}

AVFrame * IvaVideoFrame::getFrame() const
{
    return _frame;
}

int IvaVideoFrame::getFrameWidth() const
{
    return _frameWidth;
}

int IvaVideoFrame::getFrameHeight() const
{
    return _frameHeight;
}

unsigned int IvaVideoFrame::getTimestamp() const
{
    return _timestamp;
}

IvaPixFmt& IvaVideoFrame::getPixelFormat()
{
    return _pixelFormat;
}

void IvaVideoFrame::setDataSize(const long int value)
{
    _dataSize = value;

}

void IvaVideoFrame::setData(unsigned char * value, bool freeable)
{
    _dataFree = freeable;
    _data = value;
}

void IvaVideoFrame::setFrame(AVFrame * value)
{
    _frame = value;
}

void IvaVideoFrame::setFrameWidth(const int value)
{
    _frameWidth = value;
}

void IvaVideoFrame::setFrameHeight(const int value)
{
    _frameHeight = value;
}

void IvaVideoFrame::setTimestamp(const unsigned int value)
{
    _timestamp = value;
}

void IvaVideoFrame::setPixelFormat(IvaPixFmt& value)
{
    _pixelFormat = value;
}


