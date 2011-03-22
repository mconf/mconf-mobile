#ifndef _QUEUE_BUFFER_H_
#define _QUEUE_BUFFER_H_

#include "queue.h"
#include <queue>
using namespace std;

/** \brief Elemento do buffer da queue (QueueBuffer)
 */
class QueueBufferItem {
private:
    uint8_t *buffer_;               ///< Buffer de dados
    uint32_t buffersize_;           ///< Tamanho do buffer
    uint32_t timestamp_;            ///< Timestamp do buffer
    QueueExtraData * extraData_;    ///< Dados adicionais associados ao buffer
public:
    QueueBufferItem(uint8_t *buffer, uint32_t buffersize, uint32_t timestamp,
              QueueExtraData * extraData);
    ~QueueBufferItem();

    uint8_t * getBuffer();
    uint32_t getBuffersize();
    uint32_t getTimestamp();
    QueueExtraData * getExtraData();
};

/** \brief Classe para bufferizar items na queue
 *
 * Classe utilizada para bufferizar o acesso a queue. Pode ser utilizada para garantir que,
 * por exemplo, seja feito um buffer de 3 elementos na fila de áudio.
 *
 * O buffer tem um tamanho que indica o número de elementos que ele bufferiza. Este tamanho é
 * passado na inicialização do objeto e pode ser setado com setSize(). \n
 * No primeiro acesso à queue, o QueueBuffer garante que o primeiro elemento só poderá ser consumido
 * depois que ouverem \a size elementos na queue. Após o primeiro elemento ser consumido, essa
 * restrição é removida e os dados podem ser consumidos enquanto ouverem dados na queue. \n
 * Sempre que o tamanho da queue chegar a zero, a restrição é imposta novamente, e o próximo
 * elemento só poderá ser removido quando a queue possuir \a size elementos.
 *
 * <b>Exemplo de uso:</b>
 * \li Criar o objeto e setar o tamanho do buffer com setSize()
 * \li Remover elementos da queue com dequeue(), passando um consumidor registrado na
 *     queue da qual se deseja remover o elemento
 * \li Liberar buffers utilizados com freeItem()
 * \li Repetir os 2 passos acima enquanto se deseja remover itens da queue
 *
 * \note O QueueBuffer não está diretamente associado a nenhuma queue. Sua associação é
 *       apenas através dos consumidores (queue_consumer_t) passados por parâmetro nas
 *       funções da classe.
 *
 * \note A classe não impede que o buffer seja utilizado com mais de uma queue, mas não é
 *       aconselhável. Elementos no buffer poderão ser elementos de mais de uma queue.
 */
class QueueBuffer {
private:
    int size_;                                      ///< Número de elementos que devem ser bufferizados
    bool flagWait_;                                 ///< Flag que indica se deve esperar o tamanho da queue atingir \p size_
    QueueBufferItem * queueItem_;                   ///< Elemento atualmente sendo consultado
    queue<QueueBufferItem *> buffer_;               ///< Buffer com os elementos bufferizados

    /** \brief Tenta encher o buffer com dados da queue
     */
    int tryFillBuffer(queue_consumer_t *consumer);

public:
    QueueBuffer(int size);
    ~QueueBuffer();

    /** \brief Seta o novo tamanho do buffer
     *  \param[in] value Número de elementos a serem bufferizados
     */
    void setSize(int value);

    /** \brief Busca o número de elementos a serem bufferizados
     *  \return Número de elementos a serem bufferizados
     *
     * \note Não é o número de elementos atualmente no buffer, mas sim o número
     *       de elementos alvo, setado com setSize().
     */
    int getSize();

    /** \brief Indica se o buffer está cheio ou não
     *  \return \p true se o buffer está cheio e \p false se não está
     */
    bool isFull();

    /** \brief Libera um item (corresponde ao queue_free())
     *  \param[in] consumer Consumidor que irá liberar um item
     */
    void freeItem(queue_consumer_t *consumer);

    /** \brief Busca um item na queue (corresponde ao queue_dequeue())
     *  \param[in] consumer Consumidor que irá liberar um item
     *  \param[out] oBuffer Buffer de dados retirado da queue
     *  \param[out] oBuffersize Tamanho (KB) do buffer
     *  \param[out] oTimestamp Timestamp associado ao buffer
     *  \param[out] oExtraData Dados adicionais associados ao buffer
     */
    int dequeue(queue_consumer_t *consumer, uint8_t ** oBuffer, uint32_t * oBuffersize,
                uint32_t *oTimestamp, QueueExtraData ** oExtraData);
};


#endif //_QUEUE_BUFFER_H_

