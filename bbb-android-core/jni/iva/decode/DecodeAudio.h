#ifndef _DECODE_AUDIO_H_
#define _DECODE_AUDIO_H_

#include <QueueExtraDataAudio.h>
#include "Decode.h"

/** \brief Classe para decodificação de áudio
 */
class DecodeAudio : public Decode
{
private:
    QueueExtraDataAudio _UpdateExtraData(QueueExtraDataAudio * extra);

public:
    DecodeAudio();
    ~DecodeAudio();

    /** \brief Decodifica um bloco de dados. Ver Decode::decode().
     */
    virtual int decode(uint8_t * input, unsigned int size, unsigned int timestamp,
                       queue_t * outQueue, bool * gotFrame,
                       QueueExtraData * extraData = NULL);
};

#endif

