#include <CommonLeaks.h>
#include "DecodeVideo.h"

extern "C" {
#include <libswscale/swscale.h>
};

#include <CommonLeaksCpp.h>

DecodeVideo::DecodeVideo() : Decode(),
    _lastPixFmt(), _needFrameI(false),
    _lastVideoId(QueueExtraDataVideo::VIDEO_ID_NONE),
#ifdef ANDROID
    _defaultPixFmt(IvaPixFmt::FMT_RGB565)
#else
    _defaultPixFmt(IvaPixFmt::FMT_YUV420P)
#endif
{
    _tempFrame = avcodec_alloc_frame();
}

DecodeVideo::~DecodeVideo()
{
    av_free(_tempFrame);
}

void DecodeVideo::setDeinterlace(bool value)
{
    _deinterlace = value;
}

int DecodeVideo::open(int codecId)
{
    _needFrameI = true;
    return Decode::open(codecId);
}

bool DecodeVideo::_NeedRestart(QueueExtraData * extraData)
{
    if (!_codecCtx || !extraData ||
        extraData->getType() != QueueExtraData::EXTRA_DATA_VIDEO) {
        return false;
    }

    QueueExtraDataVideo * extra = (QueueExtraDataVideo *)extraData;

    // na primeira vez guarda o id do vídeo sendo decodificado
    if (_lastVideoId == QueueExtraDataVideo::VIDEO_ID_NONE) {
        _lastVideoId = extra->getVideoId();
    }

    // se mudou a resolução ou o id do vídeo sendo recebido...
    if (extra->getWidth()  != _codecCtx->width || extra->getHeight() != _codecCtx->height ||
        extra->getVideoId() != _lastVideoId || extra->getCodecId() != _codecId) {
            _lastVideoId = extra->getVideoId();
            _codecId = extra->getCodecId();
            _needFrameI = true;
            return true;
    }
    return false;
}

int DecodeVideo::_PrepareContext()
{
    int err = Decode::_PrepareContext();
    if (err == E_OK) {
        _codecCtx->pix_fmt = _defaultPixFmt.toFfmpeg();
    }
    return err;
}

int DecodeVideo::decode(uint8_t * input, unsigned int size, unsigned int timestamp,
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

    uint32_t inbufSize = size;
    uint8_t * outbuf;
    int gotFrameInt;
    int outbufSize;
    QueueExtraDataVideo extraNew;

    if (gotFrame) {
        *gotFrame = false;
    }

    // só pode começar a decodificação em um quadro I
    if (_needFrameI) {
        if (_IsFrameI(&input, &inbufSize)) {
            _needFrameI = false;
        } else {
            NEW_WARNING(E_DECODE_WAITING_FRAME_I, "Aguardando um frame I");
            return -1;
        }
    }

    // fica decodificando o buffer até que encontre um frame completo ou até
    // que acabe o buffer disponível
    _codecCtxMutex.lock();
    int usedTotal = 0;
    while ((uint32_t)usedTotal < inbufSize) {
        AVPacket packet;
        av_init_packet(&packet);
        packet.data = input + usedTotal;
        packet.size = inbufSize - usedTotal;
        gotFrameInt = 0;
        int used = avcodec_decode_video2(_codecCtx, _tempFrame, &gotFrameInt, &packet);
        usedTotal += used;

        // se já usou todo buffer mas não completou o frame, tenta decodificar de novo o mesmo
        // buffer pra tentar pegar o frame. isso é necessário ao decodificar mpeg4
        if (usedTotal == inbufSize && !gotFrameInt && used >= 0) {
            used = avcodec_decode_video2(_codecCtx, _tempFrame, &gotFrameInt, &packet);
        }

        av_free_packet(&packet);

        if (used < 0) {
            NEW_ERROR(E_DECODE_VIDEO, "Falha decodificando video.");
            _codecCtxMutex.unlock();
            return -1;
        }

        if (gotFrameInt) { // pegou frame completo!
            break;
        }
    }
    _codecCtxMutex.unlock();

    // guarda variável de saída
    if (gotFrame) {
        *gotFrame = (gotFrameInt != 0);
    }

    // se não pegou um frame inteiro nem prossegue... retorna o que usou do buffer
    if (!(*gotFrame)) {
        return -1;
    }

    // desentrelaçamento, conversão de pix_fmt, cópia para o buffer de saída
    outbufSize = _PrepareFrame(_tempFrame, &outbuf);
    if (outbufSize <= 0) {
        return -1;
    }

    // coloca os dados na queue
    // obs: só coloca se pegou um frame inteiro
    extraNew =_UpdateExtraData((QueueExtraDataVideo *)extraData);
    if (queue_enqueue(outQueue, outbuf, outbufSize, timestamp, &extraNew) != E_OK) {
        queue_dealloc(outbuf);
        NEW_ERROR(E_DECODE_ENQUEUE, "");
        return -1;
    }

    return usedTotal;
}

void DecodeVideo::_Deinterlace(AVFrame * inFrame, AVFrame * outFrame)
{
    bool deinterlaced = false;

    if (avpicture_deinterlace((AVPicture *)outFrame, (AVPicture *)inFrame,
                              _codecCtx->pix_fmt,
                              _codecCtx->width, _codecCtx->height) >= 0) {
        deinterlaced = true;
    }

    if (!deinterlaced) {
        av_picture_copy((AVPicture *)outFrame, (AVPicture *)inFrame,
                        _codecCtx->pix_fmt, _codecCtx->width, _codecCtx->height);
    }
}

int DecodeVideo::_PrepareFrame(AVFrame * inFrame, uint8_t ** buffer)
{
    uint8_t *outbuf, *outbuf2;
    int outbufSize;
    AVFrame * frameTmp = avcodec_alloc_frame();
    AVFrame * frameTmp2 = avcodec_alloc_frame();

    // aloca buffer da queue e associa ao _tempFrame2
    outbufSize = avpicture_get_size(_codecCtx->pix_fmt, _codecCtx->width, _codecCtx->height);
    if (outbufSize <= 0) return 0;
    outbuf = (uint8_t *) queue_malloc(outbufSize);
    if (!outbuf) return 0;

    avpicture_fill((AVPicture *)frameTmp, outbuf, _codecCtx->pix_fmt, _codecCtx->width, _codecCtx->height);

    // faz desentrelaçamento e coloca resultado no _tempFrame2
    _Deinterlace(inFrame, frameTmp);

    // converte o pix_fmt se necessário
    if (_codecCtx->pix_fmt != _defaultPixFmt.toFfmpeg()) {

        outbuf2 = outbuf; // guarda ponteiro pro buffer temporário

        // novo buffer da queue para os dados finais
        outbufSize = avpicture_get_size(_defaultPixFmt.toFfmpeg(), _codecCtx->width, _codecCtx->height);
        outbuf = (uint8_t *) queue_malloc(outbufSize);
        avpicture_fill((AVPicture *)frameTmp2, outbuf, _defaultPixFmt.toFfmpeg(), _codecCtx->width, _codecCtx->height);

        _ConvertPixFmt(frameTmp, frameTmp2);

        queue_dealloc(outbuf2);
    }

    av_free(frameTmp);
    av_free(frameTmp2);

    *buffer = outbuf;
    return outbufSize;
}

void DecodeVideo::_ConvertPixFmt(AVFrame * inFrame, AVFrame * outFrame)
{
    // usa o swscale para mudar o pix_fmt
    SwsContext * scaleCtx = sws_getContext(
        _codecCtx->width, _codecCtx->height, _codecCtx->pix_fmt,
        _codecCtx->width, _codecCtx->height, _defaultPixFmt.toFfmpeg(),
        SWS_BILINEAR, NULL, NULL, NULL);
    sws_scale(scaleCtx, inFrame->data,
              inFrame->linesize, 0, _codecCtx->height,
              outFrame->data, outFrame->linesize);
    sws_freeContext(scaleCtx);
}

QueueExtraDataVideo DecodeVideo::_UpdateExtraData(QueueExtraDataVideo * extra)
{
    QueueExtraDataVideo extraNew;
    if (extra && extra->getType() == QueueExtraData::EXTRA_DATA_VIDEO) {
        extraNew = *extra;
    }
    extraNew.setWidth(_codecCtx->width);
    extraNew.setHeight(_codecCtx->height);
    extraNew.setCodecId(_codecId);
    extraNew.setPixelFmt(IvaPixFmt().fromFfmpeg(_codecCtx->pix_fmt));
    return extraNew;
}

bool DecodeVideo::_IsFrameI(uint8_t ** inbuf, uint32_t * size)
{
    int tempSize = *size;
    uint8_t * tempBuffer = *inbuf;
/*
    mpeg4
    0x000001B6
    h264
    0x0000000167
    0x0000000106
*/

    if (_codecId != COMMON_CODEC_VIDEO_MPEG4 &&
        _codecId != COMMON_CODEC_VIDEO_H264) {
            return true;
    }

    int step = 0;
    while (tempSize > 0) {
        if (_codecId == COMMON_CODEC_VIDEO_MPEG4) {
            switch (step) {
                case 0:
                case 1:
                    if (*tempBuffer == 0x00)
                        step++;
                    else
                        step = 0;
                    break;
                case 2:
                    if (*tempBuffer == 0x01)
                        step++;
                    else
                        step = 0;
                    break;
                case 3:
                    if (*tempBuffer == 0xB0)
                        step++;
                    else
                        step = 0;
                    break;
                case 4:
                    *size = tempSize + 4;
                    *inbuf = tempBuffer - 4;
                    return true;
            }
        } else  if (_codecId == COMMON_CODEC_VIDEO_H264) {
            switch (step) {
                case 0:
                case 1:
                case 2:
                    if (*tempBuffer == 0x00)
                        step++;
                    else
                        step = 0;
                    break;
                case 3:
                    if (*tempBuffer == 0x01)
                        step++;
                    else
                        step = 0;
                    break;
                case 4:
                    if (*tempBuffer == 0x06 || *tempBuffer == 0x67)
                        step++;
                    else
                        step = 0;
                    break;
                case 5:
                    *size = tempSize + 4;
                    *inbuf = tempBuffer - 4;
                    return true;
            }
        }
        tempSize--;
        tempBuffer++;
    }
    return false;
}


