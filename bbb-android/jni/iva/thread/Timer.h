#ifndef _TIMER_H_
#define _TIMER_H_

#include "Thread.h"
#include "Milliseconds.h"

/** \brief Classe base para o timer. Para ser usado deve ser herdado.
 *
 * Implementa a funcionalidade geral do timer. A cada ciclo, chama a função interna
 * \a timeout(), que pode ser herdada e implementada como se fosse um callback.
 */
class TimerBase : public Runnable
{
public:
    /** \brief Construtor padrão
     *  \param singleShot Se true, o timeout só é disparado uma vez
     */
    TimerBase(bool singleShot = false);

    virtual ~TimerBase();

    /** \brief Inicia a execução do timer com o intervalo estipulado
     *  \param msec Intervalo entre timeouts (ou o intervalo inicial se singleShot)
     *  \return E_OK em caso de sucesso ou E_ERROR em caso de erro
     */
    virtual int start(int msec);

    /** \brief Inicia a execução do timer com o intervalo setado atualmente
     *  \return E_OK em caso de sucesso ou E_ERROR em caso de erro
     */
    virtual int start();

    /** \brief Para a execução do timer
     *  \return E_OK em caso de sucesso ou E_ERROR em caso de erro
     */
    virtual int stop();

    /** \brief Espera o timer finalizar sua execução
     *  \return E_OK em caso de sucesso ou E_ERROR em caso de erro
     *
     * Bloqueia a execução até o timer acabar sua execução. Isso só acontecerá se o
     * timer for singleShot ou se outra thread fizer o timer parar.
     */
    virtual int wait();

    /** \brief Retorna se o timer está ativo ou não
     *
     * O timer se torna ativo assim que sua thread inicia e se torna inativo assim
     * que ela vai finalizar.
     */
    bool isActive();

    /** \brief Seta o intervalo entre chamadas do timer
     *  \param msec Novo valor do intervalo
     *
     * Pode ser modificado mesmo durante a execução do timer (após chamar \a start())
     */
    void setInterval(int msec);

    void setInterval(Milliseconds &msec);

    /** \brief Retorna o intervalo atual
     */
    Milliseconds& getInterval();

protected:
    void threadFunction();

    /** \brief Função chamada a cada "ciclo" do timer
     *
     * Deve ser implementada pelas classes que herdam este timer.
     */
    virtual void timeout() = 0;

private:
    static const int SLEEP_INTERVAL = 10;       ///< Tamanho dos sleeps em ms

    bool _run;                                  ///< Flag para controlar execução da thread
    bool _active;                               ///< Timer ativo?
    bool _singleShot;                           ///< Timer de execução única?
    Milliseconds _interval;                     ///< Intervalo entre timeouts

    /** \brief Função auxiliar para dormir aos poucos até atingir o tempo total \a time
     */
    void sleep(Interval &time);
};

/** \brief Timer que recebe um callback
 *
 * Herda TimerBase para possibilitar a passagem de uma função de callback para o timer,
 * para o usuário não precisar herdar TimerBase e implementar seu timer
 */
template <class TClass>
class Timer : public TimerBase
{
public:
    /** \brief Construtor padrão
     *  \param singleShot Se true, o timeout só é disparado uma vez
     */
    Timer(TClass* obj, void (TClass::*callback)(void), bool singleShot = false)
      : TimerBase(singleShot)
    {
        _callback = callback;
        _obj = obj;
    };

    /*static void singleShot(int msec, TClass* obj, void (TClass::*callback)(void))
    {
        Timer * timer = new Timer(obj, callback, true);
        timer->setInterval(msec);

        Thread<TimerBase> t(this, TimerBase::singleShotThread);
        t.run((void *)timer, true);
    };

    void * singleShotThread(void * param)
    {
        TimerBase * timer = (TimerBase *)param;
        timer->start();
        timer->wait();
        delete timer;
    };*/

protected:
    /** \brief Função chamada a cada "ciclo" do timer
     *
     * Apenas chama o callback passado pelo usuário.
     */
    virtual void timeout()
    {
        (*_obj.*_callback)();
    };

private:
    void (TClass::*_callback)(void);   ///< Ponteiro para a função de callback
    TClass * _obj;                     ///< Objeto que contém o callback
};



#endif // _TIMER_H_
