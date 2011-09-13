#include "CommonLeaks.h"
#include "CommonColor.h"
#include <sstream>
#include <iomanip>
#include "CommonLeaksCpp.h"

using namespace std;

CommonColor::CommonColor()
{
    r = 0;
    g = 0;
    b = 0;
    alpha = 255;
}

CommonColor::~CommonColor()
{
}

uint32_t CommonColor::toInt()
{
    int ret = alpha;
    ret = (ret << 8) + r;
    ret = (ret << 8) + g;
    ret = (ret << 8) + b;
    return ret;
}

void CommonColor::fromInt(uint32_t value)
{
    b = value & 0xFF;
    g = (value & 0xFF00) >> 8;
    r = (value & 0xFF0000) >> 16;
    alpha = (value & 0xFF000000) >> 24;
}

uint8_t CommonColor::getRed()
{
    return r;
}

uint8_t CommonColor::getGreen()
{
    return g;
}

uint8_t CommonColor::getBlue()
{
    return b;
}

uint8_t CommonColor::getAlpha()
{
    return alpha;
}

void CommonColor::setRed(uint8_t value)
{
    r = value;
}

void CommonColor::setBlue(uint8_t value)
{
    b = value;
}

void CommonColor::setGreen(uint8_t value)
{
    g = value;
}

void CommonColor::setAlpha(uint8_t value)
{
    alpha = value;
}

void CommonColor::set(uint8_t r, uint8_t g, uint8_t b, uint8_t a)
{
    setRed(r);
    setGreen(g);
    setBlue(b);
    setAlpha(a);
}

void CommonColor::fromHexStr(string &value)
{
    uint32_t x;
    std::stringstream ss;
    ss << std::hex << value;
    ss >> x;
    fromInt(x);
}

string CommonColor::toHexStr()
{
    uint32_t x = toInt();
    std::stringstream ss;
    ss << std::uppercase;
    ss << setfill('0') << setw(8);
    ss << std::hex << x;
    return ss.str();
}

bool CommonColor::operator==(const CommonColor &operand)
{
    return (r == operand.r) &&
           (g == operand.g) &&
           (b == operand.b) &&
           (alpha == operand.alpha);
}

