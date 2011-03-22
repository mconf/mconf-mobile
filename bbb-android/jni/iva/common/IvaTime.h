#ifndef _IVATIME_H_
#define _IVATIME_H_

#include <iostream>

using namespace std;

class IvaTime
{

 public:
  
  IvaTime();
  IvaTime(const IvaTime & cp);
  IvaTime(int year, int month, int day, int hour, int minute, int second);
  void init(int year, int month, int day, int hour, int minute, int second);

  ~IvaTime();

  IvaTime & operator= (IvaTime &operand);
  const int operator-(const IvaTime &right) const;

  friend ostream & operator<<(ostream & out, const IvaTime & toPrint);

  int getYear();
  int getMonth();
  int getDay();
  int getHour();
  int getMinute();
  int getSecond();  

 private:
  
  int year_;
  int month_;
  int day_;
  int hour_;
  int minute_;
  int second_;
  time_t now;

};

#endif
