#ifndef _STATISTICS_H_
#define _STATISTICS_H_

#include "common.h"
#include "Thread.h"

#ifdef _MSC_VER
#include <objbase.h>
#include <psapi.h>
#endif

typedef BOOL (__stdcall *StaticsGetTimes) (HANDLE, LPFILETIME, LPFILETIME, LPFILETIME, LPFILETIME);

/** \brief Classe para obter informações de memória e CPU de processos e threads
 *
 * A classe funciona de dois modos:
 * * Modo automático: uma thread é executada e chama uma função de callback a cada
 *   intervalo de tempo informando CPU e memória. Esta função de callback deve ser
 *   reimplementada pela classe que deseja obter as informações. O modo automático é
 *   ligado com \a start() e desligado com \a stop().
 * * Manual: chamadas às funções \a getMemoryUsage e \a getCpuUsage podem ser feitas a
 *   qualquer momento sobre o objeto, independente da thread estar rodando ou não.
 */
class Statistics : public Runnable
{
public:
    enum StatisticsType {
        STATISTICS_OVER_PROCESS,   ///< Realiza estatisticas sobre o processo que cria a classe
        STATISTICS_OVER_THREAD     ///< Realiza estatisticas sobre a thread que cria a classe
    };

private:
    StatisticsType _type;                   ///< Tipo das informações: sobre processo ou thread
    uint32_t _interval;                     ///< Intervalo de amostragem da cpu
    uint32_t _numberOfProcessors;           ///< Numero de processadores do computador
    HANDLE _handle;                         ///< Ponteiro sobre a estrutura de processo (ou thread) aberto
    StaticsGetTimes _getTimes;              ///< Ponteiro para a funcao que pega estatisticas do processador (diferente se processo ou thread)
    bool _runThread;                        ///< Enquanto \a true, a thread será executada

    /** \brief Inicializador generico da classe
     *  \param type Vindo do construtor
     *  \param id Vindo do construtor
     *  \return E_OK em caso de sucesso ou E_ERROR em caso de erro
     */
    int init(StatisticsType type, uint32_t id);

    /** \brief Conversor FILETIME para uint32_t (em ms)
     *  \param ftTime Variavel de entrada
     *  \param *iTime Variavel de saida
     *  \return E_OK se concluido com sucesso
     *  \return E_ERROR se concluido com erro, gravado no log
     */
    int filetimeToInt(FILETIME ftTime,
                      uint32_t * iTime);

    /** \brief Funcao generica para a funcao de windows GetProcessTimes ou GetThreadTimes
     *  \param processHandle Handle sobre o processo/thread
     *  \return E_OK se concluido com sucesso
     *  \return E_ERROR se concluido com erro, gravado no log
     */
    int getCpuTimes(HANDLE processHandle,
                    uint32_t * userTime,
                    uint32_t * kernelTime,
                    uint32_t * timestamp);

protected:
    /** \brief Implementação da função threadFunction em Runnable
     */
    void threadFunction();

    /** \brief Função chamada quando a classe está em modo automático
     *
     * Deve ser reimplementada para tratar os dados recebidos
     */
    virtual void callback(uint32_t cpu_usage, uint32_t mem_usage) {};

public:

    /** \brief Construtor da classe
     *  \param type Diz se a analise e sobre thread ou processo
     */
    Statistics(StatisticsType type);

    /** \brief Construtor da classe
     *  \param type Diz se a analise e sobre thread ou processo
     *  \param id ID do processo ou thread que sera analizado
     */
    Statistics(StatisticsType type, uint32_t id);

    virtual ~Statistics();


    /** \brief Inicia o modo automático, onde a função \a callback é chamada a cada msec milissegundos
     *  \return E_OK se concluido com sucesso
     *  \return E_ERROR se a thread já estava iniciada
     */
    int start(uint32_t msec);

    /** \brief Interrompe o modo automático
     *  \return E_OK se concluido com sucesso
     *  \return E_ERROR se concluido com erro, gravado no log
     */
    int stop();

    /** \brief Especifica o intervalo usado no modo automático
     *  \param value Novo valor do intervalo em ms
     */
    void setInterval(uint32_t value);

    /** \brief Retorna a memoria utilizada pelo processo/thread, em KB
     */
    uint32_t getMemoryUsage();

    /** \brief Retorna a utilizacao de cpu do processo/thread durante o interval, em %
     */
    uint32_t getCpuUsage(uint32_t interval);
};

#endif // _STATISTICS_H_

