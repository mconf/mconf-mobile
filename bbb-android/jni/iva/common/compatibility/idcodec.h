#ifndef _IDCODEC_H_
#define _IDCODEC_H_

#define CODEC_VIDEO	0
#define CODEC_AUDIO 1

// CODEC IDS
#define CODEC_VIDEO_MPEG2   0
#define CODEC_VIDEO_MPEG4   1
#define CODEC_VIDEO_H264    2
#define CODEC_AUDIO_MP2     3
#define CODEC_AUDIO_AAC     4
#define CODEC_AUDIO_VORBIS  5
#define CODEC_VIDEO_DV25    6

#define CODEC_VIDEO_MIN_BITRATE     800     ///< Valor mais baixo para o bitrate de vídeo (kbit/s)
#define CODEC_VIDEO_MAX_BITRATE     4000    ///< Valor mais alto para o bitrate de vídeo (kbit/s)
#define CODEC_VIDEO_MIN_FPS         2       ///< Menor fps permitido no sistema
#define CODEC_VIDEO_MAX_FPS         60      ///< Maior fps permitido no sistema
#define CODEC_VIDEO_MIN_WIDTH       180     ///< Menor largura de vídeo permitida
#define CODEC_VIDEO_MAX_WIDTH       1440    ///< Maior largura de vídeo permitida
#define CODEC_VIDEO_MIN_HEIGHT      120     ///< Menor altura de vídeo permitida
#define CODEC_VIDEO_MAX_HEIGHT      960     ///< Maior altura de vídeo permitida
#define CODEC_AUDIO_MIN_BITRATE     32      ///< Valor mais baixo para o bitrate de áudio (kbit/s)
#define CODEC_AUDIO_MAX_BITRATE     192     ///< Valor mais alto para o bitrate de áudio (kbit/s)
#define CODEC_AUDIO_MIN_VOLMUTE     1       ///< Valor mais baixo para o volume do auto-mute no áudio
#define CODEC_AUDIO_MAX_VOLMUTE     10      ///< Valor mais alto para o volume do auto-mute no áudio


#endif // _IDCODEC_H_
