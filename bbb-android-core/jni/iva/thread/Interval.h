#ifndef _INTERVAL_H_
#define _INTERVAL_H_

#include "Mutex.h"

class Interval 
{

public:
    Interval(int sec, int usec);
    Interval();
    Interval(const Interval & val);
    virtual ~Interval();
    int getSeconds() const;
    int getMicroseconds() const;
    void setInterval(int sec, int usec);
    Interval operator-(const Interval& operand) const;
    Interval operator+(const Interval& operand) const;
    void operator-=(const Interval & operand);
    void operator+=(const Interval & operand);

    bool operator>=(const Interval& operand) const;
    bool operator>(const Interval& operand) const;
    bool operator==(const Interval& operand) const;
    bool operator<(const Interval& operand) const;
    bool operator<=(const Interval& operand) const;

    /** \brief Atribui o tempo atual do sistema para este objeto
     */
    void setTimestamp();

    /** \brief Função que realmente implementa o sleep() e dorme
     *         durante todo o tempo setado no objeto
     */
    void fullSleep();

    /** \brief Dorme o tempo atualmente setado no objeto. Dorme aos poucos,
     *         utilizando internamente a função sleep(Interval, bool)
     */
    void sleep();

    /** \brief Dorme, aos poucos, o tempo atualmente setado no objeto. Utilizado
     *         para que o sleep possa ser cancelado antes de dormir o tempo total.
     *  \param[in] step Intervalo entre cada dormida. Se for igual ou maior que o tempo
     *             atual do objeto, a função funcionará exatamente como a sleep().
     *  \param[in] run Ponteiro para uma flag que pode ser utilizada para parar
     *             a execução da função. Quando \a run for false, a função para. O tempo
     *             máximo que a função pode demorar para parar é definido por \a step.
     *  \return Objeto indicando quanto tempo realmente dormiu
     */
    Interval sleep(Interval &step, bool &run);

    Interval sleep(Interval &step);

private:
    int sec_;
    int usec_;
    Mutex running_mutex_;
    bool blocked_;

    

};

#endif
