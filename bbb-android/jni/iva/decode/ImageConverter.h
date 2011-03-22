#pragma once

#include <common.h>
#include <queue.h>
#include <QueueExtraDataVideo.h>

extern "C" {
#include <libswscale/swscale.h>
#include <libavcodec/avcodec.h>
};

class ImageConverter : public Runnable
{
private:
    SwsContext* _scaleContext;
    AVFrame* _outFrame, * _inFrame;
    int _inWidth, _inHeight, 
        _outWidth, _outHeight;
    IvaPixFmt _inFormat, _outFormat;

    queue_t* _inQueue, * _outQueue;
    bool _stopThread;
public:
    ImageConverter()
        : _scaleContext(NULL)
        , _inFrame(avcodec_alloc_frame())
        , _outFrame(avcodec_alloc_frame())
    {
    }

    ~ImageConverter()
    {
        av_free(_inFrame);
        av_free(_outFrame);
        if (_scaleContext) {
            sws_freeContext(_scaleContext);
            _scaleContext = NULL;
        }
    }

    void start(queue_t* inQueue, queue_t* outQueue, int outWidth, int outHeight, IvaPixFmt outFormat)
    {
        _inQueue = inQueue;
        _outQueue = outQueue;
        _outWidth = outWidth;
        _outHeight = outHeight;
        _outFormat = outFormat;

        _stopThread = false;
        run(true);
    }

    void stop()
    {
        if (isRunning()) {
            _stopThread = true;
            queue_broadcast(_inQueue);
            join();
        }
    }

    void threadFunction()
    {
        queue_consumer_t* consumer = queue_registerConsumer(_inQueue);

        uint8_t* inbuffer;
        uint32_t inbuffersize, timestamp;
        QueueExtraDataVideo* extraData;

        while (!_stopThread) {
            if (queue_dequeueCond(consumer, &inbuffer, &inbuffersize, &timestamp, (QueueExtraData**) &extraData) != E_OK)
                continue;

            uint8_t* outbuffer;
            uint32_t outbuffersize;

            int r = convert(inbuffer, inbuffersize, extraData->getWidth(), extraData->getHeight(), extraData->getPixelFmt(),
                &outbuffer, &outbuffersize, _outWidth, _outHeight, _outFormat);

            queue_free(consumer);

            if (r != E_OK)
                continue;

            QueueExtraDataVideo newData(*extraData);
            newData.setWidth(_outWidth);
            newData.setHeight(_outHeight);
            newData.setPixelFmt(_outFormat);
            r = queue_enqueue(_outQueue, outbuffer, outbuffersize, timestamp, &newData);
            if (r != E_OK) {
                NEW_ERROR(E_COMMON_MEMORY_ERROR, "Não conseguiu colocar o novo frame na fila");
                continue;
            }
        }

        queue_unregisterConsumer(&consumer);
    }

    int convert(uint8_t* inFrame, uint32_t inSize, int inWidth, int inHeight, IvaPixFmt inFormat,
        uint8_t** outFrame, uint32_t* outSize, int outWidth, int outHeight, IvaPixFmt outFormat)
    {
        if (!_scaleContext
            || inWidth != _inWidth
            || inHeight != _inHeight
            || inFormat != _inFormat
            || outWidth != _outWidth
            || outHeight != _outHeight
            || outFormat != _outFormat) {
                if (_scaleContext) {
                    sws_freeContext(_scaleContext);
                    _scaleContext = NULL;
                }
                _scaleContext = sws_getContext(inWidth, inHeight, inFormat.toFfmpeg(),
                    outWidth, outHeight, outFormat.toFfmpeg(),
                    SWS_BILINEAR, NULL, NULL, NULL);
                if (!_scaleContext) {
                    NEW_ERROR(E_COMMON_MEMORY_ERROR, "Falha ao criar o contexto do SWScale");
                    return E_COMMON_MEMORY_ERROR;
                }
                _inWidth = inWidth;
                _inHeight = inHeight;
                _inFormat = inFormat;
                _outWidth = outWidth;
                _outHeight = outHeight;
                _outFormat = outFormat;
        }
       
        *outSize = avpicture_get_size(outFormat.toFfmpeg(), outWidth, outHeight);
        if (*outSize <= 0) {
            NEW_ERROR(E_COMMON_MEMORY_ERROR, "Falha ao recuperar o tamanho da imagem de saída");
            return E_COMMON_MEMORY_ERROR;
        }
        *outFrame = (uint8_t *) queue_malloc(*outSize);
        if (!*outFrame) {
            NEW_ERROR(E_COMMON_MEMORY_ERROR, "Falha ao alocar o buffer de saída");
            return E_COMMON_MEMORY_ERROR;
        }

        avpicture_fill((AVPicture*) _inFrame, inFrame, inFormat.toFfmpeg(), inWidth, inHeight);
        avpicture_fill((AVPicture*) _outFrame, *outFrame, outFormat.toFfmpeg(), outWidth, outHeight);

        int r = sws_scale(_scaleContext, 
            _inFrame->data,
            _inFrame->linesize,
            0,
            inHeight,
            _outFrame->data,
            _outFrame->linesize);

        if (r != outHeight) {
            NEW_ERROR(E_COMMON_UNKNOWN_ERROR, "Tamanho da imagem de saída incorreto!");
            return E_COMMON_UNKNOWN_ERROR;
        }

        return E_OK;
    }
};