#ifndef _LINKED_LIST_INT_H_
#define _LINKED_LIST_INT_H_

#include "linked_list.h"

/** \struct linkedListElem_t
 *  \brief Elemento de uma lista encadeada.
 */
struct linkedListElem_s {
    linkedListElem_t *next;
    void *content;
};



int linkedListElem_create(linkedListElem_t **elem, void *content);
int linkedListElem_destroy(linkedListElem_t *elem, void (*destroyContent)(void *));
int linkedListElem_remove(linkedList_t *list, linkedListElem_t *elem, linkedListElem_t *elemAnt);

int _PointerEquals(void *p1, void *p2);

#endif // _LINKED_LIST_INT_H_
