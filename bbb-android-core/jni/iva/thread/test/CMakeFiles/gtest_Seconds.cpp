#include "gtest_Seconds.h"
#include <Microseconds.h>

SecondsTest::SecondsTest()
{   
};

SecondsTest::~SecondsTest() 
{
};

void SecondsTest::SetUp() 
{
};

void SecondsTest::TearDown() 
{
};

TEST_F(SecondsTest, CreateSecondsAndSleep) {

  Seconds now;
  Seconds(1).sleep();
  Seconds now2;

  Microseconds diffti = now2 - now;
  
  EXPECT_TRUE(diffti.getTime() >= 999000);
  cout << diffti.getTime() << endl;

}

TEST_F(SecondsTest, CreateSecondsAndSleep2) {

  Seconds now;
  Seconds(1).sleep();
  Seconds now2;

  Microseconds diffti = now2 - now;
  
  EXPECT_TRUE(diffti.getTime() >= 999000);
  cout << diffti.getTime() << endl;

}

TEST_F(SecondsTest, CreateSecondsAndSleep3) {

  Seconds now;
  Seconds(1).sleep();
  Seconds now2;

  Microseconds diffti = now2 - now;
  
  EXPECT_TRUE(diffti.getTime() >= 999000);
  cout << diffti.getTime() << endl;

}

TEST_F(SecondsTest, CreateSecondsAndSleep4) {

  Seconds now;
  Seconds(1).sleep();
  Seconds now2;

  Microseconds diffti = now2 - now;
  
  EXPECT_TRUE(diffti.getTime() >= 999000);
  cout << diffti.getTime() << endl;

}

TEST_F(SecondsTest, CreateSecondsAndSleep5) {

  Seconds now;
  Seconds(1).sleep();
  Seconds now2;

  Microseconds diffti = now2 - now;
  
  EXPECT_TRUE(diffti.getTime() >= 999000);
  cout << diffti.getTime() << endl;

}

TEST_F(SecondsTest, Comparison)
{
    ASSERT_TRUE(Seconds(5) == 5);
    ASSERT_TRUE(Seconds(5) >= 5);
    ASSERT_TRUE(Seconds(5) >= 4);
    ASSERT_TRUE(Seconds(5) <= 5);
    ASSERT_TRUE(Seconds(5) <= 6);
    ASSERT_TRUE(Seconds(5) > 4);
    ASSERT_TRUE(Seconds(5) < 6);

    ASSERT_TRUE(Seconds(5) == Seconds(5));
    ASSERT_TRUE(Seconds(5) >= Seconds(5));
    ASSERT_TRUE(Seconds(5) >= Seconds(4));
    ASSERT_TRUE(Seconds(5) <= Seconds(5));
    ASSERT_TRUE(Seconds(5) <= Seconds(6));
    ASSERT_TRUE(Seconds(5) > Seconds(4));
    ASSERT_TRUE(Seconds(5) < Seconds(6));
}