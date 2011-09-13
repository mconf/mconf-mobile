#include <common.h>
#include <gtest/gtest.h>
#include <string>
using namespace std;

namespace {

    class CommonRectTest : public ::testing::Test
    {

    protected:

        CommonRectTest()
        {
        }

        virtual ~CommonRectTest()
        {
        }

        virtual void SetUp()
        {
        }

        virtual void TearDown()
        {
        }

    };

};

TEST_F(CommonRectTest, Constructor)
{
    CommonRect rect;
    EXPECT_EQ(0, rect.getX());
    EXPECT_EQ(0, rect.getY());
    EXPECT_EQ(0, rect.getWidth());
    EXPECT_EQ(0, rect.getHeight());
}

TEST_F(CommonRectTest, GettersAndSetters)
{
    CommonRect rect;

    srand(Milliseconds().getTime());

    for (int i = 0; i < 1000; i++) {
        int16_t  x = rand() % 65536;
        int16_t  y = rand() % 65536;
        uint16_t w = rand() % 65536;
        uint16_t h = rand() % 65536;

        rect.setX(x);
        rect.setY(y);
        rect.setWidth(w);
        rect.setHeight(h);

        EXPECT_EQ(x, rect.getX());
        EXPECT_EQ(y, rect.getY());
        EXPECT_EQ(w, rect.getWidth());
        EXPECT_EQ(h, rect.getHeight());
    }

    for (int i = 0; i < 1000; i++) {
        int16_t  x = rand() % 65536;
        int16_t  y = rand() % 65536;
        uint16_t w = rand() % 65536;
        uint16_t h = rand() % 65536;

        rect.set(x, y, w, h);

        EXPECT_EQ(x, rect.getX());
        EXPECT_EQ(y, rect.getY());
        EXPECT_EQ(w, rect.getWidth());
        EXPECT_EQ(h, rect.getHeight());
    }
}

TEST_F(CommonRectTest, Operators)
{
    CommonRect rect1, rect2;

    rect1.set(10, 10, 200, 200);
    rect2.set(-10, 10, 200, 200);
    EXPECT_TRUE(rect1 != rect2);
    EXPECT_FALSE(rect1 == rect2);

    rect1.set(10, 10, 200, 200);
    rect2.set(10, 55, 200, 200);
    EXPECT_TRUE(rect1 != rect2);
    EXPECT_FALSE(rect1 == rect2);

    rect1.set(10, 10, 200, 200);
    rect2.set(10, 10, 99, 200);
    EXPECT_TRUE(rect1 != rect2);
    EXPECT_FALSE(rect1 == rect2);

    rect1.set(10, 10, 200, 200);
    rect2.set(10, 10, 200, 0);
    EXPECT_TRUE(rect1 != rect2);
    EXPECT_FALSE(rect1 == rect2);

    rect1.set(10, 10, 200, 200);
    rect2.set(-123, 945, 432, 587);
    EXPECT_TRUE(rect1 != rect2);
    EXPECT_FALSE(rect1 == rect2);

    rect1.set(10, 10, 200, 200);
    rect2.set(10, 10, 200, 200);
    EXPECT_FALSE(rect1 != rect2);
    EXPECT_TRUE(rect1 == rect2);
}

TEST_F(CommonRectTest, Copy)
{
    CommonRect rect1, rect2;

    // inicialmente diferentes
    rect1.set(10, 10, 200, 200);
    rect2.set(0, 0, 0, 0);
    EXPECT_TRUE(rect1 != rect2);
    EXPECT_FALSE(rect1 == rect2);

    // agora devem se tornar iguais
    rect2.copy(rect1);
    EXPECT_FALSE(rect1 != rect2);
    EXPECT_TRUE(rect1 == rect2);

    // agora devem voltar a ser diferentes
    rect2.set(0, 0, 0, 0);
    EXPECT_TRUE(rect1 != rect2);
    EXPECT_FALSE(rect1 == rect2);
}

