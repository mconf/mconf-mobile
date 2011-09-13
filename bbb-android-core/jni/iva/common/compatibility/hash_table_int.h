#ifndef _HASH_TABLE_INT_H_
#define _HASH_TABLE_INT_H_

#include "hash_table.h"


/** \internal
 *  \brief Função auxiliar para criar uma lista de uma tabela hash antes da inserção de uma entrada.
 *  \param enter Ponteiro para uma lista que será criada.
 *  \param entry Ponteiro para a entrada que será inserida.
 *  \param next Próximo elemento que sera ligado a enter, caso exista.
 *  \return E_OK se sucesso ou o código de erro gerado.
 */
int hashTable_createTableList(hashTableList_t **enter, hashTableEntry_t *entry, hashTableList_t *next);

/** \brief Cria uma entrada em uma tabela hash.
 *  \param entry Entrada que será criada.
 *  \param name Nome da entrada. NÃO pode ser NULL.
 *  \param extra Dados extras da entrada. Pode ser NULL.
 *  \return E_OK se teve sucesso ou o código de erro gerado.
 *
 * Aloca memória para a entrada 'entry' e atribuí 'name' e 'extra' à ela.
 */
int hashTable_createEntry(hashTableEntry_t **entry, char *name, void *extra);

/** \internal
 *  \brief Compara duas strings.
 *  \param str1 uma string.
 *  \param str2 uma string.
 *  \return 0 se os strings são iguais; um valor >0 se str1 sucede str2 ou um valor <0 em caso oposto.
 *
 */
int hashTable_compare(char *str1, char *str2);

/** \internal
 *  \brief Calcula um codigo hash para a string informada.
 *  \param name String para cálculo do hash.
 *  \return Código hash correspondente.
 *
 * Utiliza o algoritmo HPJW (do livro do Dragão).
 */
int hashTable_hashify(char *name);

/** \internal
 *  \brief Destrói uma lista encadeada da tabela hash.
 *  \param ll Lista encadeada.
 *  \param func Função para destruição dos itens em 'extra' das entradas ('head' da lista).
 *
 * Destrói a lista encadeada, incluindo todos os itens ligados à ela (navegação nos itens 'next') e
 * da entrada da tabela hash em 'head'. Também faz um free de 'll'.
 */
int hashTable_endList(hashTableList_t *ll, int (*func)(void *));

/** \internal
 *  \brief Função para destruição de uma entrada da tabela hash.
 *  \param entry Entrada da tabela hash.
 *  \param func Função para liberar a memória da variável 'extra' da entrada.
 *  \return E_OK se sucesso ou o código de erro gerado.
 *
 * Limpa a estrutura internamente e faz um free de 'entry'. Se 'func' for informada,
 * será chamada para liberar a memória dos dados na variável 'extra' de 'entry'.
 * Não destrói 'name', pois normalmente tem alocação estática.
 */
int hashTable_endEntry(hashTableEntry_t *entry, int (*func)(void *));


#endif // _HASH_TABLE_INT_H_
