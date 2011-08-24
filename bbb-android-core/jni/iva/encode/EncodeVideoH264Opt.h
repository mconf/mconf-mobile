#ifndef _ENCODE_VIDEO_H264_OPT_H_
#define _ENCODE_VIDEO_H264_OPT_H_

#define __STDC_LIMIT_MACROS     // Para forçar a definição do INT64_MAX em stdint.h
#include <stdint.h>             /* INT64_MAX */
extern "C" {
#include <libavcodec/avcodec.h>
#include <libavcodec/opt.h>
}
#include <float.h>              /* FLT_MIN, FLT_MAX */
#include <stddef.h>             // offsetof()

/** \brief Contém as estruturas necessárias para carregar parâmetros de codificação
 *         do H.264 no ffmpeg.
 *
 * Esta classe contém as definições das opções de configuração possíveis no ffmpeg
 * para a codificação com o H.264 (libx264). Estas são as opções disponíveis por linha de 
 * comando, que devem ser "traduzidas" para serem utilizadas com a API do ffmpeg.
 * A maioria das estruturas foram copiadas dos arquivos do ffmpeg pois não era possível
 * apenas dar um include nos arquivos -- eram  arquivos .c ou arquivos .h internos do ffmpeg,
 * e que não estão disponíveis no nosso deps. O que era possível apenas incluir foi incluído.
 * A estrutura base é a \p AVOption \p options[], que contém todas opções do ffmpeg linha de 
 * comando e contém informações para carregar essas opções no contexto do ffmpeg.
 *
 */
class EncodeVideoH264Opt
{
public:

    /** \brief Copiado de <libavutil/internal.h>
     */
    //#ifndef offsetof
    //#    define offsetof(T, F) ((unsigned int)((char *)&((T *)0)->F))
    //#endif

    /** \brief Definições copiadas de <libavcodec/options.c>
     */
    #define OFFSET(x) offsetof(AVCodecContext,x)
    #define DEFAULT 0
    #define V AV_OPT_FLAG_VIDEO_PARAM
    #define A AV_OPT_FLAG_AUDIO_PARAM
    #define S AV_OPT_FLAG_SUBTITLE_PARAM
    #define E AV_OPT_FLAG_ENCODING_PARAM
    #define D AV_OPT_FLAG_DECODING_PARAM
    #define AV_CODEC_DEFAULT_BITRATE 200*1000

    /** \brief Estrutura que contém todas as opções possíveis no ffmpeg (copiada de <libavcodec/options.c>)
     * 
     * Esta é a estrutura base necessária para fazer parse dos parâmetros do ffmpeg. Exatamente igual
     * a estrutura copiada de <libavcodec/options.c>. Inicialização é feita no arquivo EncodeVideoH264Opt.cpp
     */
    static const AVOption options[];

};

#endif

