#ifndef _MUTEX_H_
#define _MUTEX_H_

#include <pthread.h>

class Mutex
{

private:
    pthread_mutex_t _mutex;

public:
    Mutex();
    Mutex(const Mutex& m);
    ~Mutex();
    int lock();
    int unlock();
    int tryLock();

    pthread_mutex_t * getPthreadPointer();

    Mutex & operator=(const Mutex& m);
};

#endif // _MUTEX_H_
