#include <Timer.h>
#include "gtest_main.h"
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
using namespace std;

namespace {

    class TimerTest : public ::testing::Test
    {
    protected:
        int _timeouts, _interval;
        Milliseconds _lastTimeout;

        TimerTest() {};

    public:
        void timerCallback();

    };

    class TimerBaseTest : public ::testing::Test
    {
    protected:
        TimerBaseTest() {};

    public:
        void * timerCallback(void * param);

    };

    class MyTimerBase : public TimerBase
    {
    private:
        int _timeouts;
        Milliseconds _lastTimeout;

    public:
        MyTimerBase(bool singleShot = false) :
            TimerBase(singleShot),
            _timeouts(0), _lastTimeout(0)
        {
        };

        int getTimeouts()
        {
            return _timeouts;
        };

        virtual int start(int msec)
        {
            _timeouts = 0;
            _lastTimeout.setTimestamp();
            return TimerBase::start(msec);
        }

    protected:
        virtual void timeout()
        {
            // calcula tempo que passou desde a última chamada ao timeout
            Milliseconds diff;
            diff -= _lastTimeout;
            // guarda o tempo aqui no início mesmo pois o timer já desconta o
            // tempo de execução desta função!
            _lastTimeout.setTimestamp();

            // dá um limite máximo de 2ms de erro para cima ou para baixo
            cout << "Iteracao ";
            cout << setw(3) << setiosflags(ios::right) << _timeouts << ":";

            cout << " diferenca = ";
            cout << setw(4) << setiosflags(ios::right) << diff.getTime() << " |";
            cout << " esperado = ";
            cout << setw(4) << setiosflags(ios::right) << getInterval().getTime() << " |";
            cout << " erro = ";
            cout << setw(4) << setiosflags(ios::right) << (diff.getTime() - getInterval().getTime());
            cout << endl;

            EXPECT_TRUE((diff.getTime() <= getInterval().getTime() + 2) &&
                        (diff.getTime() >= getInterval().getTime() - 2))
                << "Diferença: " << diff.getTime()
                << ", Esperado " << getInterval().getTime();
            _timeouts++;

        };

    };

};
