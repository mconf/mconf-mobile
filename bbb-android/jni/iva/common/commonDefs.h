#ifndef _COMMON_DEFS_H_
#define _COMMON_DEFS_H_

#define COMMON_SUITE_NUM_PREVIEWS               6
#define COMMON_INVALID_PORT                     0
#define COMMON_INVALID_IP                       "0.0.0.0"

#define COMMON_CHAT_TEXT_BOX_LEN                200


// Definições para os codecs de áudio e vídeo

#define COMMON_CODEC_NONE                   0        ///< Codec não especificado
#define COMMON_CODEC_VIDEO_MPEG2            10       ///< Codec de vídeo: MPEG-2
#define COMMON_CODEC_VIDEO_MPEG4            11       ///< Codec de vídeo: MPEG-4
#define COMMON_CODEC_VIDEO_H264             12       ///< Codec de vídeo: H.264
#define COMMON_CODEC_VIDEO_DV25             13       ///< Codec de vídeo: DV25
#define COMMON_CODEC_VIDEO_FLV              14       ///< Codec de vídeo: FLV
#define COMMON_CODEC_AUDIO_MP2              20       ///< Codec de áudio: MP2
#define COMMON_CODEC_AUDIO_AAC              21       ///< Codec de áudio: AAC

#define COMMON_CODEC_VIDEO_MIN_BITRATE      800     ///< Valor mais baixo para o bitrate de vídeo (kbit/s)
#define COMMON_CODEC_VIDEO_MAX_BITRATE      4000    ///< Valor mais alto para o bitrate de vídeo (kbit/s)
#define COMMON_CODEC_VIDEO_MIN_FPS          2       ///< Menor fps permitido no sistema
#define COMMON_CODEC_VIDEO_MAX_FPS          60      ///< Maior fps permitido no sistema
#define COMMON_CODEC_VIDEO_MIN_WIDTH        180     ///< Menor largura de vídeo permitida
#define COMMON_CODEC_VIDEO_MAX_WIDTH        1440    ///< Maior largura de vídeo permitida
#define COMMON_CODEC_VIDEO_MIN_HEIGHT       120     ///< Menor altura de vídeo permitida
#define COMMON_CODEC_VIDEO_MAX_HEIGHT       960     ///< Maior altura de vídeo permitida
#define COMMON_CODEC_AUDIO_MIN_BITRATE      32      ///< Valor mais baixo para o bitrate de áudio (kbit/s)
#define COMMON_CODEC_AUDIO_MAX_BITRATE      192     ///< Valor mais alto para o bitrate de áudio (kbit/s)
#define COMMON_CODEC_AUDIO_MIN_VOLMUTE      1       ///< Valor mais baixo para o volume do auto-mute no áudio
#define COMMON_CODEC_AUDIO_MAX_VOLMUTE      10      ///< Valor mais alto para o volume do auto-mute no áudio


// Valores padrão para áudio e vídeo

#define COMMON_AUDIO_DEFAULT_CODEC            COMMON_CODEC_AUDIO_MP2    ///< Codec padrão de áudio
#define COMMON_AUDIO_DEFAULT_BITRATE          128000                    ///< Bitrate padrão de áudio (em bit/s)
#define COMMON_AUDIO_DEFAULT_CHANNELS         2                         ///< Número de canais de áudio
#define COMMON_AUDIO_DEFAULT_BITSAMPLE        16                        ///< Bitsample de áudio
#define COMMON_AUDIO_DEFAULT_FREQUENCY        44100                     ///< Frequência de áudio
#define COMMON_VIDEO_DEFAULT_CODEC            COMMON_CODEC_VIDEO_MPEG4  ///< Codec padrão de vídeo
#define COMMON_VIDEO_DEFAULT_BITRATE          1400000                   ///< Bitrate padrão de vídeo (em bit/s)
#define COMMON_VIDEO_DEFAULT_WIDTH            720                       ///< Largura padrão dos vídeos
#define COMMON_VIDEO_DEFAULT_HEIGHT           480                       ///< Altura padrão dos vídeos
#define COMMON_VIDEO_DEFAULT_GOP              12                        ///< GOP padrão para cod de vídeo
#define COMMON_VIDEO_DEFAULT_FPS              30                        ///< FPS padrão dos vídeos


#endif
