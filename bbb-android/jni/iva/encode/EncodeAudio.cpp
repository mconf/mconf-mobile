#include <CommonLeaks.h>
#include "EncodeAudio.h"
#include "EncodeAudioParams.h"
#include <CommonLeaksCpp.h>

EncodeAudio::EncodeAudio() :
    Encode()
{
    _params = NULL;
}

EncodeAudio::~EncodeAudio()
{
    if (_params) {
        delete _params;
    }
}

EncodeAudioParams * EncodeAudio::getParams()
{
    return _params;
}

void EncodeAudio::close()
{
    if (_params) {
        delete _params;
        _params = NULL;
    }
    Encode::close();
}

void EncodeAudio::_SetContextParameters()
{
    _codecCtxMutex.lock();

    // taxa de bits
    _codecCtx->bit_rate = _params->getBitRate();
    _codecCtx->bit_rate_tolerance = _params->getBitRate();
    // frequência de amostragem
    _codecCtx->sample_rate = _params->getSampleRate();
    // número de canais
    _codecCtx->channels = _params->getChannels();

    _codecCtxMutex.unlock();
}

int EncodeAudio::setParams(EncodeAudioParams * params)
{
    int err;

    // se não deu um open() ainda só guarda os parâmetros
    if (!(_flags & FLAG_OPENED) || !_params) {
        if (_params) {
            delete _params;
        }
        _params = new EncodeAudioParams(*params);
    }

    // se já deu um open() tem que reiniciar algumas coisas...
    else {
        // verifica o que mudou
        bool changedCodec = ( _params->getCodec() != params->getCodec() );
        bool changedOther = (
            _params->getBitRate() != params->getBitRate() ||
            _params->getChannels() != params->getChannels() ||
            _params->getSampleRate() != params->getSampleRate()
            );

        // já pode guardar os parâmetros novos
        delete _params;
        _params = new EncodeAudioParams(*params);

        // seta parâmetros novos no contexto
        if (changedOther) {
            _SetContextParameters();
        }
        // se mudou codec tem que pegar o novo e associar ao contexto
        if (changedCodec) {
            err = _PrepareCodec(_params->getCodec());
            if (err != E_OK) {
                return err;
            }
            err = _BindCodecToContext();
            if (err != E_OK) {
                return err;
            }
        }
    }

    return E_OK;
}

int EncodeAudio::open(EncodeAudioParams * params)
{
    int err;

    if (_flags & FLAG_OPENED) {
        NEW_ERROR(E_ENCODE_ALREADY_OPENED, "");
        return E_ENCODE_ALREADY_OPENED;
    }

    // guarda os parâmetros
    if (_params) {
        delete _params;
    }
    _params = new EncodeAudioParams(*params);

    // prepara o contexto e seta os parâmetros nele
    err = _PrepareContext();
    if (err != E_OK) {
        return err;
    }
    _SetContextParameters();

    // prepara o codec
    err = _PrepareCodec(_params->getCodec());
    if (err != E_OK) {
        return err;
    }

    // associa o codec no contexto
    err = _BindCodecToContext();
    if (err != E_OK) {
        return err;
    }

    _flags |= FLAG_OPENED; // liga flag de opened

    return E_OK;
}

int EncodeAudio::encode(uint8_t * input, unsigned int size, unsigned int timestamp,
                        queue_t * outQueue, QueueExtraData * extraData)
{
    unsigned char * outBuffer;
    int outBufferSize;
    QueueExtraDataAudio extraNew;

    if (!outQueue || !input || !size) {
        NEW_ERROR(E_COMMON_NULL_PARAMETER, "outQueue | input | size");
        return -1;
    }
    if (!(_flags & FLAG_OPENED)) {
        NEW_ERROR(E_ENCODE_NOT_OPENED, "");
        return -1;
    }

    // aloca buffer de saída
    outBuffer = (unsigned char *)queue_malloc(size);
    if (!outBuffer) {
        NEW_ERROR(E_COMMON_MEMORY_ERROR, "");
        return -1;
    }

    // codifica
    outBufferSize = avcodec_encode_audio(
        _codecCtx,
        outBuffer,
        size,
        (short *)input
        );
    if (outBufferSize < 0) {
        queue_dealloc(outBuffer);
        NEW_ERROR(E_ENCODE_AUDIO, "");
        return -1;
    }

    // coloca na queue
    extraNew =_UpdateExtraData((QueueExtraDataAudio *)extraData);
    if (queue_enqueue(outQueue, outBuffer, outBufferSize, timestamp, &extraNew) != E_OK) {
        queue_dealloc(outBuffer);
    }

    return outBufferSize;
}

int EncodeAudio::getFrameSize()
{
    if (_codecCtx) {
        return _codecCtx->frame_size;
    } else {
        return 0;
    }
}

QueueExtraDataAudio EncodeAudio::_UpdateExtraData(QueueExtraDataAudio * extra)
{
    QueueExtraDataAudio extraNew;
    if (extra && extra->getType() == QueueExtraData::EXTRA_DATA_AUDIO) {
        extraNew = *extra;
    }
    extraNew.setBitrate(_params->getBitRate());
    extraNew.setCodecId(_params->getCodec());
    return extraNew;
}


