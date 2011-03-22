#ifndef _COMMONCOLOR_H
#define _COMMONCOLOR_H

#include <stdint.h>
#include <string>
using namespace std;

class CommonColor {

public:
    CommonColor();
    ~CommonColor();

    uint8_t getRed();
    uint8_t getGreen();
    uint8_t getBlue();
    uint8_t getAlpha();
    void setRed(uint8_t value);
    void setBlue(uint8_t value);
    void setGreen(uint8_t value);
    void setAlpha(uint8_t value);

    void set(uint8_t r, uint8_t g, uint8_t b, uint8_t a);

    uint32_t toInt();
    void fromInt(uint32_t value);

    void fromHexStr(string &value);
    string toHexStr();

    bool operator==(const CommonColor &operand);

private:
    uint8_t r, g, b;
    uint8_t alpha;

};

#endif
