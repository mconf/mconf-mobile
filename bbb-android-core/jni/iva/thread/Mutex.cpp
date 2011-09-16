#include <CommonLeaks.h>
#include "Mutex.h"
//#include <errno.h>
#include <CommonLeaksCpp.h>


Mutex::Mutex()
: _mutex(PTHREAD_MUTEX_INITIALIZER)
{
}

Mutex::Mutex(const Mutex& m)
: _mutex(PTHREAD_MUTEX_INITIALIZER)
{
}

Mutex& Mutex::operator=(const Mutex& m)
{
  _mutex = PTHREAD_MUTEX_INITIALIZER;
  return *this;
}

Mutex::~Mutex()
{
  pthread_mutex_destroy(&this->_mutex);
}

int Mutex::lock()
{
  return pthread_mutex_lock(&this->_mutex);
}

int Mutex::unlock()
{
  return pthread_mutex_unlock(&this->_mutex);
}

int Mutex::tryLock()
{
  return pthread_mutex_trylock(&this->_mutex);
}

pthread_mutex_t *Mutex::getPthreadPointer()
{
	return &_mutex;
}



