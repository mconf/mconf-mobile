#include "gtest_Timer.h"
#include <Milliseconds.h>

#ifdef _MSC_VER
#include <winsock2.h>
#endif

void TimerTest::timerCallback()
{
    // calcula tempo que passou desde a última chamada ao timeout
    Milliseconds diff;
    diff -= _lastTimeout;
    _lastTimeout.setTimestamp();

    // dá um limite máximo de 2ms de erro para cima ou para baixo
    cout << "Timeout " << _timeouts << ", diff " << diff.getTime() << endl;
    EXPECT_TRUE((diff.getTime() <= _interval + 2) &&
        (diff.getTime() >= _interval - 2))
        << "Diferença: " << diff.getTime()
        << ", Intervalo " << _interval;
    _timeouts++;
}

TEST_F(TimerTest, CreateAndDestroy)
{
    Timer<TimerTest> * t = new Timer<TimerTest>(this, &TimerTest::timerCallback);
    delete t;
}

TEST_F(TimerTest, RunAndStop)
{
    Timer<TimerTest> t(this, &TimerTest::timerCallback);

    _timeouts = 0;
    _lastTimeout.setTimestamp();
    _interval = 500;
    t.start(_interval);
    Milliseconds(3000).sleep();
    EXPECT_TRUE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());

    _timeouts = 0;
    _lastTimeout.setTimestamp();
    _interval = 33;
    t.start(_interval);
    Milliseconds(1500).sleep();
    EXPECT_TRUE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());

    _timeouts = 0;
    _lastTimeout.setTimestamp();
    _interval = 666;
    t.start(_interval);
    Milliseconds(3500).sleep();
    EXPECT_TRUE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());
}

TEST_F(TimerTest, SingleShot)
{
    Timer<TimerTest> t(this, &TimerTest::timerCallback, true);

    _timeouts = 0;
    _lastTimeout.setTimestamp();
    _interval = 500;
    t.start(_interval);
    Milliseconds(2000).sleep();
    EXPECT_FALSE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());
    EXPECT_EQ(_timeouts, 1);
}








TEST_F(TimerBaseTest, CreateAndDestroy)
{
    MyTimerBase * t = new MyTimerBase();
    delete t;
}

TEST_F(TimerBaseTest, RunAndStop)
{
    MyTimerBase t;
    t.start(500);
    Milliseconds(3000).sleep();
    EXPECT_TRUE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());

    t.start(33);
    Milliseconds(1500).sleep();
    EXPECT_TRUE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());

    t.start(666);
    Milliseconds(3500).sleep();
    EXPECT_TRUE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());
}

TEST_F(TimerBaseTest, SingleShot)
{
    MyTimerBase t(true);
    t.start(500);
    Milliseconds(2000).sleep();
    EXPECT_FALSE(t.isActive());
    t.stop();
    EXPECT_FALSE(t.isActive());
    EXPECT_EQ(t.getTimeouts(), 1);
}

