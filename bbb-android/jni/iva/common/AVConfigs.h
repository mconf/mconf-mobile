#ifndef _AV_CONFIGS_H_
#define _AV_CONFIGS_H_

#include <list>
#include <string>
using namespace std;

/** \brief Par (resolução, faixa de bitrates de vídeo) utilizado por AVConfigs
 */
class AVConfigsPairResBitrate
{
protected:
    pair<int,int> _resolution;      ///< Resolução no formato (largura,algura)
    pair<int,int> _bitrates;        ///< Faixa de bitrates de vídeo no formato (menor, maior)

public:
    AVConfigsPairResBitrate(pair<int,int> resolution, pair<int,int> bitrates);

    pair<int,int> getResolution() const;
    string getResolutionAsString() const;
    void setResolution(const pair<int,int> resolution);

    pair<int,int> getBitrates() const;
    void setBitrates(const pair<int,int> bitrates);

};

/** \brief Contém as configurações de áudio e vídeo suportadas pelo sistema.
 *
 * Ajuda na validação dos parâmetros de codificação/decodificação de áudio e vídeo e
 * tem funções para buscar a lista dos parâmetros aceitos.
 */
class AVConfigs
{
protected:
    list<AVConfigsPairResBitrate> _resAndBitrates;  ///< Lista de resoluções aceitas e bitrates de vídeo
    pair<int,int> _fps;                             ///< Range de valores de fps válidos
    list<int> _audioBitrates;                       ///< Lista de bitrates de áudio válidos
    list<int> _audioCodecs;                         ///< Lista de codecs de áudio
    list<int> _videoCodecs;                         ///< Lista de codecs de vídeo

public:
    AVConfigs();

    /** \brief Busca a lista de resoluções suportadas
     *  \return Lista de resoluções suportadas
     *
     * Cada resolução é um par: largura x altura
     */
    const list<pair<int,int> > getResolutions() const;

    /** \brief Verifica se uma resolução qualquer é válida
     *  \param[in] resolution Resolução a ser testada no formato <largura, altura>
     *  \return true se a resolução é válida
     */
    bool validateResolution(pair<int,int> resolution) const;

    /** \brief Verifica se uma resolução qualquer é válida
     *  \param[in] resolution Resolução a ser testada no formato "LarguraxAltura".
     *             Exemplo: "1280x800"
     *  \return true se a resolução é válida
     */
    bool validateResolution(string resolution) const;

    /** \brief Retorna a faixa de valores fps suportados
     *  \return Faixa de valores fps no formato:
                <menor valor (inclusive), maior valor (inclusive)>
     */
    pair<int,int> getFpsRange() const;

    /** \brief Verifica se um fps qualquer é válido
     *  \param[in] fps Valor do fps
     *  \return true se o fps é válido
     */
    bool validateFps(int fps) const;

    /** \brief Retorna a faixa de bitrates de áudio suportados
     *  \return Faixa de bitrates no formato:
                <menor valor (inclusive), maior valor (inclusive)>
     */
    /** \brief Busca a lista de bitrates de áudio suportados em kbit/s
     *  \return Lista de bitrates de áudio suportadas
     */
    const list<int>& getAudioBitrates() const;

    /** \brief Verifica se um bitrate de áudio qualquer é válido
     *  \param[in] bitrate Valor do bitrate de áudio
     *  \return true se o bitrate é válido
     */
    bool validateAudioBitrate(int bitrate) const;

    /** \brief Retorna a faixa de bitrates de vídeo suportados
     *  \return Faixa de bitrates no formato:
                <menor valor (inclusive), maior valor (inclusive)>
     */
    pair<int,int> getVideoBitrateRange(pair<int,int> resolution) const;

    /** \brief Verifica se um bitrate de vídeo qualquer é válido
     *  \param[in] bitrate Valor do bitrate de vídeo
     *  \return true se o bitrate é válido
     */
    bool validateVideoBitrate(pair<int,int> resolution, int bitrate) const;

    /** \brief Busca a lista de codecs de áudio suportados
     *  \return Lista de codecs de áudio suportados
     *
     * A lista contém os IDs dos codecs suportados. Esses IDs são definidos na common.
     */
    const list<int>& getAudioCodecs() const;

    /** \brief Verifica se um codec de áudio qualquer é suportado
     *  \param[in] codec Codec a ser testado
     *  \return true se o codec é válido
     */
    bool validateAudioCodec(int codec) const;

    /** \brief Busca a lista de codecs de vídeo suportados
     *  \return Lista de codecs de vídeo suportados
     *
     * A lista contém os IDs dos codecs suportados. Esses IDs são definidos na common.
     */
    const list<int>& getVideoCodecs() const;

    /** \brief Verifica se um codec de vídeo qualquer é suportado
     *  \param[in] codec Codec a ser testado
     *  \return true se o codec é válido
     */
    bool validateVideoCodec(int codec) const;

};

#endif

