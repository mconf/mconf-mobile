#ifndef _ENCODE_VIDEO_H_
#define _ENCODE_VIDEO_H_

#include <QueueExtraDataVideo.h>
#include "Encode.h"
#include "EncodeVideoParams.h"

/** \brief Classe para codificação de vídeo
 *
 * \note Para DVVIDEO, os parâmetros \a width, \a height e \a pix_fmt devem corresponder aos
 *       de algum DVProfile. O tradicional é o 720x480 com PIX_FMT_YUV411P, que é o DVProfile 0
 *       no ffmpeg. A lista de profiles está em libavcodec/dvdata.c (dv_profiles[])
 */
class EncodeVideo : public Encode
{
private:
    virtual int open() {return E_ERROR;}; // inibe acesso à função do pai

protected:
    static const int NUMERATOR   = 1;   ///< Para inicializar \p time_base.num no AVCodecContext
    static const int NUMB        = 0;   ///< Número de quadros B

    EncodeVideoParams * _params;        ///< Para armazenar os parâmetros de codificação

    /** \brief Seta os parâmetros de \p _params no contexto de codificação  \p _codecCtx
     */
    void _SetContextParameters();

    QueueExtraDataVideo _UpdateExtraData(QueueExtraDataVideo * extra);

public:
    EncodeVideo();
    virtual ~EncodeVideo();

    /** \brief Inicializa as estruturas necessárias para codificação
     *  \param[in] params Parâmetros para codificação
     */
    int open(EncodeVideoParams * params);

    /** \brief Função padrão (herdada) para codificar um bloco de dados. Ver Encode::encode().
     *
     * Função herdada de Encode. Internamente chama a outra função encode(), que recebe
     * um AVFrame: EncodeVideo::encode(AVFrame * input, ...).
     */
    virtual int encode(uint8_t * input, unsigned int size, unsigned int timestamp,
                       queue_t * outQueue, QueueExtraData * extraData = NULL);

    /** \brief Codifica um frame (AVFrame) com dados e coloca na queue
     *  \param[in] input Dados (frame) a serem codificados
     *  \param[in] timestamp Timestamp dos dados ao serem colocados na queue
     *  \param[in] outQueue Queue onde os dados codificados serão colocados
     *  \param[in] extraData Dados extra (opcionais) para colocar na queue
     *  \return E_OK em caso de sucesso ou o código de erro gerado
     *
     * Difere da outra função encode() pois recebe um AVFrame como parâmetro.
     */
    int encode(AVFrame * input, unsigned int timestamp, queue_t * outQueue,
               QueueExtraData * extraData = NULL);

    /** \brief Finaliza a codificação, limpa estruturas internas
     */
    virtual void close();

    /** \brief Retorna uma referência aos parâmetros sendo usados na encode
     *  \return Parâmetros atuais da codificação de vídeo
     */
    EncodeVideoParams * getParams();

    /** \brief Seta os parâmetros de codificação
     *  \param[in] params Objeto com os parâmetros de codificação
     *  \return E_OK em caso de sucesso ou o código de erro gerado
     *
     * Dependendo dos parâmetros setados, a classe precisará reiniciar alguns objetos
     * necessários para a codificação (o codec especialmente).
     */
    int setParams(EncodeVideoParams * params);
};

#endif

