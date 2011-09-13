#include "gtest_ConditionVariable.h"
#include <Thread.h>
#include <Seconds.h>

QueueTest::QueueTest(int size) {

	size_ = size;

	a = new Mutex;

	if (a == NULL) {
		throw -1;
	}

	b = new ConditionVariable(*a);

	if (b == NULL) {
		delete a;
		throw -2;
	}

};

void QueueTest::producer(int data) {

	a->lock();

	while (arr.size() == size_) {
		b->wait();
	}

	int x = arr.size();

	arr.push_back(data);

	if (x == 0) {
		b->notify();
	}

	a->unlock();

}

int QueueTest::consumer() {

	a->lock();

	while (arr.size() == 0) {
		b->wait();
	}

	unsigned int x = arr.size();

	int ret = arr.front();
	arr.pop_front();

	if (x == size_) {
		b->notify();
	}

	a->unlock();

	return ret;

}

ConditionVariableTest::ConditionVariableTest()
{   
};

ConditionVariableTest::~ConditionVariableTest()
{
};

void ConditionVariableTest::SetUp()
{
};

void ConditionVariableTest::TearDown()
{
};

TEST_F(ConditionVariableTest, CreateConditionVariableAndSleep) {

	QueueTest a(100);

	Teste1 x(a);

	x.run(true);

	Seconds tempo(1);
	tempo.sleep();

	Teste2 y(a);

	y.run(true);

	x.join();
	y.join();

}

TEST_F(ConditionVariableTest, CreateConditionVariable) {

	QueueTest a(100);

	Teste2 x(a);

	x.run(true);

	Seconds tempo(1);
	tempo.sleep();

	Teste1 y(a);

	y.run(true);

	x.join();
	y.join();

}

