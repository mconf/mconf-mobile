#ifndef _ENCODE_VIDEO_H264_PRESETS_H_
#define _ENCODE_VIDEO_H264_PRESETS_H_

#include <string>
#include <vector>
using namespace std;

#include "EncodeVideoH264Opt.h"

/** \brief Carrega os presets para codificação H.264 (libx264)
 */
class EncodeVideoH264Presets
{
private:
    int _Parse(istream & myfile, AVCodecContext * context);
    int _ParseLine(string line, AVCodecContext * context);
    AVOption * _FindOption(string name);

    template <typename T> int _SetValue(string value, void * pointer);
    int _GetFlags(string value, int * retValue);
    int _BreakFlags(string value, vector<string> & vec);
    template <typename T> int _CheckFlagOrValueAndSet(string value, void * pointer);

public:
    EncodeVideoH264Presets();

    /** \brief Faz a leitura de um arquivo de preset e seta as opções no contexto
     *  \param[in] filename Nome do arquivo de preset a ser lido
     *  \param[in,out] Contexto do ffmpeg onde serão configurados os dados lidos do preset
     *  \return E_OK em caso de sucesso ou o código de erro gerado
     */
    int parse(string filename, AVCodecContext * context);

};

#endif

