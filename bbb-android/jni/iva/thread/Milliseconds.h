#ifndef _MILLISECONDS_H_
#define _MILLISECONDS_H_

#include "Interval.h"

class Milliseconds 
: public Interval
{

 public:
  Milliseconds(int val);
  Milliseconds();
  Milliseconds(const Interval& val);
  ~Milliseconds();
  int getTime() const;

  int operator=(int val);
};

bool operator<=(const Milliseconds& m, int v);
bool operator<(const Milliseconds& m, int v);
bool operator==(const Milliseconds& m, int v);
bool operator>(const Milliseconds& m, int v);
bool operator>=(const Milliseconds& m, int v);

#endif
