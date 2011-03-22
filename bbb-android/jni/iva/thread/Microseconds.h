#ifndef _MICROSECONDS_H_
#define _MICROSECONDS_H_

#include "Interval.h"

class Microseconds 
: public Interval
{

 public:
  Microseconds(int val);
  Microseconds();
  Microseconds(const Interval& val);
  ~Microseconds();
  int getTime() const;

  int operator=(int val);
};

bool operator<=(const Microseconds& m, int v);
bool operator<(const Microseconds& m, int v);
bool operator==(const Microseconds& m, int v);
bool operator>(const Microseconds& m, int v);
bool operator>=(const Microseconds& m, int v);

#endif
