#include <CommonLeaks.h>
#include "Milliseconds.h"
#include <CommonLeaksCpp.h>

Milliseconds::Milliseconds(int val)
  : Interval(val/1000,(val%1000)*1000)
{
  
};

Milliseconds::Milliseconds(const Interval & val)
  : Interval(val)
{
};

Milliseconds::Milliseconds() 
  : Interval()
{
  
};

int Milliseconds::getTime() const
{
  return (getSeconds()*1000) + (getMicroseconds()/1000);
};

Milliseconds::~Milliseconds()
{
  
};

int Milliseconds::operator=(int val)
{
    setInterval(val/1000, (val%1000)*1000);
    return val;
}

bool operator<=(const Milliseconds& m, int v)
{
    return m.getTime() <= v;
}

bool operator<(const Milliseconds& m, int v)
{
    return m.getTime() < v;
}

bool operator==(const Milliseconds& m, int v)
{
    return m.getTime() == v;
}

bool operator>(const Milliseconds& m, int v)
{
    return m.getTime() > v;
}

bool operator>=(const Milliseconds& m, int v)
{
    return m.getTime() >= v;
}

