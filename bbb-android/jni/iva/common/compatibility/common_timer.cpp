#include "common_leaks.h"
#include <pthread.h>
#include "common_compatibility.h"
#include "error.h"
#include "common_timer.h"
#include "common_timer_int.h"

common_timer_t * common_timer_create(int type, uint32_t time, int interval,int (*callback)(void *),
                                     void *param)
{
    common_timer_t *timer;
    pthread_attr_t pthreadAttr;

    if (type != COMMON_TIMER_TYPE_FOREVER && type != COMMON_TIMER_TYPE_ONCE) {
        error_s(AT, E_INVALID_PARAMETER, E_MSG_INVALID_PARAMETER, "type");
        return NULL;
    }
    if (!callback) {
        error_s(AT, E_NULL_PARAMETER, E_MSG_NULL_PARAMETER, "callback");
        return NULL;
    }

    // aloca memória para o novo timer
    timer = (common_timer_t *)malloc(sizeof(common_timer_t));
    if (!timer) {
        error_s(AT, E_INSUFFICIENT_MEMORY, E_MSG_INSUFFICIENT_MEMORY, "common_timer_t");
        return NULL;
    }
    timer->timeLast = getTimestamp();
    timer->type = type;
    timer->time = time;
	timer->interval = interval;
    timer->callback = callback;
    timer->callbackParam = param;
    timer->run = true;

    // inicia a thread de chamadas do timer
    pthread_attr_init(&pthreadAttr);
    pthread_attr_setdetachstate(&pthreadAttr, PTHREAD_CREATE_JOINABLE);
    pthread_create(&timer->thread, &pthreadAttr, common_timer_thread, (void *)timer);
    pthread_attr_destroy(&pthreadAttr);

    return timer;
}

void * common_timer_thread(void *param)
{
    common_timer_t *timer;
    unsigned int elapsed;
    int sleepTime;
    timer = (common_timer_t *)param;

    while (timer->run) {
        // dorme o tempo setado menos o tempo que passou desde a última vez
        // que dormiu
        elapsed = getTimestamp() - timer->timeLast; // tempo passado desde o último sleep
        //printf("timer: time %d, elapsed %d \n", timer->time, elapsed);
        sleepTime = timer->time - elapsed;
        while (sleepTime > timer->interval) {
            if (!timer->run) break;
            common_sleep(timer->interval);
            sleepTime -= timer->interval;
        }

        if (!timer->run) break;

        common_sleep(sleepTime);
        timer->timeLast = getTimestamp();

        // pra garantir que a thread está rodando
        if (!timer->run) break;

        // chama a função setada no timer
        if (timer->callback) {
            // se a função retorna algo != E_OK, para de executar o timer
            if (timer->callback(timer->callbackParam) != E_OK) {
                break;
            }
        }

        // se é timer para ser executado só uma vez, finaliza a thread
        if (timer->type == COMMON_TIMER_TYPE_ONCE) {
            break;
        }
    }
    timer->run = false;
    return NULL;
}

int common_timer_destroy(common_timer_t **timer)
{
    int joinRet;

    if (!timer) {
        error_s(AT, E_NULL_PARAMETER, E_MSG_NULL_PARAMETER, "timer");
        return E_NULL_PARAMETER;
    }

    if (!(*timer)) {
        error_s(AT, E_NULL_PARAMETER, E_MSG_NULL_PARAMETER, "*timer");
        return E_NULL_PARAMETER;
    }

    // para garantir que a thread está mesmo executando
    if ((*timer)->run) {

        // seta a flag pra acabar a thread e fica esperando ela acabar
        (*timer)->run = false;
        joinRet = pthread_join((*timer)->thread, NULL);
        if (joinRet != 0) {
            error_s(AT, E_PTHREAD_JOIN_ERROR, E_MSG_PTHREAD_JOIN_ERROR, joinRet);
            return joinRet;
        }
    }

    free(*timer);
    (*timer) = NULL;

    return E_OK;
}

