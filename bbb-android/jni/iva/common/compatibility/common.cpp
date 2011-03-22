extern "C" {
#include <libavcodec/avcodec.h>
};

#define _WINSOCKAPI_   /* Prevent inclusion of winsock.h in windows.h */
#include <windows.h>
#include <Commdlg.h>
#include <shlobj.h>
#include <iostream>
using namespace std;

#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include <sys/types.h>
#include "error.h"
#include "errorDefs.h"
#include "common_compatibility.h"
#include "common_sock.h"

#include "common_leaks.h"
#ifdef _MSC_VER
#include <winsock2.h>
#else
#include <sys/socket.h>
#include <sys/stat.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <errno.h>
#endif

#include "../Folders.h"

common_context_t commonCtx;

int common_init()
{
    struct tm today;
    time_t t;
    char strtime[100];
    //char logpath[250];
    int ret;

    bzero(strtime, 100);
    //bzero(logpath, 250);

    pthread_mutex_init(&commonCtx.logMutex, NULL);
    error_init();

    IvaString logpath;
    logpath  = Folders::getLogFolder();

    // tenta criar o diretório do log
#ifdef _MSC_VER
    if (CreateDirectory(logpath.toWchar(), NULL) == 0) {
        int error = GetLastError();
        if (error != ERROR_ALREADY_EXISTS) { // já existe, não é erro
            error_s(AT, E_ERROR, "Não foi possível criar arquivo o diretório log (%s)",
                    COMMON_LOG_DIRECTORY);
            return E_ERROR;
        }
    }
#else
    if (mkdir((char *)logpath.c_str(), NULL) == 0) {
        if (errno != EEXIST) { // já existe, não é erro
            error_s(AT, E_ERROR, "Não foi possível criar arquivo o diretório log (%s)",
                    COMMON_LOG_DIRECTORY);
            return E_ERROR;
        }
    }
#endif

    // nome do log padrão depende da data de hoje
    time(&t);
    localtime_s(&today, &t);
    //strftime(strtime, 100, "log-%d.%m.%y-%Hh%M.txt", today); // usa a hora do dia tbm
    strftime(strtime, 100, "log-%d.%m.%y.txt", &today); // usa só o dia

    // abre o arquivo de log padrão
    pthread_mutex_lock(&commonCtx.logMutex);
    commonCtx.logFile = common_log_openFile(strtime);

    // imprime data e o texto no arquivo de log
    if (commonCtx.logFile) {
        time(&t);
        localtime_s(&today, &t);
        strftime(strtime, 50, "%d/%m/%Y %H:%M.%S", &today);
        fprintf(commonCtx.logFile, "\n+-------------------------------------------------------------+\n");
        fprintf(commonCtx.logFile, "| Nova sessao [%s]                           |\n", strtime);
        fprintf(commonCtx.logFile, "+-------------------------------------------------------------+\n");
        fflush(commonCtx.logFile);
        ret = E_OK;
    } else {
        ret = E_ERROR;
    }

    pthread_mutex_unlock(&commonCtx.logMutex);

    return ret;
}

int common_end()
{
    struct tm today;
    time_t t;
    char strtime[50];

    // finaliza arquivo de lpg
    if (commonCtx.logFile) {
        // imprime data e o texto no arquivo de log
        time(&t);
        localtime_s(&today, &t);
        strftime(strtime, 50, "%d/%m/%Y %H:%M.%S", &today);

        pthread_mutex_lock(&commonCtx.logMutex);
        fprintf(commonCtx.logFile,
                "+-------------------------------------------------------------+\n");
        fprintf(commonCtx.logFile,
                "| Fim da sessao [%s]                        |\n", strtime);
        fprintf(commonCtx.logFile,
                "+-------------------------------------------------------------+\n\n");
        fflush(commonCtx.logFile);
        fclose(commonCtx.logFile); // fecha o arquivo
        pthread_mutex_unlock(&commonCtx.logMutex);
    }

    error_end();
    pthread_mutex_destroy(&commonCtx.logMutex);

    return E_OK;
}



/********************************************************************/



#ifdef _MSC_VER
void bzero(char * s, int n)
{
    memset(s, '\0', n);
}
#endif

void common_nullFunc(char *fmt, ...)
{
; // não deve fazer NADA
}

unsigned int getTimestamp(void)
{
#ifdef _MSC_VER
    LARGE_INTEGER ts,freq;
    unsigned int timestamp = 0;

    // Query for the timestamp
    QueryPerformanceCounter(&ts);

    // se o hardware instalado suporta "high-resolution performance counter"
    // o valor de freq é diferente de zero
    QueryPerformanceFrequency(&freq);

    /*
    // não usa em alta-resolução
    if(freq.QuadPart == 0) 
    {
        // no caso do hardware nao suportar o mecanismo convencional, 
        // usa-se a funcao gettimeofday versao windows
        FILETIME ft;
        unsigned int tmpres = 0;

        GetSystemTimeAsFileTime(&ft);
        timestamp |= ft.dwLowDateTime;

        timestamp /= 10000;
    }
    else*/

    timestamp = (unsigned int) ((ts.QuadPart * 1000) / ((double) freq.QuadPart));
#else
    /// \todo Código não testado
    struct timespec ts;
    unsigned int timestamp = 0;
    clock_gettime(CLOCK_MONOTONIC,&ts);
    timestamp = ts.tv_sec*1000 + ts.tv_nsec/1000000;
#endif
    return timestamp;
}

wchar_t *charToWchar(char *str)
{
    wchar_t *wcStr;
    int length;

    length = (int)strlen(str)+1;
    wcStr = (wchar_t *)malloc(sizeof(wchar_t)*length);
    mbstowcs(wcStr, str, length);
    return wcStr;
}

char *wcharToChar(wchar_t *str)
{
    char *cStr;
    int length;

    length = (int)wcslen(str)+1;
    cStr = (char *)malloc(sizeof(char)*length);
    wcstombs(cStr, str, length);
    return cStr;
}

/// \todo ver se é necessária mesmo e remover
void die(int line, const char *function, const char *fmt,...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, fmt);
    vsprintf(buffer,fmt,args);
    va_end(args);

    fprintf(stderr,"%d %s: %s\n",line, function, buffer);
#ifdef DEBUG
    getchar();
#endif
    common_log(buffer);
    common_log("FINISH BY ERROR\n\n");
    exit(EXIT_FAILURE);
}




#ifdef _MSC_VER
BOOL WINAPI DllMain( HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpReserved )
{
    WORD wVersionRequested;
    WSADATA wsaData;

    wVersionRequested = MAKEWORD( 2, 2 );
    if(WSAStartup( wVersionRequested, &wsaData)!=0){
        return false;
    }

    return true;
}
#endif



/************************************************************************************
 * Logs e printfs
 ************************************************************************************/

void common_log_internal(FILE * file, const char *fmt, ...)
{
    va_list args;
    time_t t;
    struct tm today;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];
    char strtime[50];

    // se não criou o arquivo de log, sai fora
    if (!file) {
        return;
    }

    va_start(args, fmt);
    vsprintf(buffer,fmt,args);
    va_end(args);

    // pega a data atual
    time(&t);
    localtime_s(&today, &t);
    strftime(strtime, 50, "%d/%m/%Y %H:%M.%S", &today);

    // imprime data e o texto no arquivo de log
    fprintf(file, "[%s] %s\n", strtime, buffer);
    fflush(file);
}


void common_log(const char *fmt, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, fmt);
    vsprintf(buffer, fmt, args);
    va_end(args);

    if (commonCtx.logMutex) {
        pthread_mutex_lock(&commonCtx.logMutex);
    }
    common_log_internal(commonCtx.logFile, "%s", buffer);
    if (commonCtx.logMutex) {
        pthread_mutex_unlock(&commonCtx.logMutex);
    }
}

void common_log_at(char *location, const char *fmt, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, fmt);
    vsprintf(buffer, fmt, args);
    va_end(args);

    common_log("[%s]: %s", location, buffer);
}

void common_logprintf(const char *description, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, description);
    vsprintf(buffer, description, args);
    va_end(args);

    common_log("%s", buffer);
#ifdef _CONSOLE
    common_printf("%s\n", buffer);
#endif
}

void common_logprintf_at(char *location, const char *description, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];
    char *locAux;

    va_start(args, description);
    vsprintf(buffer, description, args);
    va_end(args);

    locAux = common_parseAtStr(location);
    common_logprintf("[%s]: %s", locAux, buffer);
    if (locAux) {
        free(locAux);
    }
}

void common_printf_at(char *location, const char *description, ...)
{
#ifdef _CONSOLE
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, description);
    vsprintf(buffer, description, args);
    va_end(args);

    common_printf("[%s]: %s", location, buffer);
#endif
}



void common_flog(FILE * file, const char *fmt, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, fmt);
    vsprintf(buffer, fmt, args);
    va_end(args);

    common_log_internal(file, "%s", buffer);
}

void common_flog_at(FILE * file, char *location, const char *fmt, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, fmt);
    vsprintf(buffer, fmt, args);
    va_end(args);

    common_log_internal(file, "[%s]: %s", location, buffer);
}

void common_flogprintf(FILE * file, const char *description, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];

    va_start(args, description);
    vsprintf(buffer, description, args);
    va_end(args);

    common_log_internal(file, "%s", buffer);
#ifdef _CONSOLE
    common_printf("%s\n", buffer);
#endif
}

void common_flogprintf_at(FILE * file, char *location, const char *description, ...)
{
    va_list args;
    char buffer[COMMON_LOG_MAX_LINE_SIZE];
    char *locAux;

    va_start(args, description);
    vsprintf(buffer, description, args);
    va_end(args);

    locAux = common_parseAtStr(location);
    common_log_internal(file, "[%s]: %s", locAux, buffer);
    if (locAux) {
        free(locAux);
    }
}


int common_log_closeFile(FILE * file)
{
    if (file) {
        return fclose(file);
    } else {
        return E_NULL_PARAMETER;
    }
}

FILE * common_log_openFile(char *filename)
{
    //char logpath[255];
    FILE * file;

    string logpath;
    logpath  = Folders::getLogFolder();
    logpath += filename;

    // tenta criar o arquivo de log
    if ((file = fopen(logpath.c_str(), "a")) == NULL) {
        error_s(AT, E_ERROR, "Não foi possível abrir arquivo de log (%s)", filename);
    }

    return file;
}

int common_log_getDefaultName(char *name, int namesize)
{
    struct tm today;
    time_t t;

    if (!name) {
        return E_NULL_PARAMETER;
    }

    // nome do log padrão depende da data de hoje
    bzero(name, namesize);
    time(&t);
    localtime_s(&today, &t);
    strftime(name, namesize, "log-%d.%m.%y", &today); // usa só o dia, sem hora

    return E_OK;
}


/************************************************************************************
 * sleeps
 ************************************************************************************/

void common_sleep(int msec)
{
    struct timeval tv;
    fd_set readfds;
    SOCKET s=0;

    if (msec <= 0) return;

    tv.tv_sec = (long)(msec / 1000);
    tv.tv_usec = (long)((msec % 1000) * 1000);

    common_sock_startup();
    s = socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);

    FD_ZERO(&readfds);
    FD_SET(s,&readfds);

    // Usa a função select para manter o processo parado.
    select((int)s, &readfds, NULL, NULL, &tv);
#ifdef _MSC_VER
    closesocket(s);
#else
    close(s);
#endif
}

void common_usleep(int usec)
{
    struct timeval tv;
    fd_set readfds;
    SOCKET s=0;

    if (usec <= 0) return;

    tv.tv_sec = (long)(usec / 1000000);
    tv.tv_usec = (long)((usec % 1000000) * 1000000);

    common_sock_startup();
    s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    FD_ZERO(&readfds);
    FD_SET(s,&readfds);

    // Usa a função select para manter o processo parado.
    select((int)s, &readfds, NULL, NULL, &tv);
#ifdef _MSC_VER
    closesocket(s);
#else
    close(s);
#endif
}



/************************************************************************************
 * outras...
 ************************************************************************************/

int common_iToChar(char **str, uint32_t value)
{
    int valueSize;

    if (!str) {
        return E_NULL_PARAMETER;
    }

    if (value == 0) {
        valueSize = 2; // texto será "0\0"
    } else {
        valueSize = sizeof(char)*((int)floor(log10((float)value))+2);
    }
    *str = (char *)malloc(valueSize);
    _itoa(value, *str, 10);
    return E_OK;
}

int common_validateIPv4(char *ip, uint8_t multicast)
{
    char aux[15];
    int ipNumb, j, k = 0, count = 0;
    int firstIpNumb = 0;

    // valida o tamanho máximo de um ip
    int i = (int) strlen(ip);
    //printf("i=%d\n",i);
    if (i > 15) { 
        return E_ERROR;
    }

    while (count < 4) {
        // acha o próximo ponto
        i = (int) strcspn(ip + k, ".");
        //printf("i=%d\n",i);
        if (i > 3 || i == 0) {	 // mais de 3 dígitos ou 0 dígitos em um número
            //printf("i > 3 || i == 0\n");
            return E_ERROR;
        }

        // busca o número e coloca em aux
        bzero(aux, i+1);
        strncpy_s(aux, i+1, ip + k, i);
        for(j = 0; j < i; j++) {
            //printf("aux = %s\n",aux);
            if (!isdigit(aux[j])) {	// algo que não é um dígito
                //printf("!isdigit(aux[j]): %d\n",aux[j]);
                return E_ERROR;
            }
        }
        ipNumb = atoi(aux);


        // primeiro número do IP
        if (count == 0) {
            // aceita intervalo 1..239
            if (ipNumb < 1 || ipNumb > 239) {
                return E_ERROR;

                // se multicast, só aceita intervalo 224..239
            } else if (multicast && ipNumb < 224) {
                return E_ERROR;
            }
            firstIpNumb = ipNumb;

            // último número do IP
        } else if (count == 3) {

            // só maiores que 0
            if(ipNumb < 0) {
                return E_ERROR;

                // se o primeiro número é 239, só aceita que 239.x.x.254
            } else if (firstIpNumb == 239 && ipNumb > 254) {
                return E_ERROR;

                // se multicast, só aceita intervalo 1..254
            } else if (multicast && ipNumb < 1) {
                return E_ERROR;
            }

			else if ( ipNumb > 255) {
                return E_ERROR;
            }
        } else {
            // números intermediários entre 0..255
            if(ipNumb < 0 || ipNumb > 255){	
                return E_ERROR;
            }
        }

        k = k + i + 1;
        count++;

        if(count == 4) {
            if(ip[k-1] != '\0') {
                return E_ERROR;
            }
        }
    }

    return E_OK;
}

int common_getDB(int level)
{
	switch(level) {
        case 0:  return VU_db30;
		case 1:  return VU_db33;
		case 2:  return VU_db36;
		case 3:  return VU_db39;
		case 4:  return VU_db42;
		case 5:  return VU_db45;
		case 6:  return VU_db48;
		case 7:  return VU_db51;
		case 8:  return VU_db54;
		case 9:  return VU_db57;
		case 10: return VU_db60;
		case 11: return VU_db63;
		case 12: return VU_db66;
		case 13: return VU_db69;
		case 14: return VU_db72;
		case 15: return VU_db75;
		case 16: return VU_db78;
		case 17: return VU_db81;
		case 18: return VU_db84;
		case 19: return VU_db87;
		case 20: return VU_db90;
		case 21: return VU_db93;
        default:
            common_logprintf("Nível inválido");
            return VU_db93;
	}
}

int common_getVULevel(double amp)
{
    int level;

    if      (amp < VU_db30) level = 0;
    else if (amp < VU_db33) level = 1;
    else if (amp < VU_db36) level = 2;
    else if (amp < VU_db39) level = 3;
    else if (amp < VU_db42) level = 4;
    else if (amp < VU_db45) level = 5;
    else if (amp < VU_db48) level = 6;
    else if (amp < VU_db51) level = 7;
    else if (amp < VU_db54) level = 8;
    else if (amp < VU_db57) level = 9;
    else if (amp < VU_db60) level = 10;
    else if (amp < VU_db63) level = 11;
    else if (amp < VU_db66) level = 12;
    else if (amp < VU_db69) level = 13;
    else if (amp < VU_db72) level = 14;
    else if (amp < VU_db75) level = 15;
    else if (amp < VU_db78) level = 16;
    else if (amp < VU_db81) level = 17;
    else if (amp < VU_db84) level = 18;
    else if (amp < VU_db87) level = 19;
    else if (amp < VU_db90) level = 20;
    else                    level = 21;

    return level;
}

int common_rect_init(common_rect_t *rect)
{
    if (!rect) {
        return E_NULL_PARAMETER;
    }
    rect->x = 0;
    rect->y = 0;
    rect->w = 0;
    rect->h = 0;
    return E_OK;
}

int common_rect_copy(common_rect_t *dst, common_rect_t *src)
{
    if (!dst || !src) {
        return E_NULL_PARAMETER;
    }
    dst->x = src->x;
    dst->y = src->y;
    dst->w = src->w;
    dst->h = src->h;
    return E_OK;
}

int common_color_init(common_color_t *col)
{
    if (!col) {
        return E_NULL_PARAMETER;
    }
    col->r = 0;
    col->g = 0;
    col->b = 0;
    col->unused = 0;
    col->alpha = 255;
    return E_OK;
}

int common_color_toInt(common_color_t *col)
{
    int ret = 0;

    if (!col) {
        error_s(AT, E_NULL_PARAMETER, E_MSG_NULL_PARAMETER, "col");
        return E_NULL_PARAMETER;
    }

    ret += col->alpha * 0x1000000;
    ret += col->r * 0x10000;
    ret += col->g * 0x100;
    ret += col->b;

    return ret;
}

/*
int common_color_fromInt(common_color_t *col, int value)
{
    col->alpha = floor(value/0x1000000);
}*/


// créditos:
// http://devpinoy.org/blogs/cvega/archive/2006/06/19/xtoi-hex-to-integer-c-function.aspx
uint32_t common_xtoi(const char* xs)
{
    size_t szlen = strlen(xs);
    int i, xv, fact;
    uint32_t result;

    if (szlen > 0) {
        // limita em 32 bits
        if (szlen > 8) {
            return 0;
        }

        result = 0;
        fact = 1;

        // varre todos caracteres de trás pra frente
        for (i = (int)szlen-1; i >= 0; i--) {
            if (isxdigit(*(xs+i))) {
                if (*(xs+i)>=97) {
                    xv = ( *(xs+i) - 97) + 10;
                } else if ( *(xs+i) >= 65) {
                    xv = (*(xs+i) - 65) + 10; // letras maiúsculas (A - F)
                } else {
                    xv = *(xs+i) - 48; // digitos normais (0 - 9)
                }
                result += (xv * fact);
                fact *= 16;
            } else { // não é um dígito hexa válido
                return 0;
            }
        }
    }
    return result;
}

int common_strtrim(char *str)
{
    bool_t emptyChar = true;
    int firstValid, lastValid;
    int len;
    int newLen;

    if (!str) {
        error_s(AT, E_NULL_PARAMETER, E_MSG_NULL_PARAMETER, "str");
        return E_NULL_PARAMETER;
    }

    len = (int)strlen(str);
    if (len == 0) {
        //error_s(AT, E_STRING_EMPTY, E_MSG_STRING_EMPTY, "str");
        return E_STRING_EMPTY;
    }

    // acha o primeiro caractere usável
    firstValid = 0;
    do {
        emptyChar = isspace((unsigned char)str[firstValid]);
        firstValid++;
    } while (emptyChar && firstValid < len);
    firstValid--;

    if (emptyChar) { // nada usável na string, deixa ela limpa
        bzero(str, len+1);
        return E_OK;
    }

    // acha o último caractere usável
    lastValid = len-1;
    do {
        emptyChar = isspace((unsigned char)str[lastValid]);
        lastValid--;
    } while (emptyChar && firstValid >= 0);
    lastValid++;

    // newLen = tamanho da palavra válida
    newLen = lastValid-firstValid+1;
    if (firstValid > 0) { // corta caracteres do início
        strncpy(str, str+firstValid, newLen);
    }
    bzero(str+newLen, len-newLen); // corta caracteres do fim


    return E_OK;
}

void common_strrep(char *str, char oldchar, char newchar)
{
    int i;
    for (i = 0; i < strlen(str); i++) {
        if (str[i] == oldchar) {
            str[i] = newchar;
        }
    }
}

char * common_parseAtStr(const char * location)
{
    char * slashPt;
    char * ret;
    int i;
    int strStart;
    int retLen;


    // recebe uma string como: "d:\iva\trunk\common\common_test\error_test.cpp:test():18"
    // e quebra ela para:      "common_test\error_test.cpp:test():18"


    // procura a última normal
    slashPt = strrchr((char *)location, '\\');
    if (!slashPt) {
        slashPt = strrchr((char *)location, '/');
    }
    if (!slashPt) {
        return NULL; ///\todo melhorar. poderia alocar a própria 'location' inteira

    } else {
        // acha a próxima barra (de trás pra frente) pra incluir nome da lib junto na string
        strStart = 0;
        for (i = (int)(slashPt-location)-1; i >= 0; i--) {
            if (location[i] == '\\' || location[i] == '/') {
                strStart = i+1;
                break;
            }
        }
        // calcula tamanho da nova string e aloca memória
        retLen = (int)strlen(location) - strStart + 1; // incluindo \0
        ret = (char *)malloc(sizeof(char) * retLen);
        if (!ret) {
            error_s(AT, E_INSUFFICIENT_MEMORY, E_MSG_INSUFFICIENT_MEMORY2, retLen);
            return NULL;
        }
        // copia pra nova string
        sprintf_s(ret, retLen, "%s\0", location+strStart);
        
        return ret;
    }
}


char * common_formatChatMsg(const char *msg, const char *user)
{
    int lenMsg, lenUser, lenNew;
    time_t timeNow;
    struct tm * timeInfo;
    char timeMsg[8]; /// \todo fazer macro pro 8
    char *newMsg;

    if (!msg || !user) {
        error_s(AT, E_NULL_PARAMETER, E_MSG_NULL_PARAMETER, "msg | user");
        return NULL;
    }

    // busca a hora atual
    time(&timeNow);
    timeInfo = localtime(&timeNow);
    strftime(timeMsg, 8, "%H:%M", timeInfo);

    // tamanho das strs para calcular tamanho da str final
    lenMsg = strlen(msg);
    lenUser = strlen(user);
    lenNew = lenMsg + lenUser + 8 + 6; // 6 devido à formatação feita abaixo

    newMsg = (char *)malloc(sizeof(char) * lenNew);
    if (!newMsg) {
        error_s(AT, E_INSUFFICIENT_MEMORY, E_MSG_INSUFFICIENT_MEMORY2, lenNew);
        return NULL;
    }
    bzero(newMsg, lenNew);
    sprintf(newMsg, "(%s) %s: %s", timeMsg, user, msg);

    return newMsg;
}


char * common_formatChatSystemMsg(char *msg, char *user)
{
    int lenMsg, lenNew;
    time_t timeNow;
    struct tm * timeInfo;
    char timeMsg[8]; /// \todo fazer macro pro 8
    char *newMsg;

    if (!msg) {
        error_s(AT, E_NULL_PARAMETER, E_MSG_NULL_PARAMETER, "msg");
        return NULL;
    }

    // busca a hora atual
    time(&timeNow);
    timeInfo = localtime(&timeNow);
    strftime(timeMsg, 8, "%H:%M", timeInfo);

    // tamanho das strs para calcular tamanho da str final
    lenMsg = strlen(msg);
    lenNew = lenMsg + 8 + 5; // 6 devido à formatação feita abaixo

    newMsg = (char *)malloc(sizeof(char) * lenNew);
    if (!newMsg) {
        error_s(AT, E_INSUFFICIENT_MEMORY, E_MSG_INSUFFICIENT_MEMORY2, lenNew);
        return NULL;
    }
    bzero(newMsg, lenNew);
    sprintf(newMsg, "[%s: %s]", timeMsg, msg);

    return newMsg;
}



/* Facilidade de portar para o Linux */
#ifndef _MSC_VER
void _itoa(int value, char * string, int radix)
{
    sprintf(string,"%d",value);
}

char * strncpy_s(char *strDest, int numberOfElements, const char *strSource, unsigned count)
{
    return strncpy(strDest,strSource,count);
}

int WSAGetLastError()
{
    return errno;
}
#endif



int common_openSaveDialog(string * filename, list<pair<string,string>> filter, string extension)
{
    int wfiltersize = 2; // dois \0 finais
    string sfilter = "";
    for (list<pair<string,string>>::iterator i = filter.begin(); i != filter.end(); i++) {
        wfiltersize += (int) (*i).first.size() + 1;
        sfilter += (*i).first;
        sfilter += '\0';
        wfiltersize += (int) (*i).second.size() + 1;
        sfilter += (*i).second;
        sfilter += '\0';
    }
    sfilter += '\0';

    WCHAR * wfilter = new WCHAR[wfiltersize];
    ZeroMemory(wfilter,wfiltersize);
    WCHAR wextension[sizeof(extension)+1];
    ZeroMemory(wextension,sizeof(extension)+1);

    int buffersize;
    buffersize = MultiByteToWideChar(CP_ACP, 0, sfilter.c_str(), wfiltersize-1, wfilter, wfiltersize);
    buffersize = MultiByteToWideChar(CP_ACP, 0, extension.c_str(), (int) extension.size(), wextension, (int) extension.size()+1);

    WCHAR wfilename[MAX_PATH];
    ZeroMemory(wfilename,MAX_PATH);

    // http://msdn.microsoft.com/en-us/library/ms646839%28VS.85%29.aspx
    OPENFILENAME file;
    ZeroMemory(&file, sizeof(file));

    file.lStructSize = sizeof(file);
    file.hwndOwner = GetDesktopWindow();
    file.lpstrFilter = (LPCWSTR) wfilter;
    file.lpstrFile = (LPWSTR) wfilename;
    file.nMaxFile = MAX_PATH;
    file.Flags = OFN_EXPLORER | OFN_OVERWRITEPROMPT | OFN_PATHMUSTEXIST;
    file.lpstrDefExt = (LPCWSTR) wextension;

    // http://msdn.microsoft.com/en-us/library/ms646928%28VS.85%29.aspx
    BOOL result = GetSaveFileName(&file);

    if (!result)
        return E_WINDOW_CLOSED;

    char cfilename[MAX_PATH];
    ZeroMemory(cfilename,MAX_PATH);

    buffersize = WideCharToMultiByte(CP_ACP, 0, wfilename, MAX_PATH, cfilename, MAX_PATH, '\0', '\0');

    delete wfilter;

    (*filename).assign(cfilename);

    return E_OK;
}

int common_openFolderDialog(string * path)
{
    // http://msdn.microsoft.com/en-us/library/bb773205%28VS.85%29.aspx
    BROWSEINFO info;
    ZeroMemory(&info, sizeof(info));

    info.hwndOwner = GetDesktopWindow();
    info.ulFlags = BIF_RETURNONLYFSDIRS | BIF_DONTGOBELOWDOMAIN | BIF_NEWDIALOGSTYLE;// | BIF_USENEWUI;
    info.lpszTitle = L"Selecione a pasta desejada:";

    // http://msdn.microsoft.com/en-us/library/bb762115%28VS.85%29.aspx
    LPITEMIDLIST idList = SHBrowseForFolder(&info);

    // se pressionou "cancelar" ou "fechar janela", retorna falso aqui!
    if (!idList)
        return E_WINDOW_CLOSED;

    WCHAR wfolder[MAX_PATH];
    ZeroMemory(wfolder, MAX_PATH);
    BOOL result = SHGetPathFromIDList(idList,(LPWSTR) wfolder);

    // se selecionou uma pasta inválida, retorna falso aqui!
    if (!result)
        return E_INVALID_FOLDER;

    char cfolder[MAX_PATH];
    ZeroMemory(cfolder,MAX_PATH);

    int buffersize = WideCharToMultiByte(CP_ACP, 0, wfolder, MAX_PATH, cfolder, MAX_PATH, '\0', '\0');

    (*path).assign(cfolder);

    return E_OK;
}







