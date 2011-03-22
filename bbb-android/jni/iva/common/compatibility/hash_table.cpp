#include "common_leaks.h"
#include <string.h>
#include "common_compatibility.h"
#include "error.h"
#include "hash_table.h"
#include "hash_table_int.h"

int hashTable_init(hashTable_t* table)
{
    int i;

    if(!table) {
        return E_NULL_PARAMETER;
    }

    for (i = 0; i < HASH_TABLE_SIZE; i++) {
        (table->ll)[i] = NULL;
    }
    return E_OK;
}


int hashTable_end(hashTable_t *table, int (*func)(void *))
{
    int i;
    int ret = E_OK;
    int ret2 = E_OK;

    if (!table){ 
        return E_NULL_PARAMETER;
    }

    for(i = 0; i < HASH_TABLE_SIZE; i++) {
        if ((table->ll)[i]) {
            ret = hashTable_endList((table->ll)[i], func);
            if (ret != E_OK) {
                // guarda o erro mas continua destruindo as listas
                ret2 = ret;
            }
            (table->ll)[i] = NULL;
        }
    }
    return ret2;
}

int hashTable_for(hashTable_t *table, int (*func)(void *))
{
    int i;
    int ret = E_OK;
    struct hashTableList_s *list;

    if (!table || !func){ 
        return E_NULL_PARAMETER;
    }

    // para todas posições da hash
    for(i = 0; i < HASH_TABLE_SIZE; i++) {
        list = (table->ll)[i]; // percorre a lista de cada elemento

        // só a chama função para itens preenchidos na tabela
        while (list) { 
            ret = func(list->head->extra);
            if (ret != E_OK) {
                return ret;
            }
            list = list->next;
        }
    }
    return ret;
}

int hashTable_for2(hashTable_t *table, void *param, int (*func)(void *, void *))
{
    int i;
    int ret = E_OK;
    struct hashTableList_s *list;

    if (!table || !func){ 
        return E_NULL_PARAMETER;
    }

    // para todas posições da hash
    for(i = 0; i < HASH_TABLE_SIZE; i++) {
        list = (table->ll)[i]; // percorre a lista de cada elemento

        // só a chama função para itens preenchidos na tabela
        while (list) { 
            ret = func(list->head->extra, param);
            if (ret != E_OK) {
                return ret;
            }
            list = list->next;
        }
    }
    return ret;
}

int hashTable_createEntry(hashTableEntry_t **entry, char *name, void *extra)
{
    if (!entry || !name) {
        return E_NULL_PARAMETER;
    }

    *entry = (hashTableEntry_t *)malloc(sizeof(hashTableEntry_t));
    if (!entry) {
        return E_INSUFFICIENT_MEMORY;
    }
    (*entry)->name = (char *)malloc(strlen(name)+1);
    sprintf_s((*entry)->name, strlen(name)+1, "%s\0", name);
    (*entry)->extra = extra;

    return E_OK;
}


//int hashTable_insert(hashTable_t *table, hashTableEntry_t *entry)
int hashTable_insert(hashTable_t *table, char *name, void *extra)
{
    int hash_value;
    int comp_value;
    hashTableList_t *aux;
    hashTableList_t *anter;
    hashTableList_t *create;
    hashTableEntry_t *entry;
    int error = E_OK;

    if (!table || !name) {
        return E_NULL_PARAMETER;
    }

    // cria a entrada na tabela
    error = hashTable_createEntry(&entry, name, extra);
    if (error != E_OK) {
        return error;
    }

    // faz a hash
    hash_value = hashTable_hashify(entry->name);

    // não há nenhum item nessa posição da tabela
    if (!((table->ll)[hash_value])) {
        error = hashTable_createTableList(&((table->ll)[hash_value]), entry, NULL);
        return error;
    }

    anter = NULL;
    for (aux = (table->ll)[hash_value]; aux != NULL; aux = aux->next) {
        comp_value = hashTable_compare((aux->head)->name, entry->name);

        // já existe um item com esse nome na tabela
        if (comp_value == 0) {
            return E_HASH_TABLE_DUPLICATED_ENTRY;

        } else if (comp_value > 0) {
            break;
        }

        anter = aux;
    }

    if (anter == NULL) {
        error = hashTable_createTableList(&((table->ll)[hash_value]), entry, aux);
        return error;
    } else {
        error = hashTable_createTableList(&create, entry, aux);
        if (error != E_OK) {
            return error;
        }
        anter->next = create;
        return E_OK;
    }
}

int hashTable_print(hashTable_t *table)
{
    int count = 0;
    hashTableList_t *ll_aux;
    int i;

    if (!table) {
        return E_NULL_PARAMETER;
    }

    printf("\n");

    for(i = 0; i < HASH_TABLE_SIZE; i++) {

        if ((table->ll)[i] != NULL) {
            printf("Celula %d:\n", i);
        }

        for (ll_aux = (table->ll)[i]; ll_aux != NULL; ll_aux = ll_aux->next) {
            printf("name: \"%s\", extra (y/n): %c\n", ll_aux->head->name, (ll_aux->head->extra)?'y':'n');
            count++;
        }
    }
    printf("total items: %d\n\n", count);
    return count;
}

hashTableEntry_t *hashTable_lookup(hashTable_t *table, char *name)
{
    int index;
    hashTableList_t *ll_aux;
    int found = 0;

    if (!table || !name) {
        return NULL;
    }

    index = hashTable_hashify(name);

    for (ll_aux = (table->ll)[index]; ll_aux != NULL; ll_aux = ll_aux->next) {
        if (!hashTable_compare(ll_aux->head->name, name)) {
            found = 1;
            break;
        }
    }

    if (found) {
        return (ll_aux->head);
    } else {
        return (NULL);
    }

}




int hashTable_hashify(char *name)
{
    char *p;
    unsigned int h = 0;
    unsigned int g;

    for (p = name; (*p) != HASH_TABLE_EOST; p = p+1) {
        h = (h << 4) + (*p);
        if (g = h & 0xf000000) {
            h = h ^ (g >> 24);
            h = h ^ g;
        }
    }

    return (h % HASH_TABLE_SIZE);  
}


int hashTable_compare(char *str1, char *str2)
{
    char * i1;
    char * i2;

    i1 = str1;
    i2 = str2;

    while ((*i1 != 0) && (*i2 != 0)) {
        if (*i1 != *i2) {
            break;
        }
        i1++;
        i2++;
    }

    return (*i1 - *i2);

}

int hashTable_createTableList(hashTableList_t **enter, hashTableEntry_t *entry, hashTableList_t *next)
{
    *enter = (hashTableList_t *) malloc(sizeof(hashTableList_t));

    if (!(*enter)) {
        return E_NULL_PARAMETER;
    }
    (*enter)->head = entry;
    (*enter)->next = next;

    return E_OK;
}

int hashTable_endList(hashTableList_t *ll, int (*func)(void *))
{
    hashTableList_t *next;

    if (!ll) {
        return E_NULL_PARAMETER;
    }

    next = ll;
    while(next != NULL) {
        ll = ll->next;

        // destrói a entrada e depois a lista em si
        hashTable_endEntry(next->head, func);
		free(next->head);
        free(next);

        next = ll;
    }
    return E_OK;
}

int hashTable_endEntry(hashTableEntry_t *entry, int (*func)(void *))
{
    int error = E_OK;

    if (!entry) {
        return E_NULL_PARAMETER;
    }
    if (entry->name) free(entry->name);

    // se tem dados extras e tem uma função pra liberar a memória deles, chama ela
    if (entry->extra && func) {
        error = func(entry->extra);
    }

    return error;
}