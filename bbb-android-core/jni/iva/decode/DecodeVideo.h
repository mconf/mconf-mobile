#ifndef _DECODE_VIDEO_H_
#define _DECODE_VIDEO_H_

#include <QueueExtraDataVideo.h>
#include "Decode.h"

/** \brief Classe para decodificação de vídeo
 *
 * \todo Decode usa pix_fmt padrão PIX_FMT_YUV420P. Ela usa a lib swscale para converter
 *       os dados para este padrão quando necessário. O ideal seria fazer essa conversão
 *       na placa de vídeo.
 */
class DecodeVideo : public Decode
{

private:
    IvaPixFmt _defaultPixFmt;           ///< Formato padrão dos pixels dos frames
    bool _needFrameI;                   ///< Indica se precisa esperar um frame I pra decodificar
    AVFrame * _tempFrame;               ///< Quadro ffmpeg temporário
    bool _deinterlace;                  ///< Indica a necessidade de desentrelaçar a imagem
    IvaPixFmt _lastPixFmt;              /**< Último pix_fmt decodificado. Utilizado para verificar quando
                                             há mudanças nos dados sendo recebidos. */
    uint16_t _lastVideoId;              /**< Último identificador de vídeo decodificado. Utilizado para
                                             verificar quando há mudanças nos dados sendo recebidos. */

    virtual int _PrepareContext();
    virtual bool _NeedRestart(QueueExtraData * extraData);

    /** \brief Indica se o quadro é classificado como quadro I ou não
     *  \param inbuf Buffer que contém o quadro que será consultado
     *  \param size Tamanho do buffer
     *  \return Retorna um valor booleano que indica se o quadro é I
     *  \retval true É um quadro I
     *  \retval false Não é um quadro I
     *
     *  Os ponteiros inbuf e size também podem ser alterados, pois pode haver
     *  a necessidade de alinhar o buffer ao início do quadro
     */
    bool _IsFrameI(uint8_t ** inbuf, uint32_t * size);

    QueueExtraDataVideo _UpdateExtraData(QueueExtraDataVideo * extra);

    int _PrepareFrame(AVFrame * inFrame, uint8_t ** buffer);

    void _ConvertPixFmt(AVFrame * inFrame, AVFrame * outFrame);

    /** \brief Faz o desentrelaçamento do frame \p inFrame e coloca resultado
     *         \p outFrame
     *  \return E_OK em caso de sucesso ou o código de erro gerado
     */
    void _Deinterlace(AVFrame * inFrame, AVFrame * outFrame);

public:
    DecodeVideo();
    ~DecodeVideo();

    /** \brief Inicializa a decodificação, inicializa estruturas internas
     */
    virtual int open(int codecId);

    /** \brief Indica a necessidade de desentrelaçamento dos frames de vídeo
     *  \param[in] value true em caso de desentrelaçamento, false (default) em caso contrário
     */
    void setDeinterlace(bool value);

    /** \brief Decodifica um bloco de dados. Ver Decode::decode().
     */
    virtual int decode(uint8_t * input, unsigned int size, unsigned int timestamp,
                       queue_t * outQueue, bool * gotFrame,
                       QueueExtraData * extraData = NULL);
};

#endif

