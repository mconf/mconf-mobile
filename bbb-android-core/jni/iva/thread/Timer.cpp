#include <CommonLeaks.h>
#include <iostream>
using namespace std;
#include <stdio.h>
#include "Timer.h"
#include "Milliseconds.h"
#include <CommonLeaksCpp.h>

TimerBase::TimerBase(bool singleShot) :
    _run(false), _active(false),
    _interval(0), _singleShot(singleShot)
{
}

TimerBase::~TimerBase()
{
    stop();
}

void TimerBase::setInterval(int msec)
{
    _interval = msec;
}

void TimerBase::setInterval(Milliseconds &msec)
{
    _interval = msec;
}

Milliseconds& TimerBase::getInterval()
{
    return _interval;
}

int TimerBase::start(int msec)
{
    if (_run) {
        return E_ERROR;
    }
    setInterval(msec);
    return start();
}

int TimerBase::start()
{
    if (_run) {
        return E_ERROR;
    }
    /// \todo Validar se _interval não é zero
    _run = true;
    run(true);
    return E_OK;
}

int TimerBase::stop()
{
    if (!_run) {
        return E_ERROR;
    }
    _run = false;
    join();
    return E_OK;
}

int TimerBase::wait()
{
    if (!_run) {
        return E_ERROR;
    }
    join();
    return E_OK;
}

bool TimerBase::isActive()
{
    return _active;
}

void TimerBase::threadFunction()
{
    Milliseconds begin;
    Milliseconds end(0), diff(0);

    _active = true;
    Interval aux;

    while (_run) {

        aux = _interval - (end - begin);
        end.setTimestamp();
        if (!_run) break;
        Milliseconds(aux).sleep(); // dorme de 5 em 5ms

        begin.setTimestamp();

        if (!_run) break;
        timeout();
        if (_singleShot) break;
    }
    _active = false;
    _run = false;
}

