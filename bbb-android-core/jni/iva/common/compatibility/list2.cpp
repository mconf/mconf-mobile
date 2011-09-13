#include "common_leaks.h"
#include <stdlib.h>
#include "error.h"
#include "list_int.h"
#include "list2.h"

int list2_init(struct list2 * listContext) 
{
    // Todos os parâmetros são nulos.
    listContext->first = NULL;
    listContext->last = NULL;
    listContext->current = NULL;
    
    // Nenhum elemento na lista.
    listContext->numElements = 0;
    
    return E_OK;
}

int list2_end(struct list2 * listContext) 
{
    list2_clean(listContext);

    // limpa dados internos
    listContext->first = NULL;
    listContext->last = NULL;
    listContext->current = NULL;
    listContext->numElements = 0;

    return E_OK;
}

struct list2_element * createElement(void * data, struct list2_element * previous,
                                    struct list2_element * next, int * errorCode)
{
    
    struct list2_element * newElement;
    
    // Aloca memória para um novo elemento.
    newElement = (struct list2_element *) malloc(sizeof(struct list2_element));
    if (!(newElement)) {
        *errorCode = E_MEMORY_ERROR;
        return NULL;
    };
    
    // Grava dados passados por parâmetro.
    newElement->previous = previous;
    newElement->next = next;
    newElement->elementData = data;
    
    // Elemento anterior aponta para novo elemento
    if (previous) {
        previous->next = newElement;
    };
    
    return newElement;
    
};

int list2_addElement(struct list2 * listContext, void * data) 
{

    int error;
    
    // Testa dados de entrada.
    if ( (!(data)) || (!(listContext)) ) {
        return E_NULL_PARAMETER;
    };
    
    // Cria elemento com parâmetros específicos.
    listContext->last = createElement(data, listContext->last, NULL, &error);
    if (!(listContext->last)) {
        return error;
    };
    
    // Se não existe o primeiro elemento, o último e o primeiro serão o mesmo.
    if (!(listContext->first)) {
        listContext->first = listContext->last;
    };
    
    // Incrementa número de elementos na lista.
    (listContext->numElements)++;

    return E_OK;

};

int cleanElement(struct list2_element * element) 
{

    // Limpa elemento.
    free(element);

    return E_OK;

};
 
int list2_updateCurrentElement(struct list2 * listContext, void * data) 
{

    struct list2_element * element;

    // Verifica se os dados são válidos
    if ( (!(listContext)) || (!(listContext->current)) ) { 
        return E_NULL_PARAMETER;
    };

    // Elemento atual da lista
    element = listContext->current;

	element->elementData = data;

    return E_OK;

};

int list2_removeCurrentElement(struct list2 * listContext) 
{

    int error;
    struct list2_element * element;

    // Verifica se os dados são válidos
    if ( (!(listContext)) || (!(listContext->current)) ) { 
        return E_NULL_PARAMETER;
    };

    // Elemento atual da lista
    element = listContext->current;

    // Elemento anterior aponta para o próximo elemento
    if (element->previous) {
        (element->previous)->next = element->next;
    } else {
        listContext->first = element->next;
    };

    // Elemento anterior aponta para o próximo elemento
    if (element->next) {
        (element->next)->previous = element->previous;
    } else {
        listContext->last = element->previous;
    };

    // O elemento corrente passa a ser o próximo elemento.
    listContext->current = element->next;

    // Limpa o elemento
    error = cleanElement(element);

    // Se ocorrer erro limpando o elemento.
    if (error) {
        return error;
    };

    // Decrementa o número de elementos.
    (listContext->numElements)--;

    if (!(listContext->current)) {
        return E_NO_MORE_ELEMENTS;
    };

    return E_OK;

};

int list2_findElement(struct list2 * listContext, void * queryData) 
{

    struct list2_element * element;

    // Verifica se os dados são válidos
    if ( (!(queryData)) || (!(listContext))) { 
        return E_NULL_PARAMETER;
    };
    
    // Primeiro elemento da lista.
    element = listContext->first;

    // Procura o elemento
    while ((element != NULL) && (element->elementData != queryData)) {
        element = element->next;
    };
    
    // Se elemento não encontrado
    if (!(element)) {
        return E_ELEMENT_NOT_FOUND;
    };

    // Salva o elemento como elemento corrente.
    listContext->current = element;

    return E_OK;

};

int list2_firstElement(struct list2 * listContext) 
{

    // Testa parâmetros
    if (!(listContext)) {
        return E_NULL_PARAMETER;
    };

    // Testa se há um primeiro elemento da lista.
    if (!(listContext->first)) {
        return E_NO_ELEMENTS;
    };

    // Torna o primeiro elemento da lista em elemento corrente.
    listContext->current = listContext->first;

    return E_OK;

};

int list2_lastElement(struct list2 * listContext) 
{

    // Testa parâmetros
    if (!(listContext)) {
        return E_NULL_PARAMETER;
    };

    // Testa se há um último elemento da lista.
    if (!(listContext->last)) {
        return E_NO_ELEMENTS;
    };

    // Torna o primeiro elemento da lista em elemento corrente.
    listContext->current = listContext->last;

    return E_OK;

};

int list2_nextElement(struct list2 * listContext) 
{

    // Testa parâmetros
    if (!(listContext)) {
        return E_NULL_PARAMETER;
    };

    // Testa se a lista já está sendo varrida.
    if (!(listContext->current)) {
        return E_NULL_PARAMETER;
    };

    // Avança um elemento na lista.
    listContext->current = (listContext->current)->next;

    // Para o caso de não haver mais elementos na lista.

    if (!(listContext->current)) {
        return E_NO_MORE_ELEMENTS;
    };

    return E_OK;

};

int list2_previousElement(struct list2 * listContext) 
{

    // Testa parâmetros
    if (!(listContext)) {
        return E_NULL_PARAMETER;
    };

    // Testa se a lista já está sendo varrida.
    if (!(listContext->current)) {
        return E_NULL_PARAMETER;
    };

    // Retrocede um elemento na lista.
    listContext->current = (listContext->current)->previous;

    // Para o caso de não haver mais elementos na lista.
    if (!(listContext->current)) {
        return E_NO_MORE_ELEMENTS;
    };

    return E_OK;

};

void * list2_getCurrentElement(struct list2 * listContext)
{

    // Se não existe elemento corrente setado.
    if (!(listContext->current)) {
        return NULL;
    };

    // Retorna o elemento corrente.
    return (listContext->current)->elementData;

};

int list2_length(struct list2 * listContext) {

    return listContext->numElements;

};

int list2_clean(struct list2 * listContext) {

    int err;

    err = list2_firstElement(listContext);

    while (!err) {
        err = list2_removeCurrentElement(listContext);
    };

    if (err != E_NO_MORE_ELEMENTS) {
        return err;
    };

    err = list2_init(listContext);

    return err;
	
};
