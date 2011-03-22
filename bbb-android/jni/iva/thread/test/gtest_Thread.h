#include <Thread.h>
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
using namespace std;

namespace {

  class ThreadTest :
  public ::testing::Test
    {

      protected:
	
	ThreadTest();
	
	virtual ~ThreadTest();

	virtual void SetUp();
	
	virtual void TearDown();
    
      protected:
    
	class t1 : 
        public Runnable
	{
	  
	public:
	t1()
	  : Runnable()
	    {
	      
	      x = 1;
	
	    };
	  
	  void threadFunction()
	  {
	    
	    x = 2;

	  };
	  
	  int getX() {
	    return x;
	  };
	  
	private:
	  int x;
	  
	};
	
	t1 teste1;
	
      };
    
};
