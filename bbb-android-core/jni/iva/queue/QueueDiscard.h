#ifndef _QUEUE_DISCARD_H
#define _QUEUE_DISCARD_H

#include "QueueDiscardPolicy.h"
#include "queue.h"

/** \brief Classe para verificação da necessidade de descartes na queue
 *
 * Esta classe auxilia a verificar se há necessidade de descartes em queues.
 * Utiliza um QueueDiscardPolicy, que contém a política de descarte.
 *
 * Esta classe não executa nenhuma ação na queue, apenas consulta seu tamanho
 * e retorna qual ação deve ser tomada pelo usuário da classe.
 */
class QueueDiscard
{
public:
    QueueDiscard(void);
    QueueDiscard(QueueDiscardPolicy &policy);
    virtual ~QueueDiscard(void);

    QueueDiscardPolicy &getPolicy();
    void setPolicy(const QueueDiscardPolicy &value);

    /** \brief Verifica a queue para ver se o frame atual deve ser descartado ou não
     *  \param[in] consumer Consumidor da queue que será verificada
     *  \return \p true caso o frame deva ser descartado ou \p false caso contrário
     */
    bool discard(queue_consumer_t *consumer);

    /** \brief Retorna uma string descrevendo o nível atual
     *
     * String no formato "nível (queueSize, framesToUse)", ex: "1 (6, 4)"
     * Usa o nível obtido na última chamada da função discard()
     */
    string getLevelAsStr();

    /** \brief Indica se a queue deve ser zerada
     *  \return \p true se a queue deve ser zerada e \p false caso contrário
     * 
     * Usa o último nível consultado na política de descarte (último nível é setado
     * nas chamadas discar()) para ver se a queue deve ser zerada. Deve ser zerada se o
     * último nível contiver a flag QueueDiscardPolicy::FLAG_CLEAR.
     */
    bool clear();

private:
    QueueDiscardPolicy _policy;         ///< Política de descarte
    int _framesUsed;                    ///< Frames usados até o momento
    int _lastLevel;                     ///< Nível na última verificação \a check()
    bool _clearing;                     ///< Controla se está em fase de limpar a queue ou não

};

#endif
