#include "common_leaks.h"
#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include "common_compatibility.h"
#include "error.h"
#include "error_int.h"
#include <sstream>
using namespace std;

error_context_t errors;

int error_init()
{
    error_queue_init(&errors.items);

    return E_OK;
}

int error_end()
{
    return error_queue_end(&errors.items);
}

int error_s(const char *file, const char *func, int line, int code, const char *description, ...)
{
    va_list args;
    int error = E_OK;

    //#define AT __FILE__ ":" __FUNCTION__ "():" TOSTRING(__LINE__)
    stringstream ss;
    ss << file;
    ss << ":";
    ss << func;
    ss << "():";
    ss << line;

    va_start(args, description);
    error = error_addToQueue(ss.str().c_str(), code, ERROR_LEVEL_NORMAL, description, args);
    va_end(args);

    return error;
}

int warning_s(const char *file, const char *func, int line, int code, const char *description, ...)
{
    va_list args;
    int error = E_OK;

    //#define AT __FILE__ ":" __FUNCTION__ "():" TOSTRING(__LINE__)
    stringstream ss;
    ss << file;
    ss << ":";
    ss << func;
    ss << "():";
    ss << line;

    va_start(args, description);
    error = error_addToQueue(ss.str().c_str(), code, ERROR_LEVEL_WARNING, description, args);
    va_end(args);

    return error;
}

int error_last(char **msg, int *level)
{
    return error_queue_pop(&errors.items, msg, level);
}





/*************************************************************
 * Internas
 *************************************************************/

int error_addToQueue(const char *location, int code, int level, const char *description,
                     va_list args)
{
    error_t error;
    char buffer[ERROR_MAX_MESSAGE_SIZE];

    memset(buffer, '\0', ERROR_MAX_MESSAGE_SIZE);

    error.code = code;
    error.level = level;
    if (location) {
        error.atStr = common_parseAtStr(location);
    } else {
        error.atStr = NULL;
    }
    if (description) {
        vsprintf(buffer, description, args);
        error.msg = buffer;
        
        //common_logprintf("Erro:[%s] - cod:[%d] - msg:\"%s\"\n", location, code, buffer);
    } else {
        error.msg = NULL;
        //common_logprintf("Erro:[%s] - cod:[%d]\n", location, code);
    }
    error_queue_push(&errors.items, &error);

    if (error.atStr) {
        free(error.atStr);
    }

    return E_OK;
}



int error_queue_init(error_queue_t *queue)
{
    int i;

    if (!queue) {
        return E_NULL_PARAMETER;
    }

    pthread_mutex_init(&queue->mutex, NULL);

    pthread_mutex_lock(&queue->mutex);
    queue->nextItem = 0;
    for (i = 0; i < ERROR_QUEUE_COUNT; i++) {
        error_itemInit(&queue->items[i]);
    }
    pthread_mutex_unlock(&queue->mutex);

    return E_OK;
}

int error_queue_end(error_queue_t *queue)
{
    int i;

    if (!queue) {
        return E_NULL_PARAMETER;
    }

    pthread_mutex_lock(&queue->mutex);
    for (i = 0; i < ERROR_QUEUE_COUNT; i++) {
        error_itemClear(&queue->items[i]);
    }
    queue->nextItem = 0;
    pthread_mutex_unlock(&queue->mutex);

    pthread_mutex_destroy(&queue->mutex);

    return E_OK;
}

int error_queue_push(error_queue_t *queue, error_t *error)
{
    int ret;

    if (!queue) {
        return E_NULL_PARAMETER;
    }

    pthread_mutex_lock(&queue->mutex);

    // copia todo conteúdo de 'error' para dentro do novo item
    queue->items[queue->nextItem].code = error->code;
    queue->items[queue->nextItem].level = error->level;
    if (error->msg) {
        if (queue->items[queue->nextItem].msg) {
            free(queue->items[queue->nextItem].msg);
        }
        queue->items[queue->nextItem].msg = (char *)malloc(strlen(error->msg)+1);
        sprintf(queue->items[queue->nextItem].msg, "%s\0", error->msg);
    }
    if (error->atStr) {
        if (queue->items[queue->nextItem].atStr) {
            free(queue->items[queue->nextItem].atStr);
        }
        queue->items[queue->nextItem].atStr = (char *)malloc(strlen(error->atStr)+1);
        sprintf(queue->items[queue->nextItem].atStr, "%s\0", error->atStr);
    }

    if (error->level == ERROR_LEVEL_NORMAL) {
        common_logprintf("[ERRO] %06d: \"%s\" (at: %s)", error->code, error->msg, error->atStr);
        //common_logprintf("[ERRO] %06d: \"%s\"", error->code, error->msg);
        //common_logprintf("           at: \"%s\"\n", error->atStr);
    } else {
        common_logprintf("[warn] %06d: \"%s\"", error->code, error->msg);
        //common_logprintf("           at: \"%s\"\n", error->atStr);
    }

    // ajusta ponteiros
    queue->nextItem++;
    if (queue->nextItem == ERROR_QUEUE_COUNT) {
        queue->nextItem = 0;
    }
    ret = pthread_mutex_unlock(&queue->mutex);
    //printf("%d \n\n", ret);

    return E_OK;
}

int error_queue_pop(error_queue_t *queue, char **msg, int *level)
{
    int ret;
    int lastItem;

    if (!queue) {
        return E_NULL_PARAMETER;
    }

    pthread_mutex_lock(&queue->mutex);

    // pega a posição do último erro
    lastItem = queue->nextItem-1;
    if (lastItem < 0) {
        lastItem = ERROR_QUEUE_COUNT-1;
    }

    // item sem código = erro inválido, não existente
    if (queue->items[lastItem].code == E_NONE) {
        ret = E_NONE;

    // erro válido
    } else {
        // preenche parâmetros solicitados
        if (queue->items[lastItem].msg && msg) {
            *msg = (char *)malloc(strlen(queue->items[lastItem].msg)+1);
            sprintf(*msg, "%s\0", queue->items[lastItem].msg);
        }
        if (level) {
            *level = queue->items[lastItem].level;
        }
        ret = queue->items[lastItem].code;

        // libera o item e atualiza a posição do próximo item
        error_itemClear(&queue->items[lastItem]);
        queue->nextItem = lastItem;
    }

    pthread_mutex_unlock(&queue->mutex);

    return ret; // código do erro
}

int error_itemClear(error_t *e)
{
    e->code = E_NONE;
    e->level = ERROR_LEVEL_UNDEFINED;
    if (e->msg) {
        free(e->msg);
        e->msg = NULL;
    }
    if (e->atStr) {
        free(e->atStr);
        e->atStr = NULL;
    }
    return E_OK;
}

int error_itemInit(error_t *e)
{
    e->code = E_NONE;
    e->level = ERROR_LEVEL_UNDEFINED;
    e->msg = NULL;
    e->atStr = NULL;
    return E_OK;
}

