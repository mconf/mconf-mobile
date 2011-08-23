#ifndef _ENCODE_AUDIO_H_
#define _ENCODE_AUDIO_H_

#include <QueueExtraDataAudio.h>
#include "Encode.h"
#include "EncodeAudioParams.h"

/** \brief Classe para codificação de áudio
 */
class EncodeAudio : public Encode
{
private:
    virtual int open() {return E_ERROR;}; // inibe acesso à função do pai

protected:
    EncodeAudioParams * _params;    ///< Para armazenar os parâmetros de codificação

    /** \brief Seta os parâmetros de \p _params no contexto de codificação \p _codecCtx
     */
    void _SetContextParameters();

    QueueExtraDataAudio _UpdateExtraData(QueueExtraDataAudio * extra);

public:
    EncodeAudio();
    virtual ~EncodeAudio();

    /** \brief Inicializa as estruturas necessárias para codificação
     *  \param params Parâmetros para codificação
     */
    int open(EncodeAudioParams * params);

    /** \brief Função padrão (herdada) para codificar um bloco de dados. Ver Encode::encode().
     */
    virtual int encode(uint8_t * input, unsigned int size, unsigned int timestamp,
                       queue_t * outQueue, QueueExtraData * extraData = NULL);

    /** \brief Finaliza a codificação, limpa estruturas internas
     */
    virtual void close();

    /** \brief Retorna um ponteiro para os parâmetros sendo usados na encode
     *  \return Objeto com os parâmetros de codificação
     */
    EncodeAudioParams * getParams();

    /** \brief Retorna o tamanho dos frames. Só é funcional após chamada a EncodeAudio::open.
     *  \return 
     */
    int getFrameSize();

    /** \brief Seta os parâmetros de codificação
     *  \param[in] params Objeto com os parâmetros de codificação
     *  \return E_OK em caso de sucesso ou o código de erro gerado
     *
     * Dependendo dos parâmetros setados, a classe precisará reiniciar alguns objetos
     * necessários para a codificação (o codec especialmente).
     */
    int setParams(EncodeAudioParams * params);
};

#endif

