#ifndef _SECONDS_H_
#define _SECONDS_H_

#include "Interval.h"

class Seconds 
: public Interval
{

 public:
  Seconds(int val);
  Seconds();
  Seconds(const Interval & val);
  ~Seconds();
  int getTime() const;

  int operator=(int val);
  Seconds operator-(const Interval& operand) const;
  Seconds operator+(const Interval& operand) const;
};

bool operator<=(const Seconds& m, int v);
bool operator<(const Seconds& m, int v);
bool operator==(const Seconds& m, int v);
bool operator>(const Seconds& m, int v);
bool operator>=(const Seconds& m, int v);

#endif
