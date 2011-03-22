#ifndef _HASH_TABLE_H_
#define _HASH_TABLE_H_

#include <stdio.h>
#include <stdlib.h>

#define HASH_TABLE_SIZE      211    ///< Tamanho da tabela hash
#define HASH_TABLE_EOST      0



/** \struct hashTableEntry_t
 *  \brief Entrada da tabela hash.
 *
 * Cada entrada da tabela é obrigatóriamente associada a um nome 'name' e 
 * pode possuir dados adicionais na variável 'extra'.
 */
typedef struct {
    char* name;         /**< Um string que representa o nome da entrada (dado utilizado para o hashify). */
    void* extra;        /**< Qualquer informacao extra. */
} hashTableEntry_t;


/** \struct hashTableList_t
 *  \brief Tipo abstrato de uma lista encadeada de entradas na tabela Hash
 *         (para tratamento de colisões)
 *  \todo Retirar essa lista e usar alguma lista genérica da lib 'geral'
 */
typedef struct hashTableList_s {
    hashTableEntry_t *head;            /**< referencia a uma entrada na tabela. */
    struct hashTableList_s *next;      /**< referencia pro próximo elemento da lista. */
} hashTableList_t;


/** \struct hashTable_t
 *  \brief Estrutura da tabela hash.
 *
 * A tabela hash é um conjunto de listas. As entradas são inseridas na primeira posição da lista e,
 * quando ocorre colisão, são inseridas em ordem, de acordo com a função que compara o nome das 
 * entradas.
 */
typedef struct hashTable_s {
    hashTableList_t *ll[HASH_TABLE_SIZE];	/**< referencia a um vetor de listas encadeadas. */
} hashTable_t;



/** \brief Inicializa a tabela Hash.
 *  \param table Tabela hash que deve ser inicializada.
 *  \return E_OK se teve sucesso ou o código de erro gerado.
 *
 * Inicializa a tabela hash que já deve ter sido alocada anteriormente (ou seja, não faz malloc).
 */
int hashTable_init(hashTable_t *table);

/** \brief Destrói uma tabela Hash.
 *  \param table Tabela que deve ser destruída.
 *  \param func Função para destruir os dados extras nas entradas da tabela.
 *
 * Destrutor da estrutura de dados. Deve ser chamado pelo usuario no 
 * fim de seu uso de uma tabela hash. Destrói as variáveis internas 
 * estrutura, mas NÃO dá um free() na memória da tabela hash. Também não destrói
 * os nomes das entradas (pois normalmente tem alocação estática).
 */
int hashTable_end(hashTable_t *table, int (*func)(void *));

/** \brief Percorre os elementos preenchidos de uma tabela hash.
 *  \param table Tabela que será percorrida.
 *  \param func Função chamada para cada elemento da tabela.
 *
 * Somente os elementos preenchidos na tabela que são percorridos. A função 'func'
 * é chamada tendo como parâmetro o dado em 'extra' de cada elemento da tabela.
 * Se 'func' retornar diferente de E_OK, a função para a execução e retorna o mesmo
 * erro gerado em 'func'.
 */
int hashTable_for(hashTable_t *table, int (*func)(void *));
int hashTable_for2(hashTable_t *table, void *param, int (*func)(void *, void *));

/** \brief Retorna um ponteiro para entrada da tabela hash associada ao nome informado.
 *  \param table Tabela hash.
 *  \param name Nome da entrada procurada.
 *  \return Ponteiro para a entrada associada a 'name', ou NULL se 'name'
 *          não foi encontrado na tabela.
 */
hashTableEntry_t * hashTable_lookup(hashTable_t *table, char *name);

/** \brief Insere uma entrada em uma tabela hash.
 *  \param table Tabela hash.
 *  \param name Nome da nova entrada
 *  \param extra Dados extras para a nova entrada.
 *  \return E_OK se teve sucesso ou o código de erro gerado.
 *
 * Cria uma nova entrada e insere-a na tabela hash. A entrada é criada
 * pela função hashTable_createEntry().
 */
int hashTable_insert(hashTable_t *table, char *name, void *extra);

/** \brief Imprime o conteudo de uma tabela hash.
 *  \param table Tabela hash a ser imprimida.
 *  \return O número de entradas da tabela.
 */
int hashTable_print(hashTable_t *table);




#endif // _HASH_TABLE_H_
