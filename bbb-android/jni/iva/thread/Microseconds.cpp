#include <CommonLeaks.h>
#include "Microseconds.h"
#include <iostream>
using namespace std;
#include <CommonLeaksCpp.h>

Microseconds::Microseconds(int val)
  : Interval(val/1000000,val%1000000)
{
};

Microseconds::Microseconds()
  : Interval()
{  
};

Microseconds::~Microseconds()
{
};

int Microseconds::getTime() const
{
  return (getSeconds()*1000000) + getMicroseconds();
};

Microseconds::Microseconds(const Interval & val)
  : Interval(val)
{
};

int Microseconds::operator=(int val)
{
    setInterval(val/1000000, val%1000000);
    return val;
}

bool operator<=(const Microseconds& m, int v)
{
    return m.getTime() <= v;
}

bool operator<(const Microseconds& m, int v)
{
    return m.getTime() < v;
}

bool operator==(const Microseconds& m, int v)
{
    return m.getTime() == v;
}

bool operator>(const Microseconds& m, int v)
{
    return m.getTime() > v;
}

bool operator>=(const Microseconds& m, int v)
{
    return m.getTime() >= v;
}
