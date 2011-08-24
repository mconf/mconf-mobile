#include "CommonLeaks.h"
#include "ErrorVector.h"
#include "errorDefs.h"
#include "CommonLeaksCpp.h"

ErrorVector::ErrorVector() :
  vector<vector<IvaString> >()
{

  /*
   * Cria entidades
   */

  resize(ERROR_LIBS_SIZE);
  
  /*
   * Common errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON).resize(ERROR_COMMON_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_OK]                          = "OK";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_UNKNOWN_ERROR]               = "Erro inesperado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_MEMORY_ERROR]                = "Erro de memória";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_OVERFLOW]                    = "Estouro de memória";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_INSUFFICIENT]                = "Memória insuficiente";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_NULL_PARAMETER]              = "Parâmetro nulo";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_INVALID_PARAMETER]           = "Parâmetro inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_FILE_ERROR]                  = "Manipulação de arquivo";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_FILE_NOT_FOUND]              = "Arquivo não encontrado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_DEVICE_ERROR]                = "Erro no dispositivo";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_DEVICE_NOT_FOUND]            = "Dispositivo não encontrado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_COMMON)[ERROR_COMMON_NULL_POINTER]                = "Ponteiro nulo";

  /*
   * Thread errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_THREAD).resize(ERROR_THREAD_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_THREAD)[ERROR_THREAD_KILL]                        = "Thread kill";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_THREAD)[ERROR_THREAD_INIT]                        = "Inicialização da thread";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_THREAD)[ERROR_THREAD_JOIN]                        = "Thread join";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_THREAD)[ERROR_THREAD_MUTEX_LOCK]                  = "Lock de um mutex";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_THREAD)[ERROR_THREAD_MUTEX_UNLOCK]                = "Unlock de um mutex";

  /*
   * Socket errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET).resize(ERROR_SOCKET_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_STARTUP]                     = "Inicialização do contexto de socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_CLEANUP]                     = "Finalização do contexto de socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_IOCTL]                       = "Controle de IO do socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_LISTEN]                      = "Espera de conexões no socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_CREATE]                      = "Criação do socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_SETSOCKOPT]                  = "Configuração de opção do socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_BIND]                        = "Socket bind";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_CONNECT]                     = "Conexão de socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_ACCEPT]                      = "Socket accept";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_SELECT]                      = "Socket select";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_RECV]                        = "Socket recv";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_SEND]                        = "Envio de dados pelo socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_CLOSE]                       = "Fechamento do socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_SHUTDOWN]                    = "Desligamento do socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_ADD_MEMBERSHIP]              = "Multicast join";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SOCKET)[ERROR_SOCKET_DROP_MEMBERSHIP]             = "Multicast leave";
  
  /*
   * Queue errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_QUEUE).resize(ERROR_QUEUE_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_QUEUE)[ERROR_QUEUE_EMPTY]                         = "Fila vazia";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_QUEUE)[ERROR_QUEUE_ENDOF]                         = "Fim da fila";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_QUEUE)[ERROR_QUEUE_INVALID_CONSUMER]              = "Consumidor inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_QUEUE)[ERROR_QUEUE_FREE_NEEDED]                   = "Liberação de fila necessária";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_QUEUE)[ERROR_QUEUE_MAX_CONSUMERS_REACHED]         = "Máximo de consumidores alcançado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_QUEUE)[ERROR_QUEUE_MAX_LENGTH_REACHED]            = "Tamanho máximo da fila alcançado";

  /*
   * Parser errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_PARSER).resize(ERROR_PARSER_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_PARSER)[ERROR_PARSER_WRONG_SYNTAX]                = "Erro na sintaxe do arquivo de configuração";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_PARSER)[ERROR_PARSER_PARAM_WRONG_TYPE]            = "Parâmetro com tipo incorreto no arquivo de configuração";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_PARSER)[ERROR_PARSER_WRONG_CHECKER_TYPE]          = "Verificador de tipo incorreto";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_PARSER)[ERROR_PARSER_NO_CHECKER]                  = "Não há verificador";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_PARSER)[ERROR_PARSER_PARAM_TOO_MANY_CHARS]        = "Estouro no número de caracteres";

  /*
   * Capvid errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPVID).resize(ERROR_CAPVID_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPVID)[ERROR_CAPVID_MICROSOFT_API]               = "Problema na API de captura";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPVID)[ERROR_CAPVID_DEVICE_PARAMETERS]           = "Parametros do dispositivo incorretos";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPVID)[ERROR_CAPVID_NOT_A_VIDEO_DEVICE]          = "Dispositivo não é dispositivo de vídeo";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPVID)[ERROR_CAPVID_NO_MEDIA_TYPE]               = "Sem tipo de mídia";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPVID)[ERROR_CAPVID_NO_DEVICES]                  = "Nenhum dispositivo de vídeo disponível";

  /*
   * Encode Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE).resize(ERROR_ENCODE_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_FPS]                         = "Framerate incorreto";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_OPEN]                        = "Inicialização da ffmpeg"; 
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_INCORRECT_CODEC_TYPE]        = "Tipo de codec incorreto";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_ALREADY_STARTED]             = "Codec já iniciado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_NOT_STARTED]                 = "Codec não iniciado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_ALREADY_OPENED]              = "Codec já aberto";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_NOT_OPENED]                  = "Codec não aberto";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_MULTITHREAD]                 = "Setando ffmpeg como multi-threaded";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_PRESET_NOT_FOUND]            = "Arquivo de preset não encontrado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_AUDIO]                       = "Codificação de áudio";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_ENCODE)[ERROR_ENCODE_VIDEO]                       = "Codificação de vídeo";

  /*
   * Decode Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE).resize(ERROR_DECODE_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_CODEC_NOT_FOUND]             = "Codec não encontrado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_CODEC_ERROR]                 = "Problema no codec";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_ALREADY_STARTED]             = "Codec já iniciado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_NOT_STARTED]                 = "Codec não iniciado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_ALREADY_OPENED]              = "Codec já aberto";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_NOT_OPENED]                  = "Codec não aberto";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_WAITING_FRAME_I]             = "Aguardando frame I";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_ENQUEUE]                     = "Erro ao colocar dados na queue";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_SWS_CONTEXT]                 = "Criação do contexto swscale";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_AUDIO]                       = "Decodificação de áudio";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_DECODE)[ERROR_DECODE_VIDEO]                       = "Decodificação de vídeo";

  /*
   * Net Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET).resize(ERROR_NET_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_INVALID_SOCKET]                    = "Problema na criação do socket";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_INVALID_IP]                        = "IP não é válido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_GET_HOST_NAME]                     = "Não conseguiu adquirir o nome do host";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_ADD_MEMBERSHIP]                    = "Não conseguiu entrar em grupo multicast";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_MCASTTTL]                          = "Problema no Time to Live do multicast";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_MCASTLOOP]                         = "Erro no multicast loop";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_MCASTIF]                           = "Erro na interface do multicast";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_PACKET_OVERFLOW]                   = "Erro no tamanho do pacote";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_PACKET_BUFFPTR]                    = "Não conseguiu alocar memória para um buffer";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_PACKET_INITBUFFERS]                = "Problema na função InitBuffers";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_PACKET_ARRAYPTR]                   = "Alocação espaço para o array de estruturas";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_FRAG_ALREADY_RECEIVED]             = "Fragmento de dados já recebido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NET)[ERROR_NET_INVALID_TYPE]                      = "Tipo inválido";


  /*
   * Statistics Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_STATISTICS).resize(ERROR_STATISTICS_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_STATISTICS)[ERROR_STATISTICS_TIMES_READ] = "Lendo tempo de processamento";

  /*
   * Netcom Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM).resize(ERROR_NETCOM_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_DATA] = "Dados inválidos";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_HEADER_CHECKSUM] = "Checksum do cabeçalho inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_TEXT_CHECKSUM] = "Checksum do texto inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_CODE] = "Código inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_PREVIEW] = "Preview inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_TEXT_EMPTY] = "Texto vazio";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_CLIENT_NOT_FOUND] = "Cliente não encontrado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_SUITE_NOT_CONNECTED] = "Suite não conectada";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_NOT_CONNECTED] = "Não conectado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_ALREADY_CONNECTED] = "Já conectado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_NOT_REGISTERED] = "Não registrado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_ALREADY_REGISTERED] = "Já registrado";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_NAME_ALREADY_EXISTS] = "Nome já existe";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_USERNAME_ALREADY_EXISTS] = "Nome de usuário já existe";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_NOT_TRANSMITTING] = "Não está transmitindo";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_ALREADY_TRANSMITTING] = "Já está transmitindo";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_NOT_PROMOTED] = "Não está promovido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_ALREADY_PROMOTED] = "Já está promovido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_IP] = "Endereço IP inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_VERSION] = "Versão inválida";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_CONNECTION_LOST] = "Conexão perdida";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_CONNECTION_CLOSED] = "Conexão finalizada";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_NETCOM)[ERROR_NETCOM_INVALID_MESSAGE] = "Mensagem inválida";

  /*
   * Video Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_VIDEO).resize(ERROR_VIDEO_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_VIDEO)[ERROR_VIDEO_ALREADY_INITIALIZED] = "Video já inicializada";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_VIDEO)[ERROR_VIDEO_NOT_INITIALIZED] = "Video não inicializada";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_VIDEO)[ERROR_VIDEO_INVALID_ID] = "Id inválido";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_VIDEO)[ERROR_VIDEO_SURFACE_UNAVAILABLE] = "Surface não disponível";

  /*
   * Capaud Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPAUD).resize(ERROR_CAPAUD_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPAUD)[ERROR_CAPAUD_NO_DEVICES] = "Nenhum dispositivo de áudio disponível";
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPAUD)[ERROR_CAPAUD_PORTAUDIO] = "Chamada da lib portaudio";

  /*
   * Capenc Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPENC).resize(ERROR_CAPENC_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_CAPENC)[ERROR_CAPENC_ENCODE_VIDEO] = "Erro ao codificar vídeo";

  /*
   * Server Errors
   */

  vector<vector<IvaString> >::operator[](ERROR_LIBS_SERVER).resize(ERROR_SERVER_SIZE);
  vector<vector<IvaString> >::operator[](ERROR_LIBS_SERVER)[ERROR_SERVER_GSOAP_ERROR] = "Erro ao codificar vídeo";

  /**
   * Non Existent Error
   */

  nonError = "";

};

IvaString & ErrorVector::codeToMessage(int code)
{

  int lib;
  int error;
  error = code % 10000;
  lib = code / 10000;

  if (lib >= ((int) size())) {
    return nonError;
  };
  
  if (error >= ((int) (vector<vector<IvaString> >::operator[](lib).size()))) {
    return nonError;
  };

  return vector<vector<IvaString> >::operator[](lib)[error];
  
};

IvaString & ErrorVector::operator[] ( size_type n )
{

  return codeToMessage((int) n);

};

ErrorVector::~ErrorVector()
{

  

};
