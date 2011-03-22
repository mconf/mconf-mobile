#ifndef _QUEUE_INT_H_
#define _QUEUE_INT_H_

#include <pthread.h>
#include <list>
using namespace std;

#include "queue.h"


/// \todo Porque essas constantes da netcom tão aqui?
//#define NETCOM_EVENT                  SDL_USEREVENT + 2
//#define NETCOM_RESET_ENCODE           524


#define QUEUE_MAX_CONSUMERS  100                            ///< Número máximo de consumidores em uma queue
#define QUEUE_KBYTE          1024                           ///< Quantos bits há em um KByte
#define QUEUE_BLOCK          (QUEUE_KBYTE*20)               ///< Tamanho (KB) dos blocos de memória
#define QUEUE_CHUNKS         1000                           ///< Quantidade de blocos de memória
#define QUEUE_MEMORY_SYZE    (QUEUE_BLOCK * QUEUE_CHUNKS)   ///< Tamanho máximo (KB) da memória para as queues
#define QUEUE_MAX_BLOCKS     40                             ///< Número máximo de blocos que podem ser alocados

/** \brief Elemento da queue que guarda um buffer de dados e informações
 *         associadas a ele
 */
typedef struct queue_store_s {
    struct queue_store_s *nextstore_p;  ///< Próximo elemento
    struct queue_store_s *prevstore_p;  ///< Elemento anterior
    uint8_t *buffer_p;                  ///< Ponteiro para o buffer de dados
    uint32_t buffersize;                ///< Tamanho do buffer de dados
    uint32_t timestamp;                 ///< Timestamp associado ao buffer de dados
    QueueExtraData * data;              ///< Dados adicionais associados à este item da queue
    int consumers;                      ///< Número de consumidores que ainda não consumiram este elemento
} queue_store_t;

/** \brief Estrutura que representa uma queue
 *
 * <b>Funcionamento básico da queue:</b> \n
 * Uma queue é uma fila de itens que podem ser consumidos por um consumidor (queue_consumer_t).
 * A queue é criada com queue_create() e destruída com queue_destroy(). \n
 * Para pegar dados da queue, é necessário registrar um consumidor com queue_registerConsumer(). A
 * partir daí, basta buscar os dados usando queue_dequeue(). Quando um consumidor não é mais necessário,
 * ele pode ser removido com queue_unregisterConsumer(). \n
 * Para inserir dados na queue não é necessário registrar um consumidor. Mas se ela não possuir
 * nenhum consumidor, o dado inserido será ignorado pela queue, ou seja, não será mantido na queue.
 * Os dados são inseridos com queue_enqueue(). \n
 * A queue utiliza uma área de memória específica para seus dados para otimizar o desempenho. Esta
 * área de memória é alocada em blocos de QUEUE_MEMORY_SYZE (20 MB) bytes sempre que é necessário mais
 * memória. Todos os buffers inseridos na queue devem ter sido alocados com a função queue_malloc(), que
 * aloca um bloco desta área de memória privada da queue.
 * 
 * <b>Passos para inserir dados na queue:</b> \n
 * \li Alocar dados com queue_malloc()
 * \li Inserir dados com queue_enqueue()
 * \li Se a inserção dos dados falhar, dados devem ser desalocados com queue_dealloc()
 *
 * <b>Passos para remover dados da queue:</b> \n
 * \li Registrar consumidor com queue_registerConsumer()
 * \li Remover dados com queue_dequeue()
 * \li Assim que o buffer retirado da queue for usado, ele deve ser liberado com queue_free()
 * \li Repetir os 2 passos acima enquanto deseja-se consumir da queue
 * \li Desregistrar o consumidor com queue_unregisterConsumer() quando não for mais necessário
 * 
 * <b>Como os dados extra funcionam na queue:</b> \n
 * No enqueue, é criada uma CÓPIA do objeto QueueExtraData passado por parâmetro. Esta cópia
 * é guardada junto a uma posição na queue.
 * No dequeue é retornada uma referência a este objeto, que NÃO deve ser deletado fora da queue.
 * O objeto só é desalocado (dentro da queue) quando todos os consumidores registrados já liberaram
 * a posição da queue com a qual o objeto está associado (na função queue_freeDirect()).
 */
typedef struct queue_s {
    queue_store_t *firststore_p;                ///< primeiro elemento da queue
    queue_store_t *laststore_p;                 ///< ultimo elemento da queue
    int length;                                 ///< tamanho da queue (numero de elementos)
    uint32_t size;                              ///< memoria total ocupada pela queue, em bytes
    uint32_t last_timestamp;                    ///< valor do ultimo timestamp adicionado
    uint32_t last_buffersize;                   ///< valor do ultimo buffersize adicionado
    struct queue_consumer_s *firstconsumer_p;   ///< fila de consumidores
    int consumers;                              ///< numero corrente de consumidores
    pthread_mutex_t mtx;                        ///< protege a fila em operacoes criticas  
    pthread_cond_t condVariable;
} queue_t;

/** \brief Consumidor de dados da queue
 */
typedef struct queue_consumer_s {
    struct queue_consumer_s *nextconsumer_p;    ///< Próximo consumidor
    struct queue_s *queue_p;                    ///< Queue da qual o consumidor consome
    struct queue_store_s *store_p;
    int hold;
    uint32_t current_timestamp;                  ///< valor do timestamp atual
} queue_consumer_t;

/** \brief Descritor de um bloco de memória global
 */
typedef struct queue_memoryDesc_s {
    void * initBlock;
    int size;
    int enqueue;
    int busy;
} queue_memoryDesc_t;

/** \brief Descritor da memória global da queue
 */
typedef struct queue_memory_s {
    int qtMemory;
    void * memory_area[QUEUE_MAX_BLOCKS]; ///< ponteiro para a área de memória alocada dinamicamente
    list<queue_memoryDesc_t *> free;      ///< lista para ponteiros de descritores de memória livres
    list<queue_memoryDesc_t *> busy;      /** lista para ponteiros de descritores de pedaços de memória
                                             ocupada */
    pthread_mutex_t queue_mutex;
    int blocks;
} queue_memory_t;

// funcoes privadas

/** \brief Libera todos os elementos da queue
 *  \internal
 */
void queue_flushInternal(queue_t *);

/** \brief Desregistra todos os consumidores da queue
 *  \internal
 */
void queue_unregisterAllConsumers(queue_t *);

/** \brief Aumenta o número de consumidores sempre que um novo é inserido
 *  \internal
 */
void queue_incStoreConsumers(queue_store_t *);

/** \brief Reduz o número de consumidores sempre que um é removido
 *  \internal
 */
void queue_decStoreConsumers(queue_store_t *);

/** \brief Atualiza os consumidores que já consumiram todos os itens par avisá-los
 *         que um novo item foi inserido
 *  \internal
 */
void queue_updateConsumers(queue_t *,queue_store_t *);

/** \brief Libera um elemento da queue que já foi usado por todos consumidores
 *  \internal
 */
void queue_freeDirect(queue_t *, queue_store_t *);

/** \brief Aloca um espaço de memória na memória global da queue
 *  \internal
 */
void *queue_statAlloc(size_t size);

/** \brief Libera um espaço de memória da memória global da queue
 *  \internal
 */
void queue_statFree(void *);

#endif // _QUEUE_INT_H_
