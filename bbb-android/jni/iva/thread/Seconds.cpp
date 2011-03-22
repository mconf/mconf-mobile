#include <CommonLeaks.h>
#include "Seconds.h"
#include <iostream>
using namespace std;

#include <CommonLeaksCpp.h>

Seconds::Seconds(int val)
  : Interval(val,0)
{
};

Seconds::~Seconds()
{
};

Seconds::Seconds()
  : Interval()
{
};

int Seconds::getTime() const
{
	return getSeconds();
};

Seconds::Seconds(const Interval & val)
  : Interval(val)
{
};

int Seconds::operator=(int val)
{
    setInterval(val, 0);
    return val;
}

Seconds Seconds::operator-(const Interval & operand) const
{
    return Interval::operator -(operand);
}

Seconds Seconds::operator+(const Interval & operand) const
{
    return Interval::operator +(operand);
}

bool operator<=(const Seconds& m, int v)
{
    return m.getTime() <= v;
}

bool operator<(const Seconds& m, int v)
{
    return m.getTime() < v;
}

bool operator==(const Seconds& m, int v)
{
    return m.getTime() == v;
}

bool operator>(const Seconds& m, int v)
{
    return m.getTime() > v;
}

bool operator>=(const Seconds& m, int v)
{
    return m.getTime() >= v;
}

