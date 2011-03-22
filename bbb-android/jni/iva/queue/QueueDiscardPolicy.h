#ifndef _QUEUE_DISCARD_POLICY_H
#define _QUEUE_DISCARD_POLICY_H

#include <common.h>
#include <map>
using namespace std;

/** \brief Política de descarte para o QueueDiscard
 *
 * Contém as informações da política de descarte para ser usado com a
 * classe QueueDiscard.
 * O descarte é baseado no tamanho da queue. São criados pares do tipo
 * (tamanho, framesToUse) que indicam que queues com tamanho >= 'tamanho'
 * devem utilizar 'framesToUse' e só então descartar um. Diversos destes pares
 * definem os níveis de descarte existentes. Por exemplo:
 * 
 * 3 pares cadastrados:
 * \li (5, 4)
 * \li (10, 2)
 * \li (15, FLAG_CLEAR)
 *
 * Três pares indicam que existem 4 níveis: o primeiro de 0 à 4, o segundo de 5 à 9, o terceiro
 * de 10 à 14 e o quarto de 15 em diante. Quando o tamanho da queue esta entre 0 e 4, ela está no
 * nível 0, onde utiliza todos os frames (QueueDiscardPolicy::FLAG_ALL). Entre 5 e 9 está no nível 1,
 * onde usa 4 frames e só então descarta um. Entre 10 e 14 frames, está no nível 2, onde usa 2
 * frames e só então descarta um. Acima de 15 está no nível 3, onde a flag QueueDiscardPolicy::FLAG_CLEAR
 * indica que a fila deve ser zerada (remover todos os frames).
 */
class QueueDiscardPolicy
{
public:
    static const int FLAG_CLEAR     = -1;   ///< Indica que deve limpar a queue
    static const int FLAG_ALL       = -2;   ///< Indica que sempre deve usar os frames, nunca descarta

    QueueDiscardPolicy(void);
    virtual ~QueueDiscardPolicy(void);

    /** \brief Adiciona um item à lista de políticas
     *  \param[in] queueSize Tamanho da queue para ativar este item (deve ser único!)
     *  \param[in] framesToUse Quantidade de frames a serem usados antes de descartar um
     *  \return E_OK em caso de sucesso ou E_ERROR em caso de erro
     */
    int add(int queueSize, int framesToUse);

    /** \brief Remove um item da lista de políticas
     *  \param[in] queueSize Índice do item, que é o tamanho da queue (\a queueSize da função \a add())
     */
    void remove(int queueSize);

    /** \brief Retorna a quantidade de políticas já inseridas
     */
    int size();

    /** \brief Retorna quantos frames devem ser usados para o tamanho de fila \a queueSize
     *
     * O retorno da função é feito com base nos items cadastrados que definem os níveis de descarte
     * existentes. Ver exemplo na descrição desta classe.
     * Obs: O tamanho \a queueSize não precisa estar cadastrado na lista, pode ser um valor qualquer
     * (e.g. um valor intermediário entre 2 cadastrados).
     */
    int getFramesToUse(int queueSize);

    /** \brief Retorna o nível correspondente ao tamanho de fila \a queueSize
     *  \param[in] queueSize Tamanho da queue para se buscar o nível
     */
    int getLevel(int queueSize);

    /** \brief Verifica se o nível queueSize está cadastrado
     *  \param[in] queueSize Tamanho da queue que deve ser encontrado
     *  \return Retorna se há um item cadastrado com tamanho de queue \a queueSize
     */
    bool exists(int queueSize);

    /** \brief Retorna o par (queueSize, framesToUse) para o nível escolhido \a level
     *  \param[in] level Nível sendo buscado
     */
    pair<int, int> getItemByLevel(int level);

    bool operator==(const QueueDiscardPolicy &operand);
    bool operator!=(const QueueDiscardPolicy &operand);

private:
    map<int, int> _items;       ///< Contém os pares cadastrados

};

#endif
