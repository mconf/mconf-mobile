#ifndef _QUEUE_EXTRA_DATA_VIDEO_H_
#define _QUEUE_EXTRA_DATA_VIDEO_H_

#include <common.h>
#include "QueueExtraData.h"

/** \brief Dados adicionais para buffers de áudio
 */
class QueueExtraDataVideo : public QueueExtraData
{
public:
    // dados abaixo não são flags, são valores inteiros
    static const uint16_t VIDEO_ID_NONE = 0;
    static const uint16_t VIDEO_ID_1    = 1;
    static const uint16_t VIDEO_ID_2    = 2;
    static const uint16_t VIDEO_ID_3    = 3;
    static const uint16_t VIDEO_ID_4    = 4;
    static const uint16_t VIDEO_ID_5    = 5;
    static const uint16_t VIDEO_ID_6    = 6;

private:
    uint8_t _codecId;           ///< Codec de vídeo
    uint8_t _fps;               ///< Número de quadros por segundo
    uint16_t _width;            ///< Largura do vídeo
    uint16_t _height;           ///< Altura do vídeo
    uint32_t _bitrate;          ///< Bitrate do vídeo
    uint16_t _videoId;          ///< Identificador de vídeo
    IvaPixFmt _pixFmt;          ///< Formato dos pixels

public:
    QueueExtraDataVideo(void);
    virtual ~QueueExtraDataVideo(void);

    QueueExtraDataType getType() {return EXTRA_DATA_VIDEO;};
    QueueExtraData * clone();

    uint8_t getCodecId();
    uint8_t getFps();
    uint16_t getWidth();
    uint16_t getHeight();
    uint32_t getBitrate();
    uint16_t getVideoId();
    IvaPixFmt& getPixelFmt();
    void setCodecId(uint8_t value);
    void setFps(uint8_t value);
    void setWidth(uint16_t value);
    void setHeight(uint16_t value);
    void setBitrate(uint32_t value);
    void setVideoId(uint16_t value);
    void setPixelFmt(const IvaPixFmt& value);

    static uint16_t getVideoIdByPreview(int preview);
};

#endif
