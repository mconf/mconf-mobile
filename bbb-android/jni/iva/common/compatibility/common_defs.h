#ifndef _COMMON_DEFS_COMPAT_H_
#define _COMMON_DEFS_COMPAT_H_


#define true          1
#define false         0

#define MAX(a,b)      ((a>b)?a:b)

#if defined(SDL_VERSION) // tipos podem ter sido definidos pela SDL
#elif defined(AXIS2_SKIP_INT_TYPEDEFS)
#else
    typedef long long int64_t;
    typedef int int32_t;
    typedef short int16_t;
    #ifdef _MSC_VER
        typedef char int8_t;
    #endif
    typedef unsigned long long uint64_t;
    typedef unsigned int uint32_t;
    typedef unsigned short uint16_t;
    typedef unsigned char uint8_t;
    #define AXIS2_SKIP_INT_TYPEDEFS
    #define TYPES_IVA
#endif
typedef unsigned char bool_t;

#ifdef _MSC_VER
#define inline __inline
#else
#define sprintf_s snprintf
typedef unsigned int SOCKET;
#define INVALID_SOCKET -1
#define SOCKET_ERROR   -1
#endif


// para padronizar a passagem do local onde funções foram chamadas dentro do código
// basta usar a macro AT para informar arquivo:função:linha
#define STRINGIFY(x) #x
#define TOSTRING(x) STRINGIFY(x)
#ifdef _MSC_VER
//#define AT __FILE__ ":" __FUNCTION__ "():" TOSTRING(__LINE__)
#define AT __FILE__,__FUNCTION__,__LINE__
#else
//#define AT __FILE__ ":" TOSTRING(__LINE__)
#define AT __FILE__,__FUNCTION__,__LINE__
#endif


/***********************************************************************************
 * AUDIO E VIDEO
 ***********************************************************************************/

// Constantes para áudio e vídeo
// São macros padrão que devem ser usadas na INICIALIZAÇÃO dos componentes
#define COMMON_AUDIO_NETRECV_SIZE             (12*1024)  ///< Size para init da netrecv áudio
#define COMMON_AUDIO_NETRECV_QUANT            300        ///< Quant para init da netrecv áudio
#define COMMON_AUDIO_NETRECV_PACKET_SIZE      1024       ///< Tamanho dos pacotes de áudio
#define COMMON_AUDIO_DEFAULT_NET_BITRATE      ((int)(COMMON_AUDIO_DEFAULT_BITRATE*1.2))
#define COMMON_AUDIO_DEFAULT_PORT             12002
#define COMMON_AUDIO_DEFAULT_VOLUME           100        ///< Volume padrão para tocar áudio
#define COMMON_AUDIO_DEFAULT_SYNC             0          ///< 0: desabilitado, 1: habilitado
#define COMMON_VIDEO_NETRECV_SIZE             (512*1024) ///< Size para init da netrecv vídeo
#define COMMON_VIDEO_NETRECV_QUANT            8          ///< Quant para init da netrecv vídeo
#define COMMON_VIDEO_NETRECV_PACKET_SIZE      1024       ///< Tamanho dos pacotes de vídeo
#define COMMON_VIDEO_DEFAULT_NET_BITRATE      ((int)(COMMON_VIDEO_DEFAULT_BITRATE*1.2))
#define COMMON_VIDEO_DEFAULT_PORT             12001
#define COMMON_VIDEO_DEFAULT_SYNC             0          ///< 0: desabilitado, 1: habilitado
#define COMMON_SESSION_DEFAULT_IP             "127.0.0.1"
#define COMMON_SESSION_DEFAULT_PORT           12000
#define COMMON_DEFAULT_IGMP                   60         ///< (em segundos)

#define H222            1       ///\todo rever se é utilizado mesmo
#define VIDEO_T         1100    ///\todo rever se é utilizado mesmo
#define AUDIO_T         1101    ///\todo rever se é utilizado mesmo


// Nome das entidades
#define COMMON_NAME_MODERATOR               "Moderador"
#define COMMON_NAME_SUITE                   "Suíte"
#define COMMON_NAME_PRESENTER               "Apresentador"



/***********************************************************************************
 * DEFINIÇÕES SUÍTE
 ***********************************************************************************/
#define COMMON_SUITE_NUM_PREVIEWS              6      ///< Número de previews da suíte



// defines que indicam o número máximo de CARACTERES que os itens podem ter
// todos valores INCLUINDO um \0 no fim da string

#define COMMON_MAX_USERNAME_C        31         ///< Username (30 caracteres)
#define COMMON_MAX_PASSWORD_C        31         ///< Senha (30 caracteres)
#define COMMON_MAX_NAME_C            51         ///< Nome de usuário (50 caracteres)
#define COMMON_MAX_ORGANIZATION_C    51         ///< Organização (50 caracteres)
#define COMMON_MAX_LOCAL_C           51         ///< Localização (50 caracteres)
#define COMMON_MAX_CONNECTION_C      26         ///< Conexão (25 caracteres)
#define COMMON_MAX_PHONE_C           26         ///< Telefone (25 caracteres)
#define COMMON_MAX_EMAIL_C           51         ///< E-mail (25 caracteres)
#define COMMON_MAX_GTALK_C           COMMON_MAX_EMAIL_C
#define COMMON_MAX_SKYPE_C           COMMON_MAX_USERNAME_C
#define COMMON_MAX_OPERATOR_C        COMMON_MAX_NAME_C  ///< Nome do operador

#define COMMON_MAX_CHANNEL_C         31         ///< Nome de canal (30 caracteres)
#define COMMON_MAX_IP_C              16         ///< IPv4 (xxx.xxx.xxx.xxx)
#define COMMON_MAX_PORT_C            7          ///< Porta (6 dígitos)
#define COMMON_MAX_CODEC_C           2          ///< Id de codecs (1 dígito)
#define COMMON_MAX_NETCOM_MODEOP_C   2          ///< Modo do servidor da netcom: moderado ou p2p
#define COMMON_MAX_VOLUME_C          3          ///< Valor para o volume (2 dígitos)
#define COMMON_MAX_AUTOMUTE_C        3          ///< Valor para o automute (2 dígitos)
#define COMMON_MAX_AUDIO_BITRATE_C   4          ///< Bitrate de áudio (5 dígitos)
#define COMMON_MAX_VIDEO_BITRATE_C   6          ///< Bitrate de vídeo (5 dígitos)
#define COMMON_MAX_NETCOM_INDEX_C    6          ///< Índice máximo do cliente na netcom (2^16 dígitos)
#define COMMON_MAX_FLAG_C            2          ///< Flags em geral (1 caracter)
#define COMMON_MAX_PREVIEW_C         2          ///< Número do preview (1 dígito - de 0 a 5)
#define COMMON_MAX_ENTITY_C          2          ///< Tipo da entidade (1 dígito)
#define COMMON_MAX_ACTIVE_TPS_C      4          ///< Número de TPs ativos (3 dígitos)
#define COMMON_MAX_MESSAGE_TEXT_C    501        ///< Mensagens de chat (500 caracteres)
#define COMMON_MAX_RES_WIDTH_C       5          ///< Largura máxima da resolução (4 dígitos)
#define COMMON_MAX_RES_HEIGHT_C      5          ///< Altura máxima da resolução (4 dígitos)
#define COMMON_MAX_FPS_C             4          ///< FPS máximo (3 dígitos)
#define COMMON_MAX_GOP_C             4          ///< GOP máximo (3 dígitos)
#define COMMON_MAX_VISUALIZER_C      2          ///< Visualizador (é um bool)
#define COMMON_MAX_FILENAME_C        256        ///< Arquivos (incluindo path + nome arquivo)

#define COMMON_MAX_CHAT_MESSAGES     200        ///< Número máximo de mensagens de chat guardadas


// CHATS

#define COMMON_CHAT_COLOR_SELF       0xffffff00  ///< Cor das mensagens da própria entidade
#define COMMON_CHAT_COLOR_SYSTEM     0xff800000  ///< Cor das mensagens de sistema
#define COMMON_CHAT_COLOR_OTHERS     0xffffffff  ///< Cor padrão das mensagens em geral


// Controle dos LEDs do VUMETER
// vários níveis com a amplitude correspondente
#define VU_db97     65536   // max = 96db // 16 níveis = 16bits da amostra
#define VU_db93     49152
#define VU_db90     32768
#define VU_db87     24576
#define VU_db84     16384
#define VU_db81     12288
#define VU_db78     8192
#define VU_db75     6144
#define VU_db72     4096
#define VU_db69     3072
#define VU_db66     2048
#define VU_db63     1536
#define VU_db60     1024
#define VU_db57     768
#define VU_db54     512
#define VU_db51     384
#define VU_db48     256
#define VU_db45     192
#define VU_db42     128
#define VU_db39     96
#define VU_db36     64
#define VU_db33     48
#define VU_db30     32
#define VU_db27     24
#define VU_db24     16
#define VU_db21     12
#define VU_db18     8
#define VU_db15     6
#define VU_db12     4
#define VU_db9      3
#define VU_db6      2
#define VU_db0      1       // min


#endif // _COMMON_DEFS_H_
