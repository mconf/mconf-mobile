/*
 * ConditionVariable.h
 *
 *  Created on: 13/05/2010
 *      Author: lmborba
 */

#ifndef CONDITIONVARIABLE_H_
#define CONDITIONVARIABLE_H_

#include "Interval.h"
#include <pthread.h>

class ConditionVariable {

	pthread_cond_t pthreadStructure_;
	Mutex * associatedMutex_;

public:

	ConditionVariable(Mutex & associated);

	virtual ~ConditionVariable();

	bool wait(const Interval & timeToWait);

	bool wait();

	bool notify();

	bool notifyAll();

};

#endif /* CONDITIONVARIABLE_H_ */
