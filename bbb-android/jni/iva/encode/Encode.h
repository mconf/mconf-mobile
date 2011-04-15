#ifndef _ENCODE_H_
#define _ENCODE_H_

extern "C" {
#include <libavcodec/avcodec.h>
}
#include <queue.h>
#include <Thread.h>
#include <Mutex.h>

/** \brief Classe base para codificação de áudio/vídeo
 *
 * Contém as funcionalidades básicas comuns na codificação de áudio
 * e na codificação de vídeo.
 * Dados podem ser codificados de duas formas:
 *  - bloco-a-bloco: usuário da lib faz chamadas à função encode() para codificar um
 *                   bloco de dados
 *  - modo automático: usuário da lib passa uma queue para a encode e chama a função
 *                     start(). A encode cria uma thread que vai buscando os dados
 *                     dessa queue de entrada, codificando-os e os colocando na queue
 *                     de saída. Chamar a função stop() para parar a thread.
 */
class Encode
{
private:
    void * _ThreadEncode(void * param);

protected:
    static const int FLAG_NONE       = 0;      ///< Flag zerada, inicializada
    static const int FLAG_OPENED     = 0x0001; ///< Processo iniciado, feita chamada a open()
    static const int FLAG_STARTED    = 0x0002; ///< Processo iniciado, já criada a thread de codificação
    static const int FLAG_THREAD_RUN = 0x0004; ///< Flag para controlar a execução da thread

    AVCodec * _codec;               ///< Codec no ffmpeg \see Função avcodec_find_encoder()
    AVCodecContext * _codecCtx;     ///< Contexto de codificação no ffmpeg
    Mutex _codecCtxMutex;           ///< Mutex para uso do \p _codecCtx
    int _numProcessor;              ///< Número de processadores da máquina
    int _flags;                     ///< Flags para controlar estado atual da lib

    queue_t * _queueIn;             ///< Queue de onde virão os dados para codificar em modo automático
    queue_t * _queueOut;            ///< Queue onde serão colocados os dados codificados em modo automático
    queue_consumer_t * _consumer;   ///< Consumidor para a \p _queueIn
    Thread<Encode> * _thread;       ///< Thread para codificar os dados automaticamente

    /** \brief Prepara o contexto de codificação padrão da classe
     */
    int _PrepareContext();

    /** \brief Prepara o codec padrão da classe
     *  \param codecId Identificador do codec que deve ser usado
     */
    int _PrepareCodec(int codecId);

    /** \brief Liga o codec ao contexto (devem ter sido previamente criados)
     */
    int _BindCodecToContext();

public:
    Encode();
    virtual ~Encode();

    /** \brief Inicializa a codificação, inicializa estruturas internas
     */
    virtual int open() = 0;

    /** \brief Finaliza a codificação, limpa estruturas internas
     */
    virtual void close();

    /** \brief Função base para codificação de um bloco de dados
     *  \param[in] input Buffer com os dados de entrada
     *  \param[in] size Tamanho (em bytes) dos dados de entrada
     *  \param[in] timestamp Timestamp dos dados
     *  \param[in] outQueue Queue onde serão colocados os dados codificados
     *  \param[in] extraData Dados extra (opcionais) para colocar na queue
     *  \return -1 em caso de erro ou o número de bytes usados para codificar o bloco
     *  de dados passado em \p input.
     *
     * Função pode ser chamada externamente para codificar um bloco de dados e também
     * é chamada internamente na thread que faz a codificação automática.
     * Esta função não é implementada nesta classe, deve ser implementada pelos descendentes.
     * \see Encode::start
     */
    virtual int encode(uint8_t * input, unsigned int size, unsigned int timestamp,
                       queue_t * outQueue, QueueExtraData * extraData = NULL) = 0;

    /** \brief Inicializa o processo de codificação automática
     *  \param[in] queueIn Queue com os frames de entrada a serem codificados
     *  \param[in] queueOut Queue onde serão colocados os frames codificados
     *  \return E_OK se teve sucesso (e criou a thread com sucesso) ou o código de erro
     *
     * Cria uma thread que consome os frames de \p queueIn, codifica esses frames e
     * coloca o resultado em \p queueOut.
     */
    int start(queue_t * queueIn, queue_t * queueOut);

    /** \brief Para o processo de codificação automática (iniciado em Encode::start)
     *  \return E_OK se teve sucesso (se parou a thread com sucesso) ou o código de erro
     */
    int stop();

    /** \brief Indica se está em proecsso de codificação automática (iniciado em Encode::start)
     *  \return true se está codificando automaticamente (thread está criada e executando)
     */
    bool isEncoding() const;

    bool isOpened() const;

    /**
     *  Função estática responsável por fazer o multithread no FFmpeg funcionar.
     */
    static int lockManager(void** mutex, enum AVLockOp op);

};

#endif

