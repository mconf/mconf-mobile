#include "gtest_Milliseconds.h"
#include <Microseconds.h>

MillisecondsTest::MillisecondsTest()
{   
};

MillisecondsTest::~MillisecondsTest() 
{
};

void MillisecondsTest::SetUp() 
{
};

void MillisecondsTest::TearDown() 
{
};

TEST_F(MillisecondsTest, CreateMillisecondsAndSleep) {

  Milliseconds now;
  Milliseconds(20).sleep();
  Milliseconds now2;

  //cout << now2.getTime() << "," << now.getTime() << endl;
  Microseconds diffti = now2 - now;
  
  EXPECT_GE(diffti.getTime(), 20000 * (1-SLEEP_ERROR));
  EXPECT_LE(diffti.getTime(), 20000 * (1+SLEEP_ERROR));
  cout << diffti.getTime() << endl;

}

TEST_F(MillisecondsTest, AssigningAndSleep) {
    Milliseconds m(20);
    m = 30;
    Milliseconds now1;
    m.sleep();
    Milliseconds now2;

    Microseconds diffti = now2 - now1;

    EXPECT_GE(diffti.getTime(), 30000 * (1-SLEEP_ERROR));
    EXPECT_LE(diffti.getTime(), 30000 * (1+SLEEP_ERROR));

    cout << diffti.getTime() << endl;
}

TEST_F(MillisecondsTest, Comparing) {
    Milliseconds m1;
    Milliseconds m2;

    m1 = 5;     // sec = 0, usec = 5000
    m2 = 6;     // sec = 0, usec = 6000
    EXPECT_TRUE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_FALSE(m1 == m2);
    EXPECT_FALSE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);

    m1 = 999;   // sec = 0, usec = 999000
    m2 = 1001;  // sec = 1, usec = 1000
    EXPECT_TRUE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_FALSE(m1 == m2);
    EXPECT_FALSE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);

    m1 = 1000;  // sec = 1, usec = 0
    m2 = 1001;  // sec = 1, usec = 1000
    EXPECT_TRUE(m1 < m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;
    EXPECT_TRUE(m1 <= m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;
    EXPECT_FALSE(m1 == m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;
    EXPECT_FALSE(m1 >= m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;
    EXPECT_FALSE(m1 > m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;

    m1 = 1;  // sec = 0, usec = 1000
    m2 = 1;  // sec = 0, usec = 1000
    EXPECT_FALSE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_TRUE(m1 == m2);
    EXPECT_TRUE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);

    m1 = 1050;  // sec = 1, usec = 50000
    m2 = 1050;  // sec = 1, usec = 50000
    EXPECT_FALSE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_TRUE(m1 == m2);
    EXPECT_TRUE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);
}

TEST_F(MillisecondsTest, ArithmeticOperators)
{
    Milliseconds m1(1500);
    Milliseconds m2(500);
    Milliseconds m3(30);
    Milliseconds result;

    result = m1 + m2;
    EXPECT_EQ(2000, result.getTime());

    result = m1 + m3;
    EXPECT_EQ(1530, result.getTime());

    result = m2 + m3;
    EXPECT_EQ(530, result.getTime());

    result = m1 + m2 + m3;
    EXPECT_EQ(2030, result.getTime());

    result = m1 - m2;
    EXPECT_EQ(1000, result.getTime());

    result = m1 - m3;
    EXPECT_EQ(1470, result.getTime());

    result = m2 - m3;
    EXPECT_EQ(470, result.getTime());

    result = m1 - m2 - m3;
    EXPECT_EQ(970, result.getTime());

    result = m3 - m1;
    EXPECT_EQ(0, result.getTime());

    result = m1 - m2 + m3;
    EXPECT_EQ(1030, result.getTime());

    // agora os que alteram m1

    m1 -= m2 + m3;
    EXPECT_EQ(970, m1.getTime());

    m2 += m1 - m3; // 500 + 970 - 30
    EXPECT_EQ(1440, m2.getTime());
}

TEST_F(MillisecondsTest, Comparison)
{
    ASSERT_TRUE(Milliseconds(5) == 5);
    ASSERT_TRUE(Milliseconds(5) >= 5);
    ASSERT_TRUE(Milliseconds(5) >= 4);
    ASSERT_TRUE(Milliseconds(5) <= 5);
    ASSERT_TRUE(Milliseconds(5) <= 6);
    ASSERT_TRUE(Milliseconds(5) > 4);
    ASSERT_TRUE(Milliseconds(5) < 6);

    ASSERT_TRUE(Milliseconds(5) == Milliseconds(5));
    ASSERT_TRUE(Milliseconds(5) >= Milliseconds(5));
    ASSERT_TRUE(Milliseconds(5) >= Milliseconds(4));
    ASSERT_TRUE(Milliseconds(5) <= Milliseconds(5));
    ASSERT_TRUE(Milliseconds(5) <= Milliseconds(6));
    ASSERT_TRUE(Milliseconds(5) > Milliseconds(4));
    ASSERT_TRUE(Milliseconds(5) < Milliseconds(6));
}