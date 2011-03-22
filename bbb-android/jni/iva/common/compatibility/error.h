#ifndef _ERROR_H_
#define _ERROR_H_

#include <pthread.h>
#include <errorDefs.h>

#define ERROR_MAX_MESSAGE_SIZE      255  ///< Máximo de caracteres de uma mensagem de erro
#define ERROR_QUEUE_COUNT            10   ///< Número de erros armazenados na fila

#define ERROR_LEVEL_UNDEFINED         0
#define ERROR_LEVEL_WARNING           1
#define ERROR_LEVEL_NORMAL            2



/******************************************************************
 * Constantes de erro de todas libs/entidades
 * Códigos seguem o padrão: E_NOMELIB_NOMEERRO
 * Mensagens de erro seguem o padrão: E_MSG_NOMELIB_NOMEERRO
 * OBS: Antes de incluir um erro, verificar se não pode ser utilizado um genérico.
 ******************************************************************/


// erros inexistentes na common nova ou com nome diferente
#define E_NULL_PARAMETER           E_COMMON_NULL_PARAMETER
#define E_INSUFFICIENT_MEMORY      E_COMMON_INSUFFICIENT
#define E_MEMORY_ERROR             E_COMMON_MEMORY_ERROR
#define E_NONE                     E_COMMON_UNKNOWN_ERROR
#define E_INVALID_PARAMETER        E_COMMON_INVALID_PARAM
#define E_PTHREAD_JOIN_ERROR       E_THREAD_JOIN
#define E_SOCKET_WSASTARTUP        E_SOCKET_STARTUP
#define E_SOCKET_WSACLEANUP        E_SOCKET_CLEANUP
#define E_MSG_NULL_PARAMETER                 "Parâmetro '%s' nulo"
#define E_MSG_INVALID_PARAMETER              "Parâmetro '%s' inválído"
#define E_MSG_INSUFFICIENT_MEMORY            "Memória insuficiente para alocar '%s'"
#define E_MSG_INSUFFICIENT_MEMORY2           "Memória insuficiente para alocar %d bytes"
#define E_MSG_PTHREAD_JOIN_ERROR             "Erro %d em pthread_join()"

#define E_STRING_EMPTY                          999900
#define E_WINDOW_CLOSED                         999901
#define E_INVALID_FOLDER                        999902
#define E_ELEMENT_NOT_FOUND                     999903
#define E_NO_ELEMENTS                           999904
#define E_NO_MORE_ELEMENTS                      999905
#define E_LINKED_LIST_EMPTY                     999906
#define E_HASH_TABLE_DUPLICATED_ENTRY           999907
#define E_SDL_MUTEX_ERROR                       999908
#define E_QUEUE_END_OF                          999909
#define E_HASH_TABLE_ERROR                      999910
#define E_SYNC_ALREADY_INITIALIZED              999911
#define E_SYNC_NOT_INITIALIZED                  999912
#define E_SDL_TIMER_ERROR                       999913
#define E_SDL_AUDIO_ERROR                       999914
#define E_AUDIO_MAX_STREAMS_REACHED             999915
#define E_AUDIO_INVALID_QUEUE                   999916

#define E_NET_REGISTER_CONSUMER                 999917
#define E_NET_DONT_HAVE_NET                     999918
#define E_NET_SETSOCK                           999919
#define E_NET_REDIR_ITEM_NOT_FOUND              999920
#define E_NET_CONNECT_CLI                       999921
#define E_NET_INVALIDSIZE                       999922
#define E_NET_BIND                              999923
#define E_NET_REUSEADDR                         999924
#define E_NET_SEND_ERROR                        999925
#define E_MSG_NET_REDIR_ITEM_NOT_FOUND          "Item de id %d não encontrado na lista"

#define E_NETCOM_CONNECTION_TIMEOUT             999926


/*
 ///< Tamanho de size invalido.

#define E_MUTEXP                             618 ///< Erro mutexp
#define E_MUTEXV                             619 ///< Erro mutexv
#define E_NET_NOT_WSASTARTUP                 620 ///< Erro WSAStartup


#define E_MSG_NET_REDIR_ITEM_NOT_FOUND       "Item de id %d não encontrado na lista"
*/


/*
// Genericos: inespecificos
#define E_NONE                                  -1
#define E_OK                                    0
#define E_NO_ERROR                              0
#define E_ERROR                                 1
#define E_UNKNOWN_ERROR                         1

// Genericos: gerenciamento de memoria
#define E_MEMORY_ERROR                          10
#define E_INSUFFICIENT_MEMORY                   11
#define E_MEMORY_OVERFLOW                       12
#define E_NULL_PARAMETER                        13
#define E_INVALID_PARAMETER                     14
#define E_MSG_NULL_PARAMETER                   "Parâmetro '%s' nulo"
#define E_MSG_INVALID_PARAMETER                "Parâmetro '%s' inválído"
#define E_MSG_INSUFFICIENT_MEMORY              "Memória insuficiente para alocar '%s'"
#define E_MSG_INSUFFICIENT_MEMORY2             "Memória insuficiente para alocar %d bytes"

// Genericos: manipulacao de arquivos
#define E_FILE_ERROR                            20
#define E_FILE_NOT_FOUND                        21
#define E_INVALID_FOLDER                        22
#define E_WINDOW_CLOSED                         23

// strings
#define E_STRING_EMPTY                          25

// Genericos: dispositivos
#define E_DEVICE_ERROR                          30
#define E_DEVICE_NOT_FOUND                      31
#define E_AUDIO_DEVICE_NOT_FOUND                32
#define E_VIDEO_DEVICE_NOT_FOUND                33

// Genericos: ponteiros
#define E_POINTER_ERROR                         40
#define E_NULL_POINTER                          41

// threads
#define E_THREAD_KILL                           50
#define E_THREAD_INIT                           51

// sockets
#define E_SOCKET_WSASTARTUP                     60
#define E_SOCKET_WSACLEANUP                     61
#define E_SOCKET_WSAIOCTL                       62
#define E_SOCKET_IOCTLSOCKET                    63
#define E_SOCKET_LISTEN                         64
#define E_SOCKET_CREATE                         65
#define E_SOCKET_SETSOCKOPT                     66
#define E_SOCKET_BIND                           67
#define E_SOCKET_CONNECT                        68
#define E_SOCKET_ACCEPT                         69
#define E_SOCKET_SELECT                         70
#define E_SOCKET_RECV                           71
#define E_SOCKET_SEND                           72
#define E_SOCKET_CLOSESOCKET                    73
#define E_SOCKET_STARTUP                        74
#define E_SOCKET_CLEANUP                        75
#define E_SOCKET_CONNECTION_CLOSED              76
#define E_SOCKET_CONNECTION_LOST                77
#define E_SOCKET_ERROR                          78
#define E_SOCKET_ERROR_CRITICAL                 79

// SDL
#define E_SDL_ERROR                          80
#define E_SDL_NOT_INITIALIZED                81
#define E_SDL_ERROR_INITIALIZING             82
#define E_SDL_VIDEO_ERROR                    83
#define E_SDL_AUDIO_ERROR                    84
#define E_SDL_TIMER_ERROR                    85
#define E_SDL_MUTEX_ERROR                    86

// libs dentro da common
#define E_HASH_TABLE_ERROR                   90
#define E_HASH_TABLE_DUPLICATED_ENTRY        91
#define E_LINKED_LIST_EMPTY                  92
#define E_TIMER_NOT_RUNNING                  93
#define E_MSG_TIMER_NOT_RUNNING              "A thread deste timer não está sendo executada"

// pthread
#define E_PTHREAD_JOIN_ERROR                 120
#define E_MSG_PTHREAD_JOIN_ERROR             "Erro %d em pthread_join()"

// Lib Queue
#define E_QUEUE_ERROR                        200
#define E_QUEUE_UNDEFINED                    201
#define E_QUEUE_EMPTY                        202
#define E_QUEUE_END_OF                       203
#define E_QUEUE_INVALID_CONSUMER             204
#define E_QUEUE_FREE_NEEDED                  205
#define E_QUEUE_MAX_CONSUMERS_REACHED        206
#define E_QUEUE_MAX_LENGTH_REACHED           207
#define E_QUEUE_ENQUEUE                      208

// Erros do PARSER
#define E_PARSER_CHECK_OK                    E_OK
#define E_PARSER_CHECK_ERROR                 E_ERROR
#define E_PARSER_WRONG_SYNTAX                250
#define E_PARSER_PARAM_WRONG_TYPE            251
#define E_PARSER_WRONG_CHECKER_TYPE          252
#define E_PARSER_NO_CHECKER                  253
#define E_PARSER_PARAM_TOO_MANY_CHARS        254

// Lib Captura
#define E_CAPTURE_ERROR                      300
#define E_CAPTURE_NO_DEVICE                  301
#define E_CAPTURE_NOT_INITIALIZED            302
#define E_CAPTURE_DEVICE_DOESNT_EXISTS       303
#define E_CAPTURE_DEVICE_ALREADY_IN_USE      304
#define E_CAPTURE_COULD_NOT_START_DEVICE     305
#define E_PORTAUDIO                          306
#define E_NO_AUDIO_DEVICES                   307
#define E_PTHREAD                            308
#define E_CODEC_NOT_FOUND                    309
#define E_FFMPEG                             310

// Lib Codificacao
#define E_ENCODE_ERROR                       400
#define E_ENCODE_FPS_ERROR                   401
#define E_ENCODE_FFMPEG_OPEN_ERROR           402
#define E_ENCODE_INCORRECT_CODEC_TYPE        403
#define E_ENCODE_ALREADY_STARTED             404
#define E_ENCODE_NOT_STARTED                 405
#define E_ENCODE_NOT_OPENED                  406
#define E_ENCODE_ALREADY_OPENED              407

// Lib Decodificacao
#define E_DECODE_ERROR                       500
#define E_DECODE_CODEC_NOT_FOUND             501
#define E_DECODE_CODEC_ERROR                 502
#define E_DECODE_ALREADY_STARTED             503
#define E_DECODE_NOT_STARTED                 504
#define E_DECODE_ALREADY_OPENED              505
#define E_DECODE_NOT_OPENED                  506
#define E_DECODE_WAITING_FRAME_I             507

// Lib Rede
#define E_NET_INVALID_SOCKET                 600 ///< Nao conseguiu criar o socket
#define E_NET_INVALID_IP                     601 ///< IP não é valido
#define E_NET_BIND                           602 ///< Erro ao tentar o connect com o servidor
#define E_NET_GET_HOST_NAME                  603 ///< Nao conseguiu pegar o host
#define E_NET_REUSEADDR                      604 ///< Nao conseguiu dar setsockopt
#define E_NET_ADD_MEMBERSHIP                 605 ///< Nao conseguiu dar add em grupo multicast
#define E_NET_CONNECT_CLI                    606 ///< Erro ao conectar
#define E_NET_MCASTTTL                       607 ///< Erro no Multicast TTL Time to Live
#define E_NET_MCASTLOOP                      608 ///< Erro multicast loop
#define E_NET_MCASTIF                        609 ///< Erro multicast if
#define E_NET_PACKET_OVERFLOW                610 ///< Erro tamanho de pacote
#define E_NET_BUFFPTR                        611 ///< Nao conseguiu alocar memoria para um buffer (initBuffers)
#define E_NET_INITBUFFERS                    612 ///< Erro na funcao InitBuffers (pode ser E_NET_BUFFPTR ou E_E_ARRAYPTR)
#define E_NET_ARRAYPTR                       613 ///< Erro alocando espaco pro array de estruturas.
/// \todo Esse erro abaixo não seria da QUEUE?
#define E_NET_REGISTER_CONSUMER              614 ///< Nao conseguir registrar consumidor
#define E_NET_SETSOCK                        615 ///< Erro no setsockopt
#define E_NET_INVALIDSIZE                    616 ///< Tamanho de size invalido.
#define E_NET_DONT_HAVE_NET                  617 ///< Erro avisando que nao tem conexao
#define E_MUTEXP                             618 ///< Erro mutexp
#define E_MUTEXV                             619 ///< Erro mutexv
#define E_NET_NOT_WSASTARTUP                 620 ///< Erro WSAStartup
#define E_NET_REDIR_ITEM_NOT_FOUND           621
#define E_NET_SEND_ERROR                     622
#define E_MSG_NET_REDIR_ITEM_NOT_FOUND       "Item de id %d não encontrado na lista"

// Lib VISAudio
#define E_AUDIO_ERROR                        700
#define E_AUDIO_NOT_INITIALIZED              701
#define E_AUDIO_ALREADY_INITIALIZED          702
#define E_AUDIO_MAX_STREAMS_REACHED          703
#define E_AUDIO_INVALID_QUEUE                704
#define E_AUDIO_NO_STREAM                    705

// Lib VISVideo
#define E_VIDEO_ERROR                        800
#define E_VIDEO_NOT_INITIALIZED              801
#define E_VIDEO_ALREADY_INITIALIZED          802
#define E_VIDEO_INVALID_QUEUE                803
#define E_VIDEO_MAX_SURFACES_REACHED         804
#define E_VIDEO_SURFACE_UNAVAILABLE          805
#define E_VIDEO_SURFACE_EMPTY                806
#define E_VIDEO_INVALID_ID                   807
#define E_VIDEO_INVALID_POSITION             808
#define E_VIDEO_INVALID_ACTION               809
#define E_VIDEO_MAX_FPS_REACHED              810
#define E_VIDEO_MIN_FPS_REACHED              811

// Lib VISSync
#define E_SYNC_ERROR                         900
#define E_SYNC_NOT_INITIALIZED               901
#define E_SYNC_ALREADY_INITIALIZED           902
#define E_SYNC_AUDIO_NOT_INITIALIZED         903
#define E_SYNC_AUDIO_NOT_PLAYING             904
#define E_SYNC_VIDEO_NOT_INITIALIZED         905
#define E_SYNC_VIDEO_NOT_PLAYING             906
#define E_SYNC_INVALID_AUDIO_STREAM          907
#define E_SYNC_INVALID_VIDEO_STREAM          908



/// \todo Rever esses erros abaixo. São usados? Mudar nome para o padrão...

// Lib list
#define E_ELEMENT_NOT_FOUND                 1000
#define E_NO_ELEMENTS                       1001
#define E_NO_MORE_ELEMENTS                  1002

// Lib capvideo
#define E_MICROSOFT_API                     1100
#define E_NO_VIDEO_DEVICES                  1101
#define E_DEVICE_PARAMETERS                 1102
#define E_NOT_A_VIDEO_DEVICE                1103
#define E_NO_MEDIA_TYPE                     1104

// Netcom
#define E_NETCOM_USERNAME_EXISTS                    1200
#define E_NETCOM_USER_REJECTED                      1201
#define E_NETCOM_CLIENT_NOT_FOUND                   1202
#define E_NETCOM_INVALID_ENTITY                     1203
#define E_NETCOM_INVALID_MESSAGE_TEXT               1204
#define E_NETCOM_SUITE_ALREADY_ON                   1205
#define E_NETCOM_TP_ALREADY_SET                     1206
#define E_NETCOM_TP_NOT_SET                         1207
#define E_NETCOM_NO_TP_AVAILABLE                    1208
#define E_NETCOM_CLIENT_NOT_REGISTERED              1209
#define E_NETCOM_INVALID_MESSAGE_CODE               1210
#define E_NETCOM_INVALID_DESTINATION                1211
#define E_NETCOM_INVALID_SOURCE                     1212
#define E_NETCOM_CLIENT_ALREADY_REGISTERED          1213
#define E_NETCOM_MESSAGE_TOO_BIG                    1214
#define E_NETCOM_MESSAGE_EMPTY                      1215
#define E_NETCOM_CONNECTION_CLOSED                  1216
#define E_NETCOM_CLIENT_ALREADY_CONNECTED           1217
#define E_NETCOM_CLIENT_NOT_CONNECTED               1218
#define E_NETCOM_CONNECTION_ERROR                   1219
#define E_NETCOM_CONNECTION_TIMEOUT                 1220
#define E_NETCOM_SELECT_TIMEOUT                     1221
#define E_NETCOM_UNKNOWN_MESSAGE_TYPE               1222
#define E_NETCOM_CLIENT_NOT_PRESENTER               1223
#define E_NETCOM_INVALID_PORTS                      1224
#define E_NETCOM_CLIENT_ALREADY_TRANSMITTING        1225
#define E_NETCOM_CLIENT_NOT_TRANSMITTING            1226
#define E_NETCOM_SUITE_NOT_CONNECTED                1227
#define E_NETCOM_MAX_USERS_REGISTERED               1228
#define E_NETCOM_PREVIEW_ALREADY_TRANSMITTING       1229
#define E_NETCOM_PREVIEW_NOT_TRANSMITTING           1230
#define E_NETCOM_SOCKET_ERROR                       1231
#define E_MSG_NETCOM_UNKNOWN_MESSAGE_TYPE           "Netcom: tipo de mensagem desconhecido: %d"
#define E_MSG_NETCOM_INVALID_MESSAGE_TEXT           "Netcom: texto da mensagem é inválido"
#define E_MSG_NETCOM_INVALID_ENTITY                 "Netcom: entidade inválida: %d"
#define E_MSG_NETCOM_INVALID_MESSAGE_CODE           "Netcom: código da mensagem é inválido: %d"
#define E_MSG_NETCOM_INVALID_SOURCE                 "Netcom: origem da mensagem é inválida: %d"
#define E_MSG_NETCOM_CLIENT_NOT_FOUND               "Netcom: cliente '%d' não encontrado"
#define E_MSG_NETCOM_CLIENT_NOT_PRESENTER           "Netcom: cliente '%d' não é um apresentador"
#define E_MSG_NETCOM_INVALID_DESTINATION            "Netcom: destino da mensagem é inválido: %d"
#define E_MSG_NETCOM_CLIENT_ALREADY_REGISTERED      "Netcom: cliente '%d' já registrado"
#define E_MSG_NETCOM_CLIENT_NOT_PRESENTER           "Netcom: cliente '%d' não é um apresentador"
#define E_MSG_NETCOM_CLIENT_NOT_REGISTERED          "Netcom: cliente '%d' não está registrado"
#define E_MSG_NETCOM_SUITE_NOT_CONNECTED            "Netcom: não há uma suíte conectada"
#define E_MSG_NETCOM_MAX_USERS_REGISTERED           "Netcom: número máximo de usuários atingido"

*/


/******************************************************************
 * Lib de erros
 ******************************************************************/


/** \struct error_t
 *  \brief Estrutura de um erro
 */
typedef struct {
    int code;               ///< Código do erro
    char *msg;              ///< Mensagem do erro
    int level;              ///< Nível (grau de severidade) do erro
    char *atStr;            ///< String com a localização do erro (derivada da macro AT)
} error_t;

/** \struct error_queue_t
 *  \brief Fila de erros
 */
typedef struct {
    error_t items[ERROR_QUEUE_COUNT];
    int nextItem;
    pthread_mutex_t mutex;
} error_queue_t;

/** \struct error_context_t
 *  \brief Representa a estrutura geral da biblioteca de erros.
 */
typedef struct {
    error_queue_t items;    ///< Fila dos últimos erros
} error_context_t;


/** \brief Inicializa a lib de erros.
 *  \param logname Nome do arquivo de log (opcional).
 *  \return E_OK se sucesso ou o código de erro gerado.
 */
int error_init();

/** \brief Finaliza a lib de erros.
 *  \return E_OK se sucesso ou o código de erro gerado.
 */
int error_end();

/** \brief Adiciona um erro com código e mensagem.
 *  \param location Informação do local onde o erro ocorreu (macro AT)
 *  \param code Código do erro adicionado.
 *  \param description Mensagem associada ao erro.
 *  \return E_OK se sucesso ou o código de erro gerado.
 *
 * Internamente só chama a função error_addToQueue() que realmente trata o erro.
 */
int error_s(const char *file, const char *func, int line, int code, const char *description, ...);

/** \brief Adiciona um erro só com código, sem mensagem
 */
#define error_c(at,code) error_s(at,code,NULL)

/** \brief Retorna o último erro.
 *  \return Código do último erro.
 *  \param msg Ponteiro para uma string onde será armazenada a mensagem do erro.
 *
 * Retorna o código do último erro adicionado na lib. Caso o erro esteja associado à
 * uma mensagem, aloca memória para a variável 'msg' e copia a mensagem do último erro.
 */
int error_last(char **msg, int *level);


/******************************************************************
 * Lib de erros - parte de warnings
 ******************************************************************/

/** \brief Adiciona um warning com código e mensagem.
 *  \param location Informação do local onde o warning ocorreu (macro AT)
 *  \param code Código do warning adicionado.
 *  \param description Mensagem associada ao warning.
 *  \return E_OK se sucesso ou o código de erro gerado.
 */
int warning_s(const char *file, const char *func, int line, int code, const char *description, ...);

/** \brief Adiciona um erro só com código, sem mensagem
 */
#define warning_c(at,code) warning_s(at,code,NULL)





#endif // _ERROR_H_
