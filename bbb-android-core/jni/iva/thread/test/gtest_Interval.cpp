#include "gtest_Interval.h"
#include <Interval.h>
#include <Milliseconds.h>

IntervalTest::IntervalTest()
{
}

IntervalTest::~IntervalTest() 
{
}

void IntervalTest::SetUp() 
{
}

void IntervalTest::TearDown() 
{
}

TEST_F(IntervalTest, CreateAndDestroy)
{
    Interval *i = new Interval();
    delete i;
}


TEST_F(IntervalTest, ComparingOperators)
{
    Interval m1;
    Interval m2;

    m1.setInterval(0, 5000);
    m2.setInterval(0, 6000);
    EXPECT_TRUE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_FALSE(m1 == m2);
    EXPECT_FALSE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);

    m1.setInterval(0, 999000);
    m2.setInterval(1, 0);
    EXPECT_TRUE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_FALSE(m1 == m2);
    EXPECT_FALSE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);

    m1.setInterval(1, 0);
    m2.setInterval(1, 1);
    EXPECT_TRUE(m1 < m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;
    EXPECT_TRUE(m1 <= m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;
    EXPECT_FALSE(m1 == m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;
    EXPECT_FALSE(m1 >= m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;
    EXPECT_FALSE(m1 > m2) << "m1.sec_ = " << m1.getSeconds() << " m1.usec_ = " << m1.getMicroseconds() << " m2.sec_ = " << m2.getSeconds() << " m2.usec_ = " << m2.getMicroseconds() << endl;;

    m1.setInterval(1, 1000);
    m2.setInterval(1, 1000);
    EXPECT_FALSE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_TRUE(m1 == m2);
    EXPECT_TRUE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);

    m1.setInterval(10, 50000);
    m2.setInterval(10, 50000);
    EXPECT_FALSE(m1 < m2);
    EXPECT_TRUE(m1 <= m2);
    EXPECT_TRUE(m1 == m2);
    EXPECT_TRUE(m1 >= m2);
    EXPECT_FALSE(m1 > m2);
}

TEST_F(IntervalTest, ArithmeticOperators)
{
    Interval m1(1, 500000); // 1,5 s
    Interval m2(0, 500000); // 0,5 s
    Interval m3(0, 30000);  // 0,03 s
    Interval result;

    result = m1 + m2;
    EXPECT_EQ(2, result.getSeconds());
    EXPECT_EQ(0, result.getMicroseconds());

    result = m1 + m3;
    EXPECT_EQ(1, result.getSeconds());
    EXPECT_EQ(530000, result.getMicroseconds());

    result = m2 + m3;
    EXPECT_EQ(0, result.getSeconds());
    EXPECT_EQ(530000, result.getMicroseconds());

    result = m1 + m2 + m3;
    EXPECT_EQ(2, result.getSeconds());
    EXPECT_EQ(30000, result.getMicroseconds());

    result = m1 - m2;
    EXPECT_EQ(1, result.getSeconds());
    EXPECT_EQ(0, result.getMicroseconds());

    result = m1 - m3;
    EXPECT_EQ(1, result.getSeconds());
    EXPECT_EQ(470000, result.getMicroseconds());

    result = m2 - m3;
    EXPECT_EQ(0, result.getSeconds());
    EXPECT_EQ(470000, result.getMicroseconds());

    result = m1 - m2 - m3; // 1,5 - 0,5 - 0,03
    EXPECT_EQ(0, result.getSeconds());
    EXPECT_EQ(970000, result.getMicroseconds());

    result = m3 - m1;
    EXPECT_EQ(0, result.getSeconds());
    EXPECT_EQ(0, result.getMicroseconds());

    result = m1 - m2 + m3;
    EXPECT_EQ(1, result.getSeconds());
    EXPECT_EQ(30000, result.getMicroseconds());

    // agora os que alteram m1

    m1 -= m2 + m3; // 1,5 - (0,5 + 0,03)
    EXPECT_EQ(0, m1.getSeconds());
    EXPECT_EQ(970000, m1.getMicroseconds());

    m2 += m1 - m3; // 500 + 970 - 30
    EXPECT_EQ(1, m2.getSeconds());
    EXPECT_EQ(440000, m2.getMicroseconds());
}

TEST_F(IntervalTest, Sleep)
{
    Milliseconds now;
    Interval(0, 20000).sleep();
    Milliseconds now2;

    Milliseconds diffti = now2 - now;

    EXPECT_GE(diffti.getTime(), (20 * (1-SLEEP_ERROR)));
    EXPECT_LE(diffti.getTime(), (20 * (1+SLEEP_ERROR)));
    cout << diffti.getTime() << endl;
}

TEST_F(IntervalTest, SleepInSteps)
{
    bool flagRun = true;
    Interval step(0, 10000); // 10ms

    Milliseconds now;
    Interval(1, 500000).sleep(step, flagRun); // 1,5s
    Milliseconds now2;

    Milliseconds diffti = now2 - now;

    EXPECT_GE(diffti.getTime(), (1500 * (1-SLEEP_ERROR)));
    EXPECT_LE(diffti.getTime(), (1500 * (1+SLEEP_ERROR)));
    cout << diffti.getTime() << endl;
}
