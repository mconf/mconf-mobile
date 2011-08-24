#ifndef _ENCODE_AUDIO_PARAMS_H_
#define _ENCODE_AUDIO_PARAMS_H_

extern "C" {
#include <libavcodec/avcodec.h>
}

/** \brief Classe para armazenar os parâmetros para codificação de áudio
 */
class EncodeAudioParams
{
protected:
    int _codec;                     ///< Id do codec (e.g. mp2)
    int _channels;                  ///< Número de canais
    int _bitRate;                   ///< Taxa de bits (em kbps)
    int _sampleRate;                ///< Taxa de amostragem (e.g. 44100 Hz)

public:
    EncodeAudioParams();

    /** \brief Atribui o codec nos parâmetros de áudio
     *  \param[in] value Identificador do codec a ser utilizado
     */
    void setCodec(int value);

    /** \brief Atribui o número de canais nos parâmetros de áudio
     *  \param[in] value Número de canais
     */
    void setChannels(int value);

    /** \brief Atribui o bitrate nos parâmetros de áudio
     *  \param[in] value Valor do bitrate em kbit/s
     */
    void setBitRate(int value);

    /** \brief Atribui a taxa de amostragem nos parâmetros de áudio
     *  \param[in] value Taxa de amostragem em bits
     */
    void setSampleRate(int value);

    /** \brief Busca o codec dos parâmetros de áudio
     *  \return Identificador do codec atual
     */
    int getCodec();

    /** \brief Busca o número de canais dos parâmetros de áudio
     *  \return Número de canais atual
     */
    int getChannels();

    /** \brief Busca o bitrate dos parâmetros de áudio
     *  \return Bitrate atual em kbit/s
     */
    int getBitRate();

    /** \brief Busca a taxa de amostragem dos parâmetros de áudio
     *  \return Taxa de amostragem atual em bits
     */
    int getSampleRate();
};

#endif

