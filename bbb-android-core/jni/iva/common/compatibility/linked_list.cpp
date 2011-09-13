#include "common_leaks.h"
#include <string.h>
#include "common_compatibility.h"
#include "error.h"
#include "linked_list.h"
#include "linked_list_int.h"

linkedList_t * linkedList_create()
{
    linkedList_t * list;
    list = (linkedList_t *) malloc(sizeof(linkedList_t));

    if (!list)
        return NULL;

    if (linkedList_init(list) != E_OK) {
        linkedList_destroy(&list);
        return NULL;
    }

    return list;
}

int linkedList_init(linkedList_t *list)
{
    if (!list) {
        return E_NULL_PARAMETER;
    }
    list->first = NULL;
    list->last = NULL;
    list->size = 0;
    list->actualElem = NULL;
    pthread_mutex_init(&list->mutex, NULL);

    return E_OK;
}

int linkedList_add(linkedList_t *list, void *content)
{
    linkedListElem_t *elem;
    int error = E_OK;

    if (!list || !content) {
        return E_NULL_PARAMETER;
    }

    // cria o novo item da lista
    error = linkedListElem_create(&elem, content);
    if (error != E_OK) {
        return E_ERROR;
    }

    // coloca ele na lista
    if (list->size == 0) {
        list->first = elem;
        list->last = elem;
    } else {
        list->last->next = elem;
        list->last = elem;
    }
    list->size++;

    return E_OK;
}

int _PointerEquals(void *p1, void *p2)
{
    return p1 == p2;
}

int linkedList_removeByPointer(linkedList_t *list, void *content, void (*destroyContent)(void *))
{
    return linkedList_remove(list, content, _PointerEquals, destroyContent);
}

int linkedList_remove(linkedList_t *list, void *content, int (*equals)(void *, void *),
                      void (*destroyContent)(void *))
{
    linkedListElem_t *elem, *next, *elemAnt;
    int error = E_OK;

    if (!list || !content || !equals) {
        return E_NULL_PARAMETER;
    }

    if (list->size == 0) {
        return E_LINKED_LIST_EMPTY;
    }

    // percorre a lista removendo os elementos de acordo com o conteúdo informado
    elemAnt = NULL;
    elem = list->first;
    while (elem) {
        next = elem->next;
        // comparação deu certa, remove o elemento
        if (equals(elem->content, content)) {
            // se tá marcado como atual, pega o próximo para ser o atual
            if (elem == list->actualElem) {
                list->actualElem = elem->next;
            }
            linkedListElem_remove(list, elem, elemAnt);
            linkedListElem_destroy(elem, destroyContent);
        } else {
            elemAnt = elem;
        }
        elem = next;
    }

    return E_OK;
}
/*
int linkedList_removeByElem(linkedList_t *list, linkedListElem_t *elem)
{
    linkedListElem_t *elemAux, *next, *elemAnt;
    int error = E_OK;

    if (!list || !elem) {
        return E_NULL_PARAMETER;
    }

    if (list->size == 0) {
        return E_LINKED_LIST_EMPTY;
    }

    // percorre a lista removendo os elementos de acordo com o conteúdo informado
    elemAnt = NULL;
    elemAux = list->first;
    while (elem) {
        next = elemAux->next;
        // comparação deu certa, remove o elemento
        if (elemAux == elem) {
            // se tá marcado como atual, pega o próximo para ser o atual
            if (elem == list->actualElem) {
                list->actualElem = elem->next;
            }
            linkedListElem_remove(list, elemAux, elemAnt);
            linkedListElem_destroy(elemAux, NULL);
            break; // só um elemento é removido nesta função
        } else {
            elemAnt = elemAux;
        }
        elemAux = next;
    }

    return E_OK;
}
*/
int linkedList_contains(linkedList_t *list, void *content, int (*equals)(void *, void *))
{
    linkedListElem_t *elem;

    if (!list || !content || !equals) {
        return E_NULL_PARAMETER;
    }
    if (list->size == 0) {
        return E_LINKED_LIST_EMPTY;
    }

    // percorre a lista removendo os elementos de acordo com o conteúdo informado
    elem = list->first;
    while (elem) {
        // comparação deu certa, a lista contém o elemento
        if (equals(elem->content, content)) {
            return E_OK;
        }
        elem = elem->next;
    }

    return E_ERROR;
}

int linkedList_get(linkedList_t * list, void *content, void ** result, int (*equals)(void *, void *))
{
    linkedListElem_t *elem;
    *result = NULL;

    if (!list || !content || !equals) {
        return E_NULL_PARAMETER;
    }
    if (list->size == 0) {
        return E_LINKED_LIST_EMPTY;
    }

    // percorre a lista
    elem = list->first;
    while (elem) {
        // comparação deu certa, a lista contém o elemento
        if (equals(elem->content, content)) {
            *result = elem->content;
            return E_OK;
        }
        elem = elem->next;
    }

    return E_ERROR;
}

int linkedList_getCompare(linkedList_t * list, void ** result, int (*compare)(void *, void *))
{
    linkedListElem_t *elem;
    *result = NULL;

    if (!list || !compare) {
        return E_NULL_PARAMETER;
    }
    if (list->size == 0) {
        return E_LINKED_LIST_EMPTY;
    }

    elem = list->first;
    *result = elem->content;

    elem = elem->next;
    while (elem) {
        if (compare(*result, elem->content)) {
            *result = elem->content;
            break;
        }
        elem = elem->next;
    }

    return E_OK;
}

int linkedList_clear(linkedList_t *list, void (*destroyContent)(void *))
{
    linkedListElem_t *elem, *next, *elemAnt;

    if (!list) {
        return E_NULL_PARAMETER;
    }
    if (list->size == 0) {
        return E_LINKED_LIST_EMPTY;
    }

    // percorre a lista removendo os elementos de acordo com o conteúdo informado
    elemAnt = NULL;
    elem = list->first;
    while (elem) {
        next = elem->next;
        linkedListElem_remove(list, elem, elemAnt);
        linkedListElem_destroy(elem, destroyContent);
        elem = next;
    }

    list->actualElem = NULL;

    return E_ERROR;
}

int linkedList_end(linkedList_t *list, void (*destroyContent)(void *))
{
    if (!list) {
        return E_NULL_PARAMETER;
    }
    linkedList_clear(list, destroyContent);
    list->first = NULL;
    list->last = NULL;
    list->size = 0;
    list->actualElem = NULL;

    return E_OK;
}

int linkedList_destroy(linkedList_t **list)
{
    if (!(*list)) {
        return E_NULL_PARAMETER;
    }

    linkedList_clear(*list, NULL);

    pthread_mutex_destroy(&(*list)->mutex);
    free(*list);
    *list = NULL;

    return E_OK;
}

int linkedList_forAll(linkedList_t *list, void (*func)(void *))
{
    linkedListElem_t *elem;

    if (!list || !func) {
        return E_NULL_PARAMETER;
    }

    // percorre a lista chamando a função para cada elemento
    elem = list->first;
    while (elem) {
        func(elem->content);
        elem = elem->next;
    }

    return E_ERROR;
}

void * linkedList_first(linkedList_t *list)
{
    if (!list) {
        error_s(AT, E_NULL_PARAMETER, "Parâmetro 'list' nulo.");
        return NULL;
    }
    if (list->size == 0) {
        error_s(AT, E_LINKED_LIST_EMPTY, "Lista vazia.");
        return NULL;
    }

    list->actualElem = list->first;
    return list->actualElem->content;
}

void * linkedList_last(linkedList_t *list)
{
    if (!list) {
        error_s(AT, E_NULL_PARAMETER, "Parâmetro 'list' nulo.");
        return NULL;
    }
    if (list->size == 0) {
        error_s(AT, E_LINKED_LIST_EMPTY, "Lista vazia.");
        return NULL;
    }

    //list->actualElem = list->last;
    return list->last->content;
}

void * linkedList_next(linkedList_t *list)
{
    if (!list) {
        error_s(AT, E_NULL_PARAMETER, "Parâmetro 'list' nulo.");
        return NULL;
    }
    if (list->size == 0) {
        error_s(AT, E_LINKED_LIST_EMPTY, "Lista vazia.");
        return NULL;
    }

    if (list->actualElem)
        list->actualElem = list->actualElem->next;
    
    if (list->actualElem)
        return list->actualElem->content;
    else
        return NULL;
}

int linkedListElem_create(linkedListElem_t **elem, void *content)
{
    if (!elem || !content) {
        return E_NULL_PARAMETER;
    }
    *elem = (linkedListElem_t *)malloc(sizeof(linkedListElem_t));
    if (!(*elem)) {
        return E_INSUFFICIENT_MEMORY;
    }
    (*elem)->content = content;
    (*elem)->next = NULL;

    return E_OK;
}

int linkedListElem_destroy(linkedListElem_t *elem, void (*destroyContent)(void *))
{
    if (!elem) {
        return E_NULL_PARAMETER;
    }
    if (elem->content && destroyContent) destroyContent(elem->content);
    free(elem);

    return E_OK;
}

int linkedListElem_remove(linkedList_t *list, linkedListElem_t *elem, linkedListElem_t *elemAnt)
{
    if (!list || !elem) {
        return E_NULL_PARAMETER;
    }
    if (list->size == 1) {
        list->first = NULL;
        list->last = NULL;
    } else {
        if (elemAnt) {
            elemAnt->next = elem->next;
        }
        if (list->last == elem) {
            list->last = elemAnt;
        }
        if (list->first == elem) {
            list->first = elem->next;
        }
    }
    list->size--;
    elem->next = NULL;

    return E_OK;
}

int linkedList_size(linkedList_t *list)
{
    if (!list) {
        return -1;
    }
    return list->size;
}

int linkedList_isEmpty(linkedList_t *list)
{
    return linkedList_size(list) == 0;
}


int linkedList_lock(linkedList_t *list)
{
    if (!list)
        return E_NULL_PARAMETER;

    pthread_mutex_lock(&list->mutex);

    return E_OK;
}

int linkedList_unlock(linkedList_t *list)
{
    if (!list)
        return E_NULL_PARAMETER;

    pthread_mutex_unlock(&list->mutex);

    return E_OK;
}
/*
int linkedList_tryLock(linkedList_t *list)
{
    if (!list)
        return E_NULL_PARAMETER;

    if (pthread_mutex_trylock(&list->mutex) == 0)
        return E_OK;
    else
        return E_ERROR;
}
*/
