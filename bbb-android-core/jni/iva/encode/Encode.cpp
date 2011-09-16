#include <CommonLeaks.h>
extern "C" {
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
}

#ifdef _WIN32
#define _WINSOCKAPI_   /* Prevent inclusion of winsock.h in windows.h */
#include <windows.h>
#endif

#include "Encode.h"
#include <CommonLeaksCpp.h>

Encode::Encode()
    : _flags(FLAG_NONE)
    , _codec(NULL)
    , _codecCtx(NULL)
    , _thread(NULL)
    , _queueIn(NULL)
    , _queueOut(NULL)
    , _consumer(NULL)
{
    // inicializa o ffmpeg
    avcodec_init();
    av_register_all();

    int r = av_lockmgr_register(&Encode::lockManager);
    if (r)
        NEW_ERROR(E_COMMON_UNKNOWN_ERROR, "FFmpeg av_lockmgr_register não funcionou");

    // informações do sistema
#ifdef _MSC_VER
    SYSTEM_INFO winSystem;
    GetSystemInfo(&winSystem);
    _numProcessor = winSystem.dwNumberOfProcessors;
#else
    _numProcessor = 1;
#endif
}

Encode::~Encode()
{
    close();

    av_lockmgr_register(NULL);
}

int Encode::_PrepareContext()
{
    int err;

    _codecCtxMutex.lock();

    _codecCtx = avcodec_alloc_context();
    if (!_codecCtx) {
        _codecCtxMutex.unlock();
        return E_COMMON_MEMORY_ERROR;
    }
    if (_numProcessor > 1) {
        err = avcodec_thread_init(_codecCtx, _numProcessor - 1);
        if (err) {
            _codecCtxMutex.unlock();
            return E_ENCODE_MULTITHREAD;
        }
    }

    _codecCtxMutex.unlock();
    return E_OK;
}

int Encode::_PrepareCodec(int codecId)
{
    _codec = avcodec_find_encoder(CodecClass::ffmpegCodecId(codecId));
    if (!_codec) {
        return E_ENCODE_INCORRECT_CODEC_TYPE;
    }
    return E_OK;
}

int Encode::_BindCodecToContext()
{
    _codecCtxMutex.lock();
    int err = avcodec_open(_codecCtx, _codec); // associa o codec no contexto
    if (err) {
        _codecCtxMutex.unlock();
        return E_ENCODE_CODEC;
    }
    _codecCtxMutex.unlock();
    return E_OK;
}

void Encode::close()
{
    // para a thread se ela está rodando
    if (_flags & FLAG_THREAD_RUN) {
        stop();
    }

    _codecCtxMutex.lock();
    if (_codecCtx) {
        avcodec_close(_codecCtx);
        _codecCtx = NULL;
    }
    _codecCtxMutex.unlock();
    _flags &= ~FLAG_OPENED; // desliga flag de opened
}

int Encode::start(queue_t * queueIn, queue_t * queueOut)
{
    if (!(_flags & FLAG_OPENED)) {
        NEW_ERROR(E_ENCODE_NOT_OPENED, "");
        return E_ENCODE_NOT_OPENED;
    }
    if (_flags & FLAG_STARTED) {
        NEW_ERROR(E_ENCODE_ALREADY_STARTED, "");
        return E_ENCODE_ALREADY_STARTED;
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
    _thread = new Thread<Encode>(this, &Encode::_ThreadEncode);
    _thread->run(NULL, true);

    return E_OK;
}

int Encode::stop()
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

void * Encode::_ThreadEncode(void * param)
{
    QueueExtraData * extraData;
    uint32_t timestamp, inbufSize;
    uint8_t * inbuf;
    int ret;

    while (_flags & FLAG_THREAD_RUN) {
        if (queue_dequeueCond(_consumer, &inbuf, &inbufSize, &timestamp, &extraData) != E_OK) {
            continue;
        }
        // se os dados da queue tinham tamanho 0, só passa adiante o extraData
        if (inbufSize == 0) {
            queue_free(_consumer);
            uint8_t * emptyData = (uint8_t *)queue_malloc(sizeof(uint8_t));
            if (queue_enqueue(_queueOut, emptyData, 0, timestamp, extraData) != E_OK) {
                queue_dealloc(emptyData);
            }
            continue;
        }
        if (!(_flags & FLAG_THREAD_RUN)) {
            queue_free(_consumer);
            break;
        }

        // codifica e coloca os dados na queue...
        ret = encode(inbuf, inbufSize, timestamp, _queueOut, extraData);
        //if (ret == -1) {
            //NEW_ERROR(E_ENCODE, "Erro durante codificação");
        //}
        queue_free(_consumer);
    }

    queue_unregisterConsumer(&_consumer);

    return NULL;
}

bool Encode::isEncoding() const
{
    return (_flags & FLAG_THREAD_RUN)?true:false;
}

bool Encode::isOpened() const 
{ 
    return (_flags & FLAG_OPENED)? true: false; 
}

int Encode::lockManager(void** mutex, enum AVLockOp op) {
    switch(op) {
        case AV_LOCK_CREATE: *mutex = new Mutex(); break;
        case AV_LOCK_OBTAIN: ((Mutex*)*mutex)->lock(); break;
        case AV_LOCK_RELEASE: ((Mutex*)*mutex)->unlock(); break;
        case AV_LOCK_DESTROY: delete (Mutex*)*mutex; break;
        default: return 1;
    }
    return 0;
}
