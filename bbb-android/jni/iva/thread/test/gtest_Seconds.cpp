#include "gtest_Seconds.h"
#include <Microseconds.h>

SecondsTest::SecondsTest()
{
}

SecondsTest::~SecondsTest() 
{
}

void SecondsTest::SetUp() 
{
}

void SecondsTest::TearDown() 
{
}

void SecondsTest::_CreateSecondsAndSleep()
{
    Seconds now;
    Seconds(1).sleep();
    Seconds now2;

    Microseconds diffti = now2 - now;

    EXPECT_GE(diffti.getTime(), 1000000 * (1-SLEEP_ERROR));
    EXPECT_LE(diffti.getTime(), 1000000 * (1+SLEEP_ERROR));
    cout << diffti.getTime() << endl;
}

TEST_F(SecondsTest, CreateSecondsAndSleep)
{
    _CreateSecondsAndSleep();
    _CreateSecondsAndSleep();
    _CreateSecondsAndSleep();
    _CreateSecondsAndSleep();
    _CreateSecondsAndSleep();
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
