#include <common.h>
#include <gtest/gtest.h>
#include <string>
using namespace std;

namespace {

    class CommonColorTest : public ::testing::Test
    {

    protected:

        CommonColorTest()
        {
        }

        virtual ~CommonColorTest()
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

TEST_F(CommonColorTest, Constructor)
{
    CommonColor color;
    EXPECT_EQ(0, color.getRed());
    EXPECT_EQ(0, color.getGreen());
    EXPECT_EQ(0, color.getBlue());
    EXPECT_EQ(255, color.getAlpha());
}

TEST_F(CommonColorTest, GettersAndSetters)
{
    CommonColor color;

    srand(Milliseconds().getTime());

    for (int i = 0; i < 1000; i++) {
        uint8_t r = rand() % 256;
        uint8_t g = rand() % 256;
        uint8_t b = rand() % 256;
        uint8_t a = rand() % 256;

        color.setRed(r);
        color.setGreen(g);
        color.setBlue(b);
        color.setAlpha(a);

        EXPECT_EQ(r, color.getRed());
        EXPECT_EQ(g, color.getGreen());
        EXPECT_EQ(b, color.getBlue());
        EXPECT_EQ(a, color.getAlpha());
    }

    for (int i = 0; i < 1000; i++) {
        uint8_t r = rand() % 256;
        uint8_t g = rand() % 256;
        uint8_t b = rand() % 256;
        uint8_t a = rand() % 256;

        color.set(r, g, b, a);

        EXPECT_EQ(r, color.getRed());
        EXPECT_EQ(g, color.getGreen());
        EXPECT_EQ(b, color.getBlue());
        EXPECT_EQ(a, color.getAlpha());
    }
}

TEST_F(CommonColorTest, ToInt)
{
    CommonColor color;
    uint32_t ret;

    ret = color.toInt();
    EXPECT_EQ(4278190080, ret);

    color.setRed(15);
    color.setGreen(230);
    color.setBlue(120);
    color.setAlpha(80);
    ret = color.toInt();
    EXPECT_EQ(1343219320, ret);

    color.setRed(255);
    color.setGreen(255);
    color.setBlue(255);
    color.setAlpha(255);
    ret = color.toInt();
    EXPECT_EQ(4294967295, ret);
}

TEST_F(CommonColorTest, FromInt)
{
    CommonColor color;

    color.fromInt(4278190080);
    EXPECT_EQ(0, color.getRed());
    EXPECT_EQ(0, color.getGreen());
    EXPECT_EQ(0, color.getBlue());
    EXPECT_EQ(255, color.getAlpha());

    color.fromInt(1343219320);
    EXPECT_EQ(15, color.getRed());
    EXPECT_EQ(230, color.getGreen());
    EXPECT_EQ(120, color.getBlue());
    EXPECT_EQ(80, color.getAlpha());

    color.fromInt(4294967295);
    EXPECT_EQ(255, color.getRed());
    EXPECT_EQ(255, color.getGreen());
    EXPECT_EQ(255, color.getBlue());
    EXPECT_EQ(255, color.getAlpha());
}

TEST_F(CommonColorTest, FromHexStr)
{
    CommonColor color;

    color.fromHexStr(string("FF000000"));
    EXPECT_EQ(0, color.getRed());
    EXPECT_EQ(0, color.getGreen());
    EXPECT_EQ(0, color.getBlue());
    EXPECT_EQ(255, color.getAlpha());

    color.fromHexStr(string("00FFFFFF"));
    EXPECT_EQ(255, color.getRed());
    EXPECT_EQ(255, color.getGreen());
    EXPECT_EQ(255, color.getBlue());
    EXPECT_EQ(0, color.getAlpha());

    color.fromHexStr(string("A0885544"));
    EXPECT_EQ(136, color.getRed());
    EXPECT_EQ(85, color.getGreen());
    EXPECT_EQ(68, color.getBlue());
    EXPECT_EQ(160, color.getAlpha());
}

TEST_F(CommonColorTest, ToHexStr)
{
    CommonColor color;
    string ret;

    ret = color.toHexStr();
    EXPECT_STREQ(ret.c_str(), string("FF000000").c_str());

    color.setRed(255);
    color.setGreen(255);
    color.setBlue(255);
    color.setAlpha(0);
    ret = color.toHexStr();
    EXPECT_STREQ(ret.c_str(), string("00FFFFFF").c_str());

    color.setRed(136);
    color.setGreen(85);
    color.setBlue(68);
    color.setAlpha(160);
    ret = color.toHexStr();
    EXPECT_STREQ(ret.c_str(), string("A0885544").c_str());
}

TEST_F(CommonColorTest, Operators)
{
    CommonColor color1, color2;

    color1.fromHexStr(string("00000000"));
    color2.fromHexStr(string("00000000"));
    EXPECT_TRUE(color1 == color2);

    color1.fromHexStr(string("12345678"));
    color2.fromHexStr(string("12345678"));
    EXPECT_TRUE(color1 == color2);

    color1.fromHexStr(string("FF000000"));
    color2.fromHexStr(string("FF0000FF"));
    EXPECT_FALSE(color1 == color2);

    color1.fromHexStr(string("00000000"));
    color2.fromHexStr(string("12345678"));
    EXPECT_FALSE(color1 == color2);
}
