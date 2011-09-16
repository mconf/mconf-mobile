#include "gtest_Mutex.h"

MutexTest::MutexTest()
{
};

MutexTest::~MutexTest() 
{
};

void MutexTest::SetUp() 
{
};

void MutexTest::TearDown() 
{
};

TEST_F(MutexTest, CreateMutex) {

  x = 0;

  Mutex arra;

  teste1 = new t1(&x,arra);
  teste2 = new t1(&x,arra);

  teste1->run(true);
  teste2->run(true);

  teste1->join();
  teste2->join();

  cout << x << endl;

  EXPECT_TRUE(x == 1);

}

TEST_F(MutexTest, LockTryLock) {

  x = 0;

  Mutex arra;

  arra.lock();
  int res = arra.tryLock();
  arra.unlock();

  cout << res << endl;
  
  EXPECT_TRUE(res == EBUSY);

}

