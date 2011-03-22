#ifndef _ERROR_INT_H_
#define _ERROR_INT_H_

/** \internal
 *  \brief Utilizada pelas funções de erro para guardar um erro gerado.
 *  \param location Informação do local onde o erro ocorreu (macro AT)
 *  \param code Código do erro adicionado.
 *  \param description Mensagem associada ao erro.
 *  \param args Argumento para serem colocados junto com a mensagem
 */
int error_addToQueue(const char *location, int code, int level, const char *description,
                     va_list args);

int error_itemInit(error_t *e);
int error_itemClear(error_t *e);

int error_queue_init(error_queue_t *queue);
int error_queue_end(error_queue_t *queue);
int error_queue_push(error_queue_t *queue, error_t *error);
int error_queue_pop(error_queue_t *queue, char **msg, int *level);

#endif // _ERROR_INT_H_

