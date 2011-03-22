#ifndef _COMMON_TIMER_H_
#define _COMMON_TIMER_H_

#include <pthread.h>
#include "common_compatibility.h"


#define COMMON_TIMER_TYPE_ONCE					1
#define COMMON_TIMER_TYPE_FOREVER				2
#define COMMON_TIMER_DEFAULT_SLEEP_INTERVAL		80


/** \struct common_timer_t
 *  \brief Timer.
 *
 * Um timer chama uma função de callback a cada intervalo de tempo setado. Ele pode ser de
 * execução única (chama o callback apenas uma vez).
 */
typedef struct {
    int type;                 ///< Tipo do timer
    uint32_t time;            ///< Tempo de espera em ms
	int interval;			  ///< Intervalo de tempo que a thread testa se ela foi destruida
    pthread_t thread;         ///< Thread principal
    int (*callback)(void *);  ///< Função de callback
    void *callbackParam;      ///< Parâmetro a ser passado para a função de callback
    bool run;                 ///< Flag que indica se a thread está rodando ou não

    unsigned int timeLast;    ///< Tempo em que foi inicializado o timer
} common_timer_t;

/** \brief Cria um novo timer e inicia sua execução
 *  \param type Tipo do timer: para executar apenas uma vez ou infinitamente
 *  \param time Intervalo entre as chamadas do callback (em ms)
 *  \param callback Função de callback chamada a cada 'time' milisegundos
 *  \param param Parâmetro a ser passado para a função de callback
 *  \return E_OK se sucesso ou o código de erro gerado.
 *
 * Cria uma nova estrutura de timer e inicia a execução de sua thread.
 * 'type' pode assumir os valores COMMON_TIMER_TYPE_ONCE ou COMMON_TIMER_TYPE_FOREVER. No
 * primeiro, a função de callback é chamada apenas 1 vez após 'time' ms e o timer é
 * finalizado. No segundo, a função de callback é chamada a cada 'time' ms até que
 * o timer seja explicitamente finalizado.
 */
common_timer_t * common_timer_create(	int type,
										uint32_t time,
										int interval,
										int (*callback)(void *),
										void *param);

/** \brief Encerra a execução de um timer
 *  \param timer Estrutura do timer que será encerrado
 *  \return E_OK se sucesso ou o código de erro gerado.
 *
 * Para a execução da thread do timer e libera a memória de 'timer'.
 * Esta função pode demorar até o intervalo setado no timer para retornar, pois
 * ela só é finalizada quando a thread do timer parar sua execução.
 */
int common_timer_destroy(common_timer_t **timer);



#endif // _COMMON_TIMER_H_
