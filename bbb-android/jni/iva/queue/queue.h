#ifndef _QUEUE_H_
#define _QUEUE_H_

#include <common.h>
#include "QueueExtraData.h"

typedef struct queue_s queue_t;
typedef struct queue_consumer_s queue_consumer_t;


/** \brief Cria, inicializa e retorna uma nova fila de dados
 *  \return Ponteiro para a nova fila criada, em caso de sucesso,
 *          ou NULL caso contrario.
 */
queue_t *queue_create();

/** \brief Destrói uma fila e seta seu ponteiro para NULL
 *  \param[in,out] q Ponteiro para o ponteiro da queue que será destruída
 *  \return E_OK em caso de sucesso ou o código de erro gerado
 *  \retval E_COMMON_NULL_PARAMETER Parâmetro da função é nulo
 *  \retval E_THREAD_MUTEX_LOCK Não foi possível dar lock no mutex da queue
 */
int queue_destroy(queue_t **q);

/** \brief Cria e registra um consumir na fila escolhida
 *  \param[in] q Queue da qual o consumidor irá consumir os dados
 *  \return Ponteiro para o consumidor criado ou NULL em caso de erro
 *
 * Este consumidor criado é utilizado para retirar dados da queue com queue_dequeue().
 * A partir da chamada dessa função, o consumidor já estará ativo. Ou seja, se a queue
 * já está recebendo dados, este consumidor precisa começar a consumir imediatamente
 * ou a queue apenas receberá dados e nunca irá removê-los (até o ponto onde não há
 * mais memória disponível para dados).
 */
queue_consumer_t *queue_registerConsumer(queue_t *q);

/** \brief Desregistra e destrói um consumidor da queue. Atribui NULL no ponteiro do consumidor.
 *  \param[in,out] consumer Consumidor que será removido da queue
 *  \return E_OK em caso de sucesso ou o código de erro gerado
 *  \retval E_COMMON_NULL_PARAMETER Parâmetro da função é nulo
 *  \retval E_THREAD_MUTEX_LOCK Não foi possível dar lock no mutex da queue
 *
 * Desregistrado, o consumidor deixa de existir. Se uma queue possui apenas um
 * consumidor, ao desregistrá-lo a queue perde todos seus itens e não aceita mais
 * nenhum novo item até que ela possua um consumidor.
 */
int queue_unregisterConsumer(queue_consumer_t **consumer);

/** \brief Adiciona um buffer de dados na queue
 *  \param[in] queue_p Queue onde os dados serão inseridos
 *  \param[in] buffer_p Buffer dos dados que serão adicionados
 *  \param[in] buffersize Tamanho (em bytes) do buffer \p buffer_p
 *  \param[in] timestamp Timestamp associado ao buffer
 *  \param[in] extraData Dados extra associados ao buffer
 *  \return E_OK em caso de sucesso ou o código de erro gerado
 *  \retval E_COMMON_NULL_PARAMETER Parâmetro da função é nulo
 *  \retval E_THREAD_MUTEX_LOCK Não foi possível dar lock no mutex da queue
 *  \retval E_COMMON_MEMORY_ERROR Não há mais memória disponível
 *
 * O buffer \p buffer_pp deve ser alocado com a função queue_malloc(), que reserva
 * uma área de dados que só deve ser usada pela queue.
 * Em \p extraData deve existir um objeto previamente alocado (ou um objeto local)
 * com as informações adicionais associadas ao buffer de dados.
 * Os dados de \p extraData serão copiados para dentro de um novo objeto que será
 * armazenado (e destruído posteriormente) pela própria queue.
 */
int queue_enqueue(queue_t *queue_p, uint8_t *buffer_p, uint32_t buffersize, uint32_t timestamp,
                  QueueExtraData * extraData);


/** \brief Retira um buffer da fila
 *  \param[in] consumer_p Consumidor que irá buscar os dados
 *  \param[out] buffer_pp Ponteiro para um buffer onde será colocado o ponteiro para
 *         o buffer de dados
 *  \param[out] bufersize_p Tamanho do buffer de dados em bytes
 *  \param[out] timestamp_p Timestamp associado ao buffer
 *  \param[out] extraData Dados extra associados ao buffer
 *  \return E_OK em caso de sucesso ou o código de erro gerado
 *  \retval E_QUEUE_INVALID_CONSUMER Consumidor é nulo ou não possui uma queue associada
 *  \retval E_THREAD_MUTEX_LOCK Não foi possível dar lock no mutex da queue
 *  \retval E_QUEUE_EMPTY Queue está vazia
 *  \retval E_QUEUE_ENDOF Atingiu o fim da fila
 *  \retval E_QUEUE_FREE_NEEDED Último buffer retirado da queue não foi liberado ainda
 *
 * Este função retorna imediatamente mesmo que a queue não possua dados. Para uma função
 * bloqueante, usar queue_dequeueCond(). \n\n
 *
 * O buffer \p buffer_pp é um buffer na área de memória da queue, portanto o chamador
 * deve deselocar este buffer com a função queue_free() após ele ter sido usado. \n
 * Em \p extraData é colocado o ponteiro para os dados extra da queue caso existam.
 * Ou seja, não são copiados os dados e não é criado um novo objeto. É só colocada
 * uma referência. O objeto em si é destruído pela queue (quando o buffer associado a
 * ele for liberado com queue_free()), não deve ser destruído pelo chamador da função.
 *
 */
int queue_dequeue(queue_consumer_t *consumer_p, uint8_t **buffer_pp, uint32_t *buffersize_p,
                  uint32_t *timestamp_p, QueueExtraData ** extraData);

/** \brief Retira um buffer da fila. Bloqueia até que um buffer esteja disponível.
 *  \param[in] consumer_p Consumidor que irá buscar os dados
 *  \param[out] buffer_pp Ponteiro para um buffer onde será colocado o ponteiro para
 *         o buffer de dados
 *  \param[out] bufersize_p Tamanho do buffer de dados em bytes
 *  \param[out] timestamp_p Timestamp associado ao buffer
 *  \param[out] extraData Dados extra associados ao buffer
 *  \return E_OK em caso de sucesso ou o código de erro gerado
 *  \retval E_QUEUE_INVALID_CONSUMER Consumidor é nulo ou não possui uma queue associada
 *  \retval E_THREAD_MUTEX_LOCK Não foi possível dar lock no mutex da queue
 *  \retval E_QUEUE_EMPTY Queue está vazia
 *  \retval E_QUEUE_FREE_NEEDED Último buffer retirado da queue não foi liberado ainda
 *
 * Faz a mesma coisa que a função queue_dequeue(), com a única diferença que bloqueia o
 * processo (em uma variável de condição) até que exista algum dado disponível na queue. \n
 * É utilizada em threads que apenas removem dados da queue e tratam esses dados. Essa thread
 * pode chamar queue_dequeueCond() e ela será "avisada" (função é desbloqueada) quando dados
 * chegarem na queue.
 *
 */
int queue_dequeueCond(queue_consumer_t *consumer_p, uint8_t **buffer_pp, uint32_t *buffersize_p,
                      uint32_t *timestamp_p,  QueueExtraData ** extraData);

/** \brief Envia um sinal para todos os consumidores da queue. Este sinal é usado para
 *         desbloquear consumidores bloqueados em chamadas queue_dequeueCond().
 *  \param[in] queue Queue com os consumidores que serão acordados
 */
int queue_broadcast(queue_t * queue);

/** \brief Remove todos os itens da queue para determinado consumidor
 *  \param[in] t Consumidor do qual serão removidos todos os itens
 *  \return E_OK em caso de sucesso ou o código de erro gerado
 *  \retval E_QUEUE_INVALID_CONSUMER Consumidor é nulo ou não possui uma queue associada
 *  \retval E_THREAD_MUTEX_LOCK Não foi possível dar lock no mutex da queue
 */
int queue_flush(queue_consumer_t *t);

/** \brief Retorna a quantidade total de elementos da queue
 *  \param[in] q Queue
 *  \return Quantidade de elementos na queue
 */
int queue_length(queue_t *q);

/** \brief Retorna a quantidade de elementos da queue para determinado consumidor
 *  \param[in] temp Consumidor da queue
 *  \return Quantidade de elementos na queue para determinado consumidor
 *
 * Retorna quantos elementos o consumidor ainda não consumiu. Pode ser um valor menor
 * do que o retornado por queue_length() caso o consumidor passado já tenha retirado
 * elementos da queue.
 */
int queue_lengthCons(queue_consumer_t *temp);

/** \brief Retorna a quantidade de memória ocupada pela queue (em bytes)
 *  \param[in] queue Queue que será consultada
 *  \return Número de bytes em memória que a queue está ocupando
 */
uint32_t queue_size(queue_t *queue);

/** \brief Retorna o último timestamp inserido na queue (o mais recente)
 *  \param[in] queue Queue que será consultada
 *  \return Último timestamp inserido na queue
 */
uint32_t queue_getLastTimestamp(queue_t *queue);

/** \brief Retorna o tamanho (em bytes) do último buffer inserido na
 *         queue (o mais recente)
 *  \param[in] queue Queue que será consultada
 *  \return Tamanho (em bytes) do último buffer inserido na queue
 */
uint32_t queue_getLastBuffersize(queue_t *queue);

/** \brief Aloca um buffer de dados na área de memória interna da queue
 *  \param[in] size Tamanho em bytes do buffer a ser alocado
 *  \return Ponteiro para a área de dados alocada ou NULL em caso de erro
 *
 * Esta função só deve ser utilizada para alocar buffers que serão utilizados
 * pela queue! Não deve ser utilizada para outros tipos de alocações. \n
 * Quando é chamada, os dados não são necessariamente alocados em memória. Se
 * a área de memória da queue já possui tamanho, ela simplesmente retornará um
 * bloco interno do tamanho solicitado. Se a área da queue não tem o espaço
 * necessário, ela aloca mais um bloco de memória conforme política de alocação
 * da queue (ver queue_t).
 */
void *queue_malloc(size_t size);

/** \brief Libera um buffer de dados
 *  \param[in] consumer Consumidor que irá liberar o buffer
 *
 * Esta função apenas informa para a fila que o usuário não precisa mais do
 * buffer, o qual será desalocado em momento oportuno, \b internamente,
 * quando todos os usuários registrados já o tiverem liberado. \n
 *
 * \note Esta função deve ser chamada obrigatóriamente, imediatamente após o
 *       uso do buffer
 */
void queue_free(queue_consumer_t *consumer);

/** \brief Libera um buffer previamente alocado com queue_malloc() que não
 *         tenha sido utilizado pela queue
 *  \param[in] vp Buffer que será desalocado
 *
 * \note O buffer que está sendo liberado \b não pode ter sido inserido na queue,
 *       ou seja, não pode ter sido feito um queue_enqueue() desde buffer!
 */
void queue_dealloc(void *vp);

/** \brief Retorna a quantidade de memória (em bytes) alocada para todas as queues
 *         atualmente existentes
 *  \return Quantidade de memória (em bytes) alocada para as queues
 *
 * O valor retornado é o total de bytes da memória do computador que está alocado
 * para uso pelas queues. Este espaço não necessariamente está todo sendo usado, mas
 * está todo alocado em memória.
 */
int queue_memInUse(void);

/** \brief Retorna o percentual de memória alocada que está sendo utilizada
 *  \return Percentual de memória em uso
 *
 * O valor retornado indica qual o percentual da memória atualmente alocada (o
 * valor obtido em queue_memInUse()) que está em uso pelas queues.
 */
float queue_memPercent(void);

/** \brief Imprime dados sobre a memória em uso e livre das queues
 */
void queue_printMemory();

/** \brief Libera a memória global da queue. Deve ser chamada apenas depois que
 *         todas queues forem destruídas e que nenhuma será criada novamente.
 */
void queue_appFinish();

/** \brief Retorna se a queue possui algum consumidor registrado ou não
 *  \param[in] queue Queue que será consultada
 *  \return 1 se a queue possui algum consumidor e 0 se não possui nenhum
 *
 */
int queue_hasConsumers(queue_t * queue);


#endif //_QUEUE_H_


