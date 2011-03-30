#include <CommonLeaks.h>
#include "Decode.h"
#include <CommonLeaksCpp.h>

Decode::Decode()
{
    _flags = FLAG_NONE;
    _codecId = COMMON_CODEC_NONE;
    _queueIn = NULL;
    _queueOut = NULL;
    _consumer = NULL;
    _codec = NULL;
    _codecCtx = NULL;
    _thread = NULL;
    _bufLeft = NULL;
    _bufLeftSize = 0;

    // inicializa o ffmpeg
    avcodec_init();
    avcodec_register_all();
}

Decode::~Decode()
{
    close();
}

int Decode::_PrepareContext()
{
    _codecCtxMutex.lock();

    _codecCtx = avcodec_alloc_context();
    if (!_codecCtx) {
        _codecCtxMutex.unlock();
        NEW_ERROR(E_COMMON_MEMORY_ERROR, "Na chamada avcodec_alloc_context");
        return E_COMMON_MEMORY_ERROR;
    }
    _codecCtx->extradata_size = 0;

    _codecCtxMutex.unlock();
    return E_OK;
}

int Decode::_PrepareCodec(int codecId)
{
    _codec = avcodec_find_decoder(CodecClass::ffmpegCodecId(codecId));
    if (!_codec) {
        return E_DECODE_CODEC_NOT_FOUND;
    }
    return E_OK;
}

int Decode::_BindCodecToContext()
{
    _codecCtxMutex.lock();

    int err = avcodec_open(_codecCtx, _codec); // associa o codec no contexto
    if (err < 0) {
        _codecCtxMutex.unlock();
        return E_DECODE_CODEC_ERROR;
    }
    if (_codec->capabilities & CODEC_CAP_TRUNCATED) {
        _codecCtx->flags |= CODEC_FLAG_TRUNCATED;
    }

    _codecCtxMutex.unlock();
    return E_OK;
}

int Decode::_Open(int codecId)
{
    int err;

    // guarda o id do codec
    _codecId = codecId;

    // prepara o contexto e seta os parâmetros nele
	err = _PrepareContext();
	/*_codecCtxMutex.lock();

	    _codecCtx = avcodec_alloc_context();
	    if (!_codecCtx) {
	        _codecCtxMutex.unlock();
	        NEW_ERROR(E_COMMON_MEMORY_ERROR, "Na chamada avcodec_alloc_context");
	        return E_COMMON_MEMORY_ERROR;
	    }
	    _codecCtx->extradata_size = 0;

	    _codecCtxMutex.unlock();
	*/

	err = E_OK;
	if (err != E_OK) {
        return err;
    }

    // prepara o codec
    err = _PrepareCodec(_codecId);
    if (err != E_OK) {
        return err;
    }

    // associa o codec no contexto
    err = _BindCodecToContext();
    if (err != E_OK) {
        return err;
    }

    return err;
}

int Decode::open(int codecId)
{
    int err;

    if (_flags & FLAG_OPENED) {
        NEW_ERROR(E_DECODE_ALREADY_OPENED, "");
        return E_DECODE_ALREADY_OPENED;
    }

    err = _Open(codecId);
    if (err != E_OK) {
        return err;
    }

    _flags |= FLAG_OPENED; // liga flag de opened

    return E_OK;
}

void Decode::_Close()
{
    if (_codecCtx) {
        avcodec_close(_codecCtx);
        av_free(_codecCtx);
        _codecCtx = NULL;
    }
}

void Decode::close()
{
    // para a thread se ela está rodando
    if (_flags & FLAG_STARTED) {
        stop();
    }

    _Close();

    _flags &= ~FLAG_OPENED; // desliga flag de opened
}

int Decode::setCodec(int codecId)
{
    int err;

    _codecId = codecId; // guarda o id do codec

    // se já deu um open() tem que reiniciar algumas coisas...
    if (_flags & FLAG_OPENED) {

        avcodec_close(_codecCtx);

        // prepara o codec
        err = _PrepareCodec(_codecId);
        if (err != E_OK) {
            return err;
        }

        // associa o codec no contexto
        err = _BindCodecToContext();
        if (err != E_OK) {
            return err;
        }
    }

    return E_OK;
}

int Decode::start(queue_t * queueIn, queue_t * queueOut)
{
    if (!(_flags & FLAG_OPENED)) {
        NEW_ERROR(E_DECODE_NOT_OPENED, "");
        return E_DECODE_NOT_OPENED;
    }
    if (_flags & FLAG_STARTED) {
        NEW_ERROR(E_DECODE_ALREADY_STARTED, "");
        return E_DECODE_ALREADY_STARTED;
    }
    if (!queueIn || !queueOut) {
        NEW_ERROR(E_COMMON_NULL_PARAMETER, "queueIn | queueOut");
        return E_COMMON_NULL_PARAMETER;
    }

    // guarda as queues
    _queueIn = queueIn;
    _queueOut = queueOut;

    _flags |= FLAG_STARTED; // liga flag indicando que tá started

    // registra o consumidor da queue. registra ele aqui para não perder nenhum
    // dado da queue de entrada entre o start() e o inicio real da thread
    _consumer = queue_registerConsumer(_queueIn);

    // inicia a thread
    _flags |= FLAG_THREAD_RUN; // liga flag que indica que a thread está rodando
    _thread = new Thread<Decode>(this, &Decode::_ThreadDecode);
    _thread->run(NULL, true);

    return E_OK;
}

int Decode::stop()
{
    if (!(_flags & FLAG_STARTED)) {
        NEW_ERROR(E_ENCODE_NOT_STARTED, "");
        return E_ENCODE_NOT_STARTED;
    }

    _flags &= ~FLAG_STARTED; // desliga flag indicando que tá started

    // para a thread
    _flags &= ~FLAG_THREAD_RUN;  // desliga flag para que a thread acabe
    queue_broadcast(_queueIn);   // para sair do dequeueCond
    if (_thread->isRunning()) {
        _thread->join(NULL);
    }
    if (_thread) {
        delete _thread;
        _thread = NULL;
    }

    return E_OK;
}

bool Decode::_NeedRestart(QueueExtraData * extraData)
{
    return false;
}

void * Decode::_ThreadDecode(void * param)
{
    QueueExtraData * extraData;
    uint32_t timestamp, inbufSize;
    uint8_t * inbuf;
    uint8_t * bufLeft = NULL;
    uint8_t * bufFinal = NULL;
    bool bufFinalFree = false;
    int ret;
    bool gotFrame;

    while (_flags & FLAG_THREAD_RUN) {
        if (queue_dequeueCond(_consumer, &inbuf, &inbufSize, &timestamp, &extraData) != E_OK) {
            continue;
        }
        // se os dados da queue tinham tamanho 0, só passa adiante o extraData
        if (inbufSize == 0) {
            if (extraData) {
                queue_enqueue(_queueOut, NULL, 0, timestamp, extraData);
            }
            queue_free(_consumer);
            continue;
        }
        if (!(_flags & FLAG_THREAD_RUN)) break;

        // se mudou alguma configuração, reinicia a codificação
        if (_NeedRestart(extraData)) {
            _Close();
            _Open(_codecId);
        }

        // se tem um buffer guardado do loop anterior, tem que colocá-lo no início do buffer atual
        _RestoreBufferLeft(&inbuf, &inbufSize);

        // codifica e coloca os dados na queue...
        int usedSize = 0;
        while ((uint32_t)usedSize < inbufSize) {
            ret = decode(inbuf + usedSize, inbufSize - usedSize,
                         timestamp, _queueOut, &gotFrame, extraData);
            if (ret == -1) {
                break;
            }
            usedSize += ret;
        }

        // se não pegou nenhum frame na última passada, tem que guardar os dados
        // que "sobraram" no buffer atual e colocar eles no início do próximo buffer
        if (!gotFrame) {
            _StoreBufferLeft(inbuf + usedSize, inbufSize - usedSize);
        }

        queue_free(_consumer);
    }

    queue_unregisterConsumer(&_consumer);

    return NULL;
}

void Decode::_StoreBufferLeft(uint8_t * buffer, uint32_t buffersize)
{
    _bufLeftSize = buffersize;
    if (_bufLeftSize) {
        if (_bufLeft) {
            free(_bufLeft);
        }
        _bufLeft = (uint8_t *)malloc(_bufLeftSize);
        memcpy(_bufLeft, buffer, _bufLeftSize);
    }
}

void Decode::_RestoreBufferLeft(uint8_t ** buffer, uint32_t * buffersize)
{
    if (_bufLeft) {
        // cria um novo buffer pra guardar todos os dados
        uint32_t bufFinalSize = *buffersize + _bufLeftSize;
        uint8_t * bufFinal = (uint8_t *)malloc(bufFinalSize);

        // copia dados que sobraram anteriormente + dados do buffer atual
        memcpy(bufFinal, _bufLeft, _bufLeftSize);
        memcpy(bufFinal + _bufLeftSize, *buffer, *buffersize);
        *buffer = bufFinal;
        *buffersize = bufFinalSize;
        // obs: não precisa dar free em 'buffer' pq ele veio do queue_dequeue (queue liberará ele)

        // libera o buffer que sobrou
        free(_bufLeft);
        _bufLeft = NULL;
        _bufLeftSize = 0;
    }
}

bool Decode::isDecoding() const
{
    return (_flags & FLAG_THREAD_RUN)?true:false;
}
