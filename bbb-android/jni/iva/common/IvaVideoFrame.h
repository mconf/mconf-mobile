#ifndef _IVA_VIDEO_FRAME_H
#define _IVA_VIDEO_FRAME_H

#include "common.h"
#include "IvaPixFmt.h"

extern "C" {
#include <libavcodec/avcodec.h>
};

class IvaVideoFrame
{
public:
    IvaVideoFrame();
    virtual ~IvaVideoFrame();

    void freeInternalData();
    void clear();

    long int getDataSize() const;
    unsigned char * getData() const;
    AVFrame * getFrame() const;
    int getFrameWidth() const;
    int getFrameHeight() const;
    unsigned int getTimestamp() const;
    IvaPixFmt& getPixelFormat();

    void setDataSize(const long int value);
    void setData(unsigned char * value, bool freeable = true);
    void setFrame(AVFrame * value);
    void setFrameWidth(const int value);
    void setFrameHeight(const int value);
    void setTimestamp(const unsigned int value);
    void setPixelFormat(IvaPixFmt& value);

private:
    AVFrame * _frame;           ///< Usado para guardar frames
    int _frameWidth;            ///< Largura do frame em \a _frame
    int _frameHeight;           ///< Altura do frame em \a _frame

    unsigned char * _data;      ///< Usado para guardar dados brutos
    long int _dataSize;         ///< Tamanho dos dados (necessário quando se usa dados brutos)
    bool _dataFree;             ///< Pode liberar a memória de \a _data?

    unsigned int _timestamp;    ///< Timestamp
    IvaPixFmt _pixelFormat;     ///< Formato dos pixels
};

#endif
