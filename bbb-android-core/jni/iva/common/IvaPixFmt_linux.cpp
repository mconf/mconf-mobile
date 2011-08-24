#include "IvaPixFmt.h"

IvaPixFmtVector::IvaPixFmtVector() :
    vector<IvaPixFmtItem>()
{
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_NONE,
                            PIX_FMT_NONE,
                            "NONE"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_YUV420P,
                            PIX_FMT_YUV420P,
                            "YUV420P"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_RGB24,
                            PIX_FMT_RGB24,
                            "RGB24"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_RGB32,
                            PIX_FMT_RGB32,
                            "RGB32"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_BGR32,
                            PIX_FMT_BGR32,
                            "BGR32"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_YUV422P,
                            PIX_FMT_YUV422P,
                            "YUV422P"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_YUV411P,
                            PIX_FMT_YUV411P,
                            "YUV411P"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_RGB555,
                            PIX_FMT_RGB555,
                            "RGB555"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_DVSD,
                            PIX_FMT_YUV411P,
                            "DVSD"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_RGB565,
                            PIX_FMT_RGB565LE,
                            "RGB565"));    
}

IvaPixFmtItem::IvaPixFmtItem(enum IvaPixFmt::PixFmt value, enum PixelFormat ffmpeg,
                             const string name) :
    _value(value),
    _ffmpeg(ffmpeg),
    _name(name)
{
}

