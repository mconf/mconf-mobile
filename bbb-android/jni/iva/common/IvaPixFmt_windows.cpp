#include "IvaPixFmt.h"

IvaPixFmtVector::IvaPixFmtVector() :
    vector<IvaPixFmtItem>()
{
    /* Obs: Existem vários formatos de DV.
       Para as GUIDs ver: http://msdn.microsoft.com/en-us/library/dd388646(VS.85).aspx
       Para ffmpeg ver (libavcodec/dvdata.c):
            http://cekirdek.pardus.org.tr/~ismail/ffmpeg-docs/dvdata_8c-source.html
       No IVA é usado o DVSD que usa pix_fmt PIX_FMT_YUV411P
    */
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_NONE,
                            PIX_FMT_NONE,
                            MEDIASUBTYPE_None,
                            "NONE"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_YUV420P,
                            PIX_FMT_YUV420P,
                            MEDIASUBTYPE_YV12, /// \todo Confirmar GUID
                            "YUV420P"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_RGB24,
                            PIX_FMT_RGB24,
                            MEDIASUBTYPE_RGB24,
                            "RGB24"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_RGB32,
                            PIX_FMT_RGB32,
                            MEDIASUBTYPE_RGB32,
                            "RGB32"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_BGR32,
                            PIX_FMT_BGR32,
                            MEDIASUBTYPE_None, /// \todo Confirmar GUID
                            "BGR32"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_YUV422P,
                            PIX_FMT_YUV422P,
                            MEDIASUBTYPE_YV12, /// \todo Confirmar GUID
                            "YUV422P"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_YUV411P,
                            PIX_FMT_YUV411P,
                            MEDIASUBTYPE_Y41P, /// \todo Confirmar GUID
                            "YUV411P"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_RGB555,
                            PIX_FMT_RGB555,
                            MEDIASUBTYPE_RGB555,
                            "RGB555"));
    push_back(IvaPixFmtItem(IvaPixFmt::FMT_DVSD,
                            PIX_FMT_YUV411P,
                            MEDIASUBTYPE_dvsd,
                            "DVSD"));
}



GUID IvaPixFmt::toGUID() const
{
    return Items[_format].getGUID();
}

IvaPixFmt& IvaPixFmt::fromGUID(const GUID value)
{
    bool found = false;
    vector<IvaPixFmtItem>::const_iterator it;
    for (it = Items.begin(); it != Items.end(); ++it) {
        if ((*it).getGUID() == value) {
            found = true;
            this->set((*it).get());
            break;
        }
    }
    if (!found) set(FMT_NONE);
    return *this;
}


IvaPixFmtItem::IvaPixFmtItem(enum IvaPixFmt::PixFmt value, enum PixelFormat ffmpeg,
                             const GUID guid, const string name) :
    _value(value),
    _ffmpeg(ffmpeg),
    _guid(guid),
    _name(name)
{
}

GUID IvaPixFmtItem::getGUID() const
{
    return _guid;
}


