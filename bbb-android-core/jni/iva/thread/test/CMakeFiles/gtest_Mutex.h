#include <Mutex.h>
#include <Thread.h>
#include <Milliseconds.h>
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
using namespace std;

namespace {

  class MutexTest :
    public ::testing::Test
  {

  protected:
    
    MutexTest();

    virtual ~MutexTest();

    virtual void SetUp();

    virtual void TearDown();

  protected:
    
    class t1 : 
        public Runnable
    {
      
    public:
      
    t1(int * a, Mutex & arraa)
        : Runnable()
	{
	
	  //	arra_ = arra;
	  a_ = a;
	  arra = &arraa;
	  
	};
      
      void threadFunction()
      {
	
	arra->lock();
	if (*a_ == 0) {
	  Milliseconds(10).sleep();
	  (*a_)++;
	};
	arra->unlock();
	
      };
      
      int getX() {
	return *a_;
      };
      
    private:
      int * a_;
      Mutex * arra;
      
    };
    
    int x;
    
    t1 * teste1;
    t1 * teste2;
    
    
  };
    
};
