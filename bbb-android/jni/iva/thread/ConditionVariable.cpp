/*
 * ConditionVariable.cpp
 *
 *  Created on: 13/05/2010
 *      Author: lmborba
 */

#include <CommonLeaks.h>
#include "ConditionVariable.h"
#include <errorDefs.h>
#include <CommonLeaksCpp.h>

ConditionVariable::ConditionVariable(Mutex & associated)
{

	associatedMutex_ = &associated;
	if (pthread_cond_init(&pthreadStructure_,NULL)) {
		throw E_THREAD_INIT;
	}

}

bool ConditionVariable::notifyAll()
{

	if (pthread_cond_broadcast(&pthreadStructure_)) {
		return false;
	}

	return true;

}

ConditionVariable::~ConditionVariable() {

	pthread_cond_destroy(&pthreadStructure_);

};

bool ConditionVariable::wait()
{

	if (pthread_cond_wait(&pthreadStructure_,associatedMutex_->getPthreadPointer())) {
		return false;
	}
	return true;

}



bool ConditionVariable::notify()
{

	if (pthread_cond_signal(&pthreadStructure_)) {
		return false;
	}

	return true;

}



bool ConditionVariable::wait(const Interval & timeToWait)
{

	struct timespec interval;

	interval.tv_nsec = timeToWait.getMicroseconds()*1000;
	interval.tv_sec = timeToWait.getSeconds();

	if (pthread_cond_timedwait(&pthreadStructure_, associatedMutex_->getPthreadPointer(),&interval)) {
		return false;
	}

	return true;

}


