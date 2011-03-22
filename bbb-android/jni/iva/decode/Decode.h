/** \file Decode.h
 *  \brief Descritor da lib decode
 *  \author Felipe
 *
 *  Contém a declaração das classes utilizadas na decode, além de todos os
 *  includes que a biblioteca necessita.
 */

#ifndef __DECODE_H_
#define __DECODE_H_

#include <common.h>
#include <QueueExtraData.h>
#include <Thread.h>
#include <Mutex.h>
#include <queue.h>

extern "C" {
#include <libavcodec/avcodec.h>
};

#define DECODE_FLAG_INITIALIZED     0x0001  ///< Indica se a classe inicializada corretamente
#define DECODE_FLAG_STARTED         0x0002  ///< Indica se o processo de decodificação iniciou
#define DECODE_FLAG_THREAD_RUNNING  0x0004  ///< Indica se a thread de decodificação foi disparada

/** \brief Classe com funções básicas para decodificação
 *
 *  É especializada por DecodeVideo e DecodeAudio.
 */
class Decode
{
private:
    void * _ThreadDecode(void * param);

protected:
    static const int FLAG_NONE       = 0;      ///< Flag zerada, inicializada
    static const int FLAG_OPENED     = 0x0001; ///< Processo iniciado, feita chamada a open()
    static const int FLAG_STARTED    = 0x0002; ///< Processo iniciado, já criada a thread de decodificação
    static const int FLAG_THREAD_RUN = 0x0004; ///< Flag para controlar a execução da thread

    int _codecId;                           ///< Identificador do codec
    AVCodec * _codec;                       ///< codec utilizado na decodificação
    Mutex _codecCtxMutex;                   ///< Mutex para uso do \p _codecCtx
    AVCodecContext * _codecCtx;             ///< contexto de decodificação do ffmpeg

    queue_consumer_t * _consumer;           ///< consumidor da fila da stream codificada
    queue_t * _queueIn,                     ///< fila de entrada (encoded)
            * _queueOut;                    ///< fila de saída (decoded)
    Thread<Decode> * _thread;               ///< Thread para decodificar os dados automaticamente

    uint16_t _flags;                        ///< Flags para controlar estado atual da lib

    uint8_t * _bufLeft;                     ///< Guarda dados que "sobraram" no loop de decodificação
    uint32_t _bufLeftSize;                  ///< Tamanho (em bytes) dos dados em \p _bufLeft

    void _Close();
    int _Open(int codecId);
    virtual bool _NeedRestart(QueueExtraData * extraData);

    /** \brief Guarda dados no buffer \p _bufLeft
     *  \param[in] buffer Dados que serão guardados
     *  \param[in] buffersize Tamanho (em bytes) dos dados em \p buffer
     */
    void _StoreBufferLeft(uint8_t * buffer, uint32_t buffersize);

    /** \brief Restaura dados a partir do \p _bufLeft
     */
    void _RestoreBufferLeft(uint8_t ** buffer, uint32_t * buffersize);

    /** \brief Prepara o contexto de decodificação padrão da classe
     */
    virtual int _PrepareContext();

    /** \brief Prepara o codec padrão da classe
     *  \param codecId Identificador do codec que deve ser usado
     */
    int _PrepareCodec(int codecId);

    /** \brief Liga o codec ao contexto (devem ter sido previamente criados)
     */
    int _BindCodecToContext();

public:
    /** \brief Construtor padrão da classe
     *
     * \warning Classe não pode ser instanciada, é abstrata.
     * Utilize os construtores das classes herdadas
     */
    Decode();

    virtual ~Decode();

    /** \brief Inicializa a decodificação, inicializa estruturas internas
     *  \param[in] codecId Identificador do codec que deve ser utilizado
     *  \return E_OK em caso de sucesso ou o código de erro gerado
     */
    virtual int open(int codecId);

    /** \brief Finaliza a decodificação, finaliza estruturas internas
     */
    void close();

    /** \brief Seta o codec para decodificação
     *  \param[in] codecId Identificador do codec
     *
     * Para modificar o codec a lib precisa reiniciar algumas estruturas internas.
     * 
     * \note Mesmo que a lib esteja em modo automático (veja Decode::start), a mudança é
     * instantânea. Ou seja, se existem 5 frames na queue de entrada quando esta função é
     * chamada, o codec será mudado e esses 5 frames serão decodificados com o novo codec.
     */
    int setCodec(int codecId);

    /** \brief Inicializa o processo de decodificação automática
     *  \param[in] queueIn Queue com os frames de entrada a serem codificados
     *  \param[in] queueOut Queue onde serão colocados os frames codificados
     *  \return E_OK se teve sucesso (e criou a thread com sucesso) ou o código de erro
     *
     * Cria uma thread que consome os frames de \p queueIn, codifica esses frames e
     * coloca o resultado em \p queueOut.
     */
    int start(queue_t * queueIn, queue_t * queueOut);

    /** \brief Para o processo de decodificação automática (iniciado em Decode::start)
     *  \return E_OK se teve sucesso (se parou a thread com sucesso) ou o código de erro
     */
    int stop();

    /** \brief Decodifica um bloco de dados
     *  \param[in] input Buffer com os dados de entrada
     *  \param[in] size Tamanho (em bytes) dos dados de entrada
     *  \param[in] timestamp Timestamp associado ao buffer
     *  \param[in] outQueue Fila na qual o dado decodificado deve ser colocado
     *  \param[out] gotFrame Indica se for decodificado um frame inteiro ou não
     *  \param[in] extraData Dados extra (opcionais) para colocar na queue
     *  \return Retorna o número de bytes de \p input que foram decodificados. Caso não tenha pego um
     *          frame completo, retorna 0. Em caso de erro, retorna -1.
     *  \retval 0 Indica que aconteceu erro na função.
     *  \retval -1 Indica que aconteceu erro na função.
     *
     * Função pode ser chamada externamente para decodificar um bloco de dados e também
     * é chamada internamente na thread que faz a decodificação automática.
     * Esta função não é implementada nesta classe, deve ser implementada pelos descendentes.
     * \see Decode::start()
     *
     * A função percorre os dados de \p input até conseguir decodificar um frame completo. Ao fazer isso,
     * coloca o frame na queue, seta \p gotFrame para true e retorna o número de bytes de \p input que foram
     * usados para gerar esse frame. Se percorreu todo o buffer e não encontrou nenhum frame, retorna 0 e
     * seta \p gotFrame como false. Se algum erro ocorrer no processo, retorna -1.
     *
     * \warning Função pode retornar um valor menor do que o tamanho total do buffer (\p size).
     * Pode ser que apenas uma parte do buffer de entrada \p input seja utilizada para
     * decodificar os dados.
     */
    virtual int decode(uint8_t * input, unsigned int size, unsigned int timestamp,
                       queue_t * outQueue, bool * gotFrame,
                       QueueExtraData * extraData = NULL) = 0;

    bool isDecoding() const;
};

#endif

