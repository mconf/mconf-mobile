#ifndef _THREAD_H_
#define _THREAD_H_

#include <stdint.h>
#include <pthread.h>
#ifdef _MSC_VER
#include "Thread_windows.h"
#else
#include "Thread_Linux.h"
#endif
#include "Mutex.h"
#include <errorDefs.h>

class Runnable
{

private:

    pthread_t thread_;                      ///< pthread_t
    bool running_;                          ///< Thread rodando?
    uint32_t threadId_;                     ///< Id da thread (id retornado por GetCurrentThreadId())
    Mutex mutex_;

    static void * startThread(void * param);
    void * callFunc();

    void threadFinished();

protected:
    virtual void threadFunction() = 0;

public:

    int getId();

    Runnable();

    bool run(bool joinable);

    int join();

    int kill(int sig);

    bool isRunning() const;

};





template <class TClass>
class Thread
{
private:
    void *(TClass::*function_)(void *);     ///< Ponteiro para a função a ser chamada
    TClass * obj_;                          ///< Objeto que contém a thread da função
    pthread_t thread_;                      ///< pthread_t
    void * param_;                          ///< Parâmetro passado na chamada da function_
    bool running_;                          ///< Thread rodando?
    uint32_t threadId_;                     ///< Id da thread (id retornado por GetCurrentThreadId())

    static void * startThread(void * param)
    {
        Thread *thread = reinterpret_cast<Thread *>(param);
        return thread->callFunc();
    };

    void * callFunc()
    {
        threadId_ = (uint32_t)GetCurrentThreadId();
        void * ret = (*obj_.*function_)(param_);
        threadFinished(); // acabou a função chamda, thread está finalizada
        return ret;
    };

    void threadFinished()
    {
        running_ = false; 
        threadId_ = 0;
    };

public:

    Thread(TClass* obj, void *(TClass::*function)(void *))
    {
        running_ = false;
        obj_ = obj;
        function_ = function;
        param_ = NULL;
        threadId_ = 0; /// \todo verificar se esse valor realmente indica "id inválido"
    };

    int run(void * param, bool joinable)
    {
        int ret = 0;

        if (!running_) {
            param_ = param;

            pthread_attr_t attr;
            pthread_attr_init(&attr);
            if (joinable) {
                pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
            }
            running_ = true;
            if (pthread_create(&thread_, &attr, &Thread::startThread, this) != 0)
                ret = 1;
            pthread_attr_destroy(&attr);
        }

        return ret;
    };

    int join(void ** value_ptr)
    {
        int ret = 0;
        if (running_) {
            ret = pthread_join(thread_, value_ptr);
            threadFinished();
        }
        return ret;
    };

    int kill(int sig)
    {
        int ret = 0;
        if (running_) {

#ifdef __linux__
			// http://gcc.gnu.org/onlinedocs/gcc/Name-lookup.html
            ret = pthread_cancel(this->thread_);
#else // _ANDROID e _MSC_VER
            ret = pthread_kill(thread_, sig);
#endif

            threadFinished();
        }
        return ret;
    };

    bool isRunning()
    {
        return running_;
    };
};


#endif // _THREAD_H_
