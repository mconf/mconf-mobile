#ifndef _QUEUE_EXTRA_DATA_AUDIO_H_
#define _QUEUE_EXTRA_DATA_AUDIO_H_

#include <common.h>
#include "QueueExtraData.h"

/** \brief Dados adicionais para buffers de áudio
 */
class QueueExtraDataAudio : public QueueExtraData
{
public:
    // dados abaixo são flags, ou seja, só setam 1 bit no uint8_t
    static const uint8_t AUDIO_ID_1    = 128;
    static const uint8_t AUDIO_ID_2    = 64;
    static const uint8_t AUDIO_ID_3    = 32;
    static const uint8_t AUDIO_ID_4    = 16;
    static const uint8_t AUDIO_ID_5    = 8;
    static const uint8_t AUDIO_ID_6    = 4;
    static const uint8_t AUDIO_ID_STOP = 1;
    static const uint8_t AUDIO_ID_NONE = 0;

private:
    uint8_t _codecId;           ///< Id do codec de áudio
    uint8_t _audioFlags;        ///< Flags de áudio
    uint32_t _bitrate;          ///< Bitrate de áudio

public:
    QueueExtraDataAudio(void);
    virtual ~QueueExtraDataAudio(void);

    QueueExtraDataType getType() {return EXTRA_DATA_AUDIO;};
    QueueExtraData * clone();

    uint8_t getCodecId();
    uint8_t getAudioFlags();
    uint32_t getBitrate();
    void setCodecId(uint8_t value);
    void setAudioFlags(uint8_t value);
    void setBitrate(uint32_t value);

    static uint8_t getFlagByPreview(int preview);
};

#endif
