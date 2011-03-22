#ifndef _LINKED_LIST_H_
#define _LINKED_LIST_H_

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>


typedef struct linkedListElem_s linkedListElem_t;
typedef struct linkedList_s linkedList_t;

/** \struct linkedList_t
 *  \brief Lista encadeada.
 */
struct linkedList_s {
    linkedListElem_t *first;
    linkedListElem_t *last;
    int size;
    linkedListElem_t *actualElem;
    pthread_mutex_t mutex;
};

/**
*   \brief Cria e inicializa a lista encadeada
*   \return ponteiro para a lista se sucesso, NULL se falhou
*/
linkedList_t * linkedList_create();
/**
*   \brief Inicializa a lista encadeada
*   \param list lista que será inicializada. Deve ser previamente alocado
*   \return E_OK se sucesso
*   \return E_NULL_PARAMETER se list for nulo
*/
int linkedList_init(linkedList_t *list);
/**
*   \brief Adiciona um elemento à lista
*   \param list lista em questao
*   \param content ponteiro para a estrutura que sera adicionada à lista
*   \return E_OK se sucesso
*   \return E_NULL_PARAMETER se list ou content forem ponteiros nulos
*   \return E_ERROR se nao conseguir criar um novo elemento
*/
int linkedList_add(linkedList_t *list, void *content);
/**
*   \brief Remove um elemento da lista
*   \param list lista em questao
*   \param content ponteiro para a estrutura que sera usada para comparacao
*   \param equals ponteiro para a funcao de comparacao
*   \param destroyContent ponteiro para a funcao de dealocação da estrutura que sera removida
*   \return E_OK se sucesso
*   \return E_NULL_PARAMETER se list, content ou equals forem ponteiros nulos
*   \return E_LINKED_LIST_EMPTY se list estiver vazio
*/
int linkedList_remove(linkedList_t *list, void *content, int (*equals)(void *, void *),
                      void (*destroyContent)(void *));
int linkedList_removeByPointer(linkedList_t *list, void *content, void (*destroyContent)(void *));
int linkedList_contains(linkedList_t *list, void *content, int (*equals)(void *, void *));
int linkedList_clear(linkedList_t *list, void (*destroyContent)(void *));
int linkedList_end(linkedList_t *list, void (*destroyContent)(void *));
int linkedList_destroy(linkedList_t **list);
int linkedList_forAll(linkedList_t *list, void (*func)(void *));
void * linkedList_first(linkedList_t *list);
void * linkedList_last(linkedList_t *list);
void * linkedList_next(linkedList_t *list);
int linkedList_get(linkedList_t * list, void *content, void ** result, int (*equals)(void *, void *));
int linkedList_getCompare(linkedList_t * list, void ** result, int (*compare)(void *, void *));
int linkedList_size(linkedList_t *list);
int linkedList_isEmpty(linkedList_t *list);

int linkedList_lock(linkedList_t *list);
int linkedList_unlock(linkedList_t *list);
//int linkedList_tryLock(linkedList_t *list);


#endif // _LINKED_LIST_H_
