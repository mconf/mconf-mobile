#ifndef _QUEUE_EXTRA_DATA_H_
#define _QUEUE_EXTRA_DATA_H_

/** \brief Dados adicionais que podem ser associados a buffers da queue
 *
 * Esta classe não contém nenhum dado em si, é apenas a classe base para as
 * classes que contém os dados específicos de cada tipo de queue, como a
 * QueueExtraDataVideo e QueueExtraDataAudio.
 */
class QueueExtraData
{
public:
    /** \brief Enumeração contendo todos os tipos de dados extra possíveis
     */
    enum QueueExtraDataType {
        EXTRA_DATA_UNDEFINED,       ///< Tipo não definido
        EXTRA_DATA_AUDIO,           ///< Dados extra para áudio
        EXTRA_DATA_VIDEO            ///< Dados extra para vídeo
    };

    QueueExtraData(void);
    QueueExtraData(const QueueExtraData &src);
    virtual ~QueueExtraData(void);

    /** \brief Retorna o tipo dos dados extra
     */
    virtual QueueExtraDataType getType() {return EXTRA_DATA_UNDEFINED;};

    /** \brief Cria um novo objeto igual a este
     */
    virtual QueueExtraData * clone() {return new QueueExtraData();};

};

#endif
