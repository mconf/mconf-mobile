#ifndef _COMMON_H_
#define _COMMON_H_

#ifdef _MSC_VER
#include <winsock2.h>
#include <ws2tcpip.h>
#endif

#define _WINSOCKAPI_   /* Prevent inclusion of winsock.h in windows.h */
#include <windows.h>
#include <string>
#include <list>
#include <stdio.h>
#include <pthread.h>
#include "idcodec.h"

#include "common_defs.h"



/***********************************************************************************
 * ESTRUTURA GERAL DA COMMON - INICIALIZAÇÃO/DESTRUIÇÃO
 ***********************************************************************************/

typedef struct {
    FILE *logFile;
    pthread_mutex_t logMutex;
} common_context_t;

int common_init();
int common_end();



/***********************************************************************************
 * LOGS E PRINTS
 ***********************************************************************************/

#define COMMON_LOG_MAX_LINE_SIZE    1500
#define COMMON_LOG_DIRECTORY        "log"
#define COMMON_LOG_DIRECTORY_W      L"log"


// funções que só são executadas em modo DEBUG
#ifdef _DEBUG
#define common_logd common_log
#define common_logprintfd common_logprintf
#else
#define common_logd common_nullFunc
#define common_logprintfd common_nullFunc
#endif

// funções que só são executadas quando existe um CONSOLE
#ifdef _CONSOLE
#define common_printf printf
#else
#define common_printf common_nullFunc
#endif

/// \todo essa função deveria ser inline mas dá erro. ver pq e resolver
void common_nullFunc(char *fmt, ...);

void common_printf_at(char *location, const char *description, ...);
void common_log(const char *description, ...);
void common_log_at(char *location, const char *description, ...);
void common_logprintf(const char *description, ...);
void common_logprintf_at(char *location, const char *description, ...);

void common_flog(FILE * file, const char *description, ...);
void common_flog_at(FILE * file, char *location, const char *description, ...);
void common_flogprintf(FILE * file, const char *description, ...);
void common_flogprintf_at(FILE * file, char *location, const char *description, ...);

int common_log_getDefaultName(char *name, int namesize);

// função interna onde o texto realmente é impresso no log
void common_log_internal(FILE * file, const char *fmt, ...);
FILE * common_log_openFile(char *filename);
int common_log_closeFile(FILE * file);

/** \struct common_av_videoDesc_t
 *  \brief Estrutura para descrição de um vídeo
 *
 * Utilizada como dado adicional da queue para passar o formato do vídeo desde a captura (capenc)
 * até a transmissão (netsend) e desde o recebimento (netrecv) até a exibição (video).
 */
typedef struct {
    uint16_t width;         ///< Largura do vídeo em pixels
    uint16_t height;        ///< Altura do vídeo em pixels
    uint16_t fps;           ///< Frame rate
    uint32_t bitrate;       ///< Bitrate em kbit/s
} common_av_videoDesc_t;


/***********************************************************************************
 * Rect
 ***********************************************************************************/

typedef struct common_rect_t {
    int16_t x, y;
    uint16_t w, h;
} common_rect_t;


/** \brief Inicializador de uma estrutura common_rect_t.
 *  \param rect Estrutura a ser inicializada.
 */
int common_rect_init(common_rect_t *rect);

int common_rect_copy(common_rect_t *dst, common_rect_t *src);


/***********************************************************************************
 * Color
 ***********************************************************************************/

typedef struct common_color_t {
    uint8_t r;
    uint8_t g;
    uint8_t b;
    uint8_t unused; // Para ficar igual o da SDL
    uint8_t alpha;
} common_color_t;

/** \brief Inicializador de uma estrutura common_color_t.
 *  \param col Estrutura a ser inicializada.
 */
int common_color_init(common_color_t *col);

/** \brief Converte uma estrutura common_color_t para um int padrão 0xAARRGGBB.
 *  \param col Cor a ser convertida.
 *  \return Cor em um inteiro no formato 0xAARRGGBB
 */
int common_color_toInt(common_color_t *col);

/** \brief Converte uma string em hexadecimal para um inteiro no formato AARRGGBB.
 *  \param hexstr Cor a ser convertida - máximo 8 caracteres - COM parâmetro alpha.
 *  \return Cor em um inteiro no formato 0xAARRGGBB
 *
 * Exemplo de entrada: "80AA6D7C"
 */
uint32_t common_xtoi(const char* hexstr);

char * common_parseAtStr(const char * location);

char * common_formatChatMsg(const char *msg, const char *user);
char * common_formatChatSystemMsg(char *msg, char *user);

/***********************************************************************************
 * Sleep
 ***********************************************************************************/

/** \brief Sleep em milisegundos utilizando função select().
 *  \param msec Número de milisegundos para "dormir"
 */
void common_sleep(int msec);

/** \brief Sleep em microsegundos utilizando função select().
 *  \param usec Número de microsegundos para "dormir"
 */
void common_usleep(int usec);


/***********************************************************************************
 * Strings
 ***********************************************************************************/

// Por enquanto só pode ser usada com C++
/// \todo Rever o uso desta função...
/** \brief Converte um char* para um wchar_t* (unicode).
 *  \param str String a ser convertida.
 *  \return Nova string do tipo wchar_t*.
 *
 * Obs: A string retornada é alocada dentro da função. Portanto deve ser desalocada por
 * quem utilizar esta função.
 */
wchar_t * charToWchar(char *str);
char * wcharToChar(wchar_t *str);

/** \brief Converte um int para uma string.
 *  \param str Ponteiro para um ponteiro de char onde seáa colocado o resultado.
 *  \param value Valor inteiro que será convertido (valor não negativo)
 *  \return E_OK se é um IP valido ou E_ERROR caso contrário.
 *
 * Aloca a memória para 'str' e coloca o valor de 'value' nele.
 */
int common_iToChar(char **str, uint32_t value);

/** \brief Função para remover espaços no início e fim da string
 *  \param str String a ser editada
 *  \return E_OK ou código de erro gerado
 *
 * Remove qualquer tipo de espaço (com a função isspace()) do início e do fim da string 'str'.
 * O resultado é colocado na própria string. Ela NÃO é realocada, apenas seu conteúdo será
 * modificado.
 */
int common_strtrim(char *str);

/** \brief Função para substituir todas ocorrências de um char dentro de uma string
 *  \param str String a ser editada
 *  \param oldchar Char a ser substituído
 *  \param newchar Char que será colocado no lugar
 *  \return E_OK ou código de erro gerado
 */
void common_strrep(char *str, char oldchar, char newchar);


/***********************************************************************************
 * Geral - outras funções de propósito geral
 ***********************************************************************************/

void die(int line, const char *function, const char *fmt,...); /// \todo rever se é necessária

/** \brief Retorna o timestamp atual do sistema.
 *  \return Timestamp atual
 *
 * DEVE ser usada por TODAS as libs e entidades que precisam do timestamp. Com isso o timestamp
 * utilizado fica padrão para todo sistema.
 *
 * No windows, utiliza QueryPerformanceCounter().
 */
unsigned int getTimestamp(void);


#ifdef _MSC_VER
/** \brief Preenche uma string com zeros.
 *  \param s String a ser preenchida.
 *  \param n Tamanho da string (número total de bytes).
 */
void bzero(char *s, int n);
#endif

/** \brief Valida um endereço IPv4.
 *  \param ip Endereço que será validado.
 *  \param multicast Flag ('true' ou 'false') indicando se deve ser validado como multicast.
 *  \return E_OK se é um IP valido ou E_ERROR caso contrário.
 *
 * Valida a string para ver se corresponde a um endereço IP. Se a flag 'multicast' for 'true',
 * verifica se o IP está na faixa de IPs multicast.
 */
int common_validateIPv4(char *ip, uint8_t multicast);

int common_getDB(int level);

int common_getVULevel(double amp);


#ifndef _MSC_VER
void _itoa(int value, char * string, int radix);
char * strncpy_s(char *strDest, int numberOfElements, const char *strSource, unsigned count);
int WSAGetLastError();
#endif


int common_openSaveDialog(std::string * filename, std::list<std::pair<std::string,std::string> > filter,
                          std::string extension);
int common_openFolderDialog(std::string * path);

#endif // _COMMON_H_
