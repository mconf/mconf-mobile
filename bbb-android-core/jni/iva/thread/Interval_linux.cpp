#include "Interval.h"

void Interval::setTimestamp()
{
    
  struct timespec ts;
  clock_gettime(CLOCK_MONOTONIC,&ts);

  sec_ = ts.tv_sec;
  usec_ = ts.tv_nsec/1000;
  blocked_ = false;

}

Interval::Interval() {

  struct timespec ts;
  clock_gettime(CLOCK_MONOTONIC,&ts);

  sec_ = ts.tv_sec;
  usec_ = ts.tv_nsec/1000;
  blocked_ = false;

};
