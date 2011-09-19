#include <CommonLeaks.h>
#include "EncodeVideo.h"
#include "EncodeVideoParams.h"
//#include "EncodeVideoH264Presets.h"
#include <CommonLeaksCpp.h>

EncodeVideo::EncodeVideo() :
    Encode(),
    _params(NULL)
{
}

EncodeVideo::~EncodeVideo()
{
    if (_params) {
        delete _params;
    }
}

EncodeVideoParams * EncodeVideo::getParams()
{
    return _params;
}

void EncodeVideo::close()
{
    if (_params) {
        delete _params;
        _params = NULL;
    }
    Encode::close();
}

void EncodeVideo::_SetContextParameters()
{
    _codecCtxMutex.lock();

    // formato dos pixels.
    _codecCtx->pix_fmt = _params->getPixelsFormat().toFfmpeg();
    // Hardcoded. 1.
    _codecCtx->time_base.num = NUMERATOR;
    // frame rate
    _codecCtx->time_base.den = _params->getFrameRate();
    // taxa de bits.
    _codecCtx->bit_rate = _params->getBitRate();
    _codecCtx->bit_rate_tolerance = _params->getBitRate();
    // largura e altura.
    _codecCtx->width = _params->getWidth();
    _codecCtx->height = _params->getHeight();
    // Hardcoded. Número de quadros B. Nulo por otimização.
    _codecCtx->max_b_frames = NUMB;
    // tamanho do GOP.
    _codecCtx->gop_size = _params->getGopSize();
/*
    // tratamento do preset caso esteja codificando em H.264
    if (_params->getCodec() == COMMON_CODEC_VIDEO_H264) {
        //_codecCtx->profile = FF_PROFILE_H264_BASELINE; /// \todo Testar se seta o profile assim

        _codecCtx->bit_rate *= 1000;
        _codecCtx->bit_rate_tolerance *= 1000;

        // se tem arquivo de preset, carrega informações no contexto
        string preset = _params->getPresetFile();
        if (!preset.empty()) {
            EncodeVideoH264Presets parser;
            parser.parse(preset, _codecCtx);
        }

        // para fugir do erro "broken ffmpeg default settings detected. use an encoding preset (vpre)"
        // se não é usado um preset, por padrão usa o preset baseline
        else {
            _codecCtx->me_range = 16;
            _codecCtx->max_qdiff = 4;
            _codecCtx->qmin = 10;
            _codecCtx->qmax = 51;
            _codecCtx->qcompress = (float)0.6;
            // score += h->param.rc.i_qp_step == 3;
            // score += h->param.i_keyint_max == 12;
            // score += fabs(h->param.rc.f_ip_factor - 1.25) < 0.01;
            // score += fabs(h->param.rc.f_pb_factor - 1.25) < 0.01;
            // score += h->param.analyse.inter == 0 && h->param.analyse.i_subpel_refine == 8;
            // preset: baseline
            _codecCtx->coder_type = 0;
            _codecCtx->bframebias = 0;
            _codecCtx->flags2 = ~CODEC_FLAG2_WPRED & ~CODEC_FLAG2_8X8DCT; //-wpred-dct8x8;
            _codecCtx->weighted_p_pred = 0;
        }
    }
*/
    _codecCtxMutex.unlock();
}

int EncodeVideo::setParams(EncodeVideoParams * params)
{
    int err;

    // se não deu um open() ainda só guarda os parâmetros
    if (!(_flags & FLAG_OPENED) || !_params) {
        if (_params) {
            delete _params;
        }
        _params = new EncodeVideoParams(*params);
    }

    // se já deu um open() tem que reiniciar algumas coisas...
    else {
        // verifica o que mudou
        bool changedCodec = ( _params->getCodec() != params->getCodec() );
        bool changedOther = (
            _params->getBitRate() != params->getBitRate() ||
            _params->getFrameRate() != params->getFrameRate() ||
            _params->getGopSize() != params->getGopSize() ||
            _params->getHeight() != params->getHeight() ||
            _params->getPixelsFormat() != params->getPixelsFormat() ||
            _params->getWidth() != params->getWidth()
            );

        // já pode guardar os parâmetros novos
        delete _params;
        _params = new EncodeVideoParams(*params);

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

int EncodeVideo::open(EncodeVideoParams * params)
{
    int err;

    if (_flags & FLAG_OPENED) {
        NEW_ERROR(E_ENCODE_ALREADY_OPENED, "");
        return E_ENCODE_ALREADY_OPENED;
    }

    if (_params) {
        delete _params;
    }
    _params = new EncodeVideoParams(*params);

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

int EncodeVideo::encode(uint8_t * input, unsigned int size, unsigned int timestamp,
                        queue_t * outQueue, QueueExtraData * extraData)
{
    if (!outQueue || !input) {
        NEW_ERROR(E_COMMON_NULL_PARAMETER, "outQueue | input");
        return E_COMMON_NULL_PARAMETER;
    }
    if (!(_flags & FLAG_OPENED)) {
        NEW_ERROR(E_ENCODE_NOT_OPENED, "");
        return E_ENCODE_NOT_OPENED;
    }

    // aloca um AVFrame pra codificar
    AVFrame * frame;
    frame = avcodec_alloc_frame();
    avpicture_fill((AVPicture *)frame, input,
                   _params->getPixelsFormat().toFfmpeg(),
                   _params->getWidth(), _params->getHeight());

    // chama a função que codifica mesmo
    int ret = this->encode(frame, timestamp, outQueue, extraData);

    // libera o frame
    av_free(frame);

    return ret;
}

int EncodeVideo::encode(AVFrame * input, unsigned int timestamp, queue_t * outQueue,
                        QueueExtraData * extraData)
{
    int size;
    unsigned char * outBuffer;
    int outBufferSize;
    QueueExtraDataVideo extraNew;

    if (!outQueue || !input) {
        NEW_ERROR(E_COMMON_NULL_PARAMETER, "outQueue | input");
        return -1;
    }
    if (!(_flags & FLAG_OPENED)) {
        NEW_ERROR(E_ENCODE_NOT_OPENED, "");
        return -1;
    }

    // aloca o buffer na queue
    size = avpicture_get_size(_codecCtx->pix_fmt,
                              _codecCtx->width,
                              _codecCtx->height);
    outBuffer = (unsigned char *) queue_malloc(size);
    if (!outBuffer) {
        NEW_ERROR(E_COMMON_MEMORY_ERROR, "");
        return -1;
    }

    // codifica
    _codecCtxMutex.lock();
    outBufferSize = avcodec_encode_video(
        _codecCtx,
        outBuffer,
        size,
        input
        );
    _codecCtxMutex.unlock();
    if (outBufferSize < 0) {
        queue_dealloc(outBuffer);
        NEW_ERROR(E_ENCODE_VIDEO, "");
        return -1;
    }

    // coloca na queue
    extraNew =_UpdateExtraData((QueueExtraDataVideo *)extraData);
    if (queue_enqueue(outQueue, outBuffer, outBufferSize, timestamp, &extraNew) != E_OK) {
        queue_dealloc(outBuffer);
    }

    return outBufferSize;
}

QueueExtraDataVideo EncodeVideo::_UpdateExtraData(QueueExtraDataVideo * extra)
{
    QueueExtraDataVideo extraNew;
    if (extra && extra->getType() == QueueExtraData::EXTRA_DATA_VIDEO) {
        extraNew = *extra;
    }
    extraNew.setBitrate(_params->getBitRate());
    extraNew.setCodecId(_params->getCodec());
    extraNew.setFps(_params->getFrameRate());
    extraNew.setWidth(_params->getWidth());
    extraNew.setHeight(_params->getHeight());
    return extraNew;
}


