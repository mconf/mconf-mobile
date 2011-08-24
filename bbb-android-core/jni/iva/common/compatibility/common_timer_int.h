#ifndef _COMMON_TIMER_INT_H_
#define _COMMON_TIMER_INT_H_


/** \brief Thread principal de execução do timer
 *  \param param Estrutura common_timer_t que representa o timer desta thread
 *  \return NULL em qualquer caso
 *
 * A thread é executada até que 'param->run' seja 'false', o que deve ser feito apenas pela
 * função que destrói o timer.
 * Esta thread chama a função de callback do timer a cada 'param->time' milisegundos. No caso de
 * timer de execução única (COMMON_TIMER_TYPE_ONCE), o callback é chamado uma vez e a thread
 * é finalizada.
 */
void * common_timer_thread(void *param);



#endif // _COMMON_TIMER_INT_H_
