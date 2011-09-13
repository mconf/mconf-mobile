#ifndef _COMMONRECT_H
#define _COMMONRECT_H

#include <stdint.h>

class CommonRect
{
public:
    CommonRect();
    ~CommonRect();

    void setX(int16_t value);
    void setY(int16_t value);
    void setWidth(uint16_t value);
    void setHeight(uint16_t value);
    int16_t getX();
    int16_t getY();
    uint16_t getWidth();
    uint16_t getHeight();

    void set(int16_t x, int16_t y, uint16_t w, uint16_t h);

    void copy(CommonRect & src);

    bool operator==(const CommonRect &operand);
    bool operator!=(const CommonRect &operand);

private:
    int16_t x, y;
    uint16_t w, h;

};

#endif
