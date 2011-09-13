#ifndef _GTEST_CONDITIONVARIABLE_H_
#define _GTEST_CONDITIONVARIABLE_H_

#include <ConditionVariable.h>
#include <Mutex.h>
#include <Thread.h>
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
#include <list>
using namespace std;

namespace {

  class QueueTest
  {

  private:
	  list<int> arr;
	  unsigned int size_;
	  Mutex * a;
	  ConditionVariable * b;

  public:

	 QueueTest(int size);
	 void producer(int data);
	 int consumer();

  };

  class Teste1
  : public Runnable
    {
	private:

	  QueueTest * a;

    public:

	  Teste1(QueueTest & x) {
		  a = &x;
	  }

	  void threadFunction() {
		  for (int i=0;i<2048;i++) {
			  a->producer(i);
		  }
	  }

    };

  class Teste2
    : public Runnable
      {
  	private:

  	  QueueTest * a;

      public:

  	  Teste2(QueueTest & x) {
  		  a = &x;
  	  }

  	  void threadFunction() {
  		  for (int i=0;i<2048;i++) {
  			  //cout << a->consumer() << endl;
  			  int tt = a->consumer();
  			  EXPECT_EQ(tt,i);
  		  }
  	  }

      };

  class ConditionVariableTest :
    public ::testing::Test
  {

  protected:
    
    ConditionVariableTest();

    virtual ~ConditionVariableTest();
    
    virtual void SetUp();

    virtual void TearDown();

  };

};

#endif

