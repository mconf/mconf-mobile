#include <CommonLeaks.h>
#include "Thread.h"
#include <CommonLeaksCpp.h>

#include <signal.h>

void * Runnable::startThread(void * param)
{
    Runnable *thread = reinterpret_cast<Runnable *>(param);
    thread->callFunc();
    return NULL;
};


void * Runnable::callFunc()
{
    threadId_ = GetCurrentThreadId();
    threadFunction();
    threadFinished(); // acabou a função chamda, thread está finalizada
    return NULL;
};

void Runnable::threadFunction()
{
};

void Runnable::threadFinished()
{
    running_ = false; 
    threadId_ = 0;
};

Runnable::Runnable()
{
    running_ = false;
    threadId_ = 0; /// \todo verificar se esse valor realmente indica "id inválido"
};

int Runnable::getId() {

    if (running_) {
        return threadId_;
    }

    return 0;
};

bool Runnable::run(bool joinable)
{
    bool ret = true;

    if (!running_) {

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        if (joinable) {
            pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
        }
        running_ = true;
        if (pthread_create(&thread_, &attr, &Runnable::startThread, this) != 0)
            ret = false;
        pthread_attr_destroy(&attr);
    }

    return ret;
};

int Runnable::join()
{
    int ret = E_THREAD_JOIN;
    mutex_.lock();
    if (running_) {
        ret = pthread_join(thread_, NULL);
        threadFinished();
    }
    mutex_.unlock();
    return ret;
};

int Runnable::kill(int sig)
{
    int ret = E_THREAD_KILL;
    mutex_.lock();
    if (running_) {
        ret = pthread_kill(thread_, sig);
        threadFinished();
    }
    mutex_.unlock();
    return ret;
};

bool Runnable::isRunning() const
{
    return running_;
};

