#include <CommonLeaks.h>
#include "DecodeAudio.h"
#include <CommonLeaksCpp.h>

DecodeAudio::DecodeAudio() : Decode()
{
}

DecodeAudio::~DecodeAudio()
{
}

int DecodeAudio::decode(uint8_t * input, unsigned int size, unsigned int timestamp,
                        queue_t * outQueue, bool * gotFrame,
                        QueueExtraData * extraData)
{
    if (!(_flags & FLAG_OPENED)) {
        NEW_ERROR(E_DECODE_NOT_OPENED, "");
        return -1;
    }
    if (!outQueue || !input || !size) {
        NEW_ERROR(E_COMMON_NULL_PARAMETER, "outQueue | input | size");
        return -1;
    }

    int inbufSize = size;
    int outbufSize = AVCODEC_MAX_AUDIO_FRAME_SIZE;
    uint8_t * decodedBuffer;
    QueueExtraDataAudio extraNew;

    if (gotFrame) {
        *gotFrame = false;
    }

    /* Obs: o ffmpeg comenta que o buffer precisa ter FF_INPUT_BUFFER_PADDING_SIZE zeros no final
     * para evitar erros. Mas com esses zeros, acontece erro quando o ffmpeg acaba usando alguns
     * deles para decodificar os dados.
     * Então é melhor não colocar os zeros e tratar o erro que o ffmpeg retorna.
     */

    // se os dados de entrada são muito grandes, usa o máximo possível num frame de áudio
    if (inbufSize > AVCODEC_MAX_AUDIO_FRAME_SIZE) {
        inbufSize = AVCODEC_MAX_AUDIO_FRAME_SIZE;
    }

    // aloca o buffer para os dados decodificados
    decodedBuffer = (uint8_t *)queue_malloc(outbufSize);

    // fica decodificando o buffer até que encontre um frame completo ou até
    // que acabe o buffer disponível
    _codecCtxMutex.lock();
    int usedTotal = 0;
    while (usedTotal < inbufSize) {
        AVPacket packet;
        av_init_packet(&packet);
        packet.data = input + usedTotal;
        packet.size = inbufSize - usedTotal;
        int used = avcodec_decode_audio3(_codecCtx, (int16_t *)decodedBuffer, &outbufSize, &packet);
        av_free_packet(&packet);

        if (used < 0) {
            queue_dealloc(decodedBuffer);
            NEW_ERROR(E_DECODE_AUDIO, "Falha decodificando audio.");
            _codecCtxMutex.unlock();
            return -1;
        }
        usedTotal += used;
        if (outbufSize > 0) { // pegou frame completo
            break;
        }
    }
    _codecCtxMutex.unlock();

    if (gotFrame) {
        *gotFrame = (usedTotal > 0) && (outbufSize > 0);
    }
    // se não pegou frame, retorna
    if (!(*gotFrame)) {
        queue_dealloc(decodedBuffer);
        return 0;
    }

    // coloca os dados na queue
    // obs: só coloca se pegou um frame inteiro
    extraNew =_UpdateExtraData((QueueExtraDataAudio *)extraData);
    if (queue_enqueue(outQueue, decodedBuffer, outbufSize, timestamp, &extraNew) != E_OK) {
        queue_dealloc(decodedBuffer);
        NEW_ERROR(E_DECODE_ENQUEUE, "");
        return -1;
    }
    return usedTotal;
}

QueueExtraDataAudio DecodeAudio::_UpdateExtraData(QueueExtraDataAudio * extra)
{
    QueueExtraDataAudio extraNew;
    if (extra && extra->getType() == QueueExtraData::EXTRA_DATA_AUDIO) {
        extraNew = *extra;
    }
    extraNew.setCodecId(_codecId);
    return extraNew;
}


