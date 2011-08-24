#include "CommonLeaks.h"
#include "CommonRect.h"
#include "CommonLeaksCpp.h"

CommonRect::CommonRect() 
{
    x = 0;
    y = 0;
    w = 0;
    h = 0;
}

void CommonRect::copy(CommonRect & src)
{
    x = src.getX();
    y = src.getY();
    w = src.getWidth();
    h = src.getHeight();
}

CommonRect::~CommonRect()
{
}

void CommonRect::setX(int16_t value)
{
    x = value;
}

void CommonRect::setY(int16_t value)
{
    y = value;
}

void CommonRect::setWidth(uint16_t value)
{
    w = value;
}

void CommonRect::setHeight(uint16_t value)
{
    h = value;
}

int16_t CommonRect::getX()
{
    return x;
}

int16_t CommonRect::getY()
{
    return y;
}

uint16_t CommonRect::getWidth()
{
    return w;
}

uint16_t CommonRect::getHeight()
{
    return h;
}

void CommonRect::set(int16_t x, int16_t y, uint16_t w, uint16_t h)
{
    setX(x);
    setY(y);
    setWidth(w);
    setHeight(h);
}

bool CommonRect::operator==(const CommonRect &operand)
{
    return (x == operand.x) &&
           (y == operand.y) &&
           (w == operand.w) &&
           (h == operand.h);
}

bool CommonRect::operator!=(const CommonRect &operand)
{
    return !(operator==(operand));
}

