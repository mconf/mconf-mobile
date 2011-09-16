#include "gtest_Thread.h"

ThreadTest::ThreadTest()
{
};

ThreadTest::~ThreadTest() 
{
}

void ThreadTest::SetUp() 
{
}

void ThreadTest::TearDown() 
{
}
  
TEST_F(ThreadTest, CreateThreadAndJoin) {

  EXPECT_EQ(1, teste1.getX());
  teste1.run(true);
  int x;
  x = teste1.getX();
  EXPECT_TRUE((x == 1) || (x == 2));
  teste1.join();
  EXPECT_EQ(2, teste1.getX());

}
