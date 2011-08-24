#include "CommonLeaks.h"
#include "IvaTime.h"
#include "CommonLeaksCpp.h"

#ifdef _MSC_VER
#include <windows.h>
#endif

#include <time.h>
#include <sys/types.h>
#include <sys/timeb.h>

IvaTime::~IvaTime()
{

};

IvaTime::IvaTime()
{
  struct tm curr;
  time(&now);
#ifdef _MSC_VER
  localtime_s(&curr,&now);
#else
  localtime_r(&now,&curr);
#endif


  //  cout << curr.tm_year << endl;

  init((curr.tm_year) + 1900, (curr.tm_mon) + 1, curr.tm_mday,
       curr.tm_hour, curr.tm_min, curr.tm_sec);

};

IvaTime::IvaTime(const IvaTime & cp)
{

  init(((IvaTime &) cp).getYear(),((IvaTime &) cp).getMonth(),
       ((IvaTime &) cp).getDay(),((IvaTime &) cp).getHour(),
       ((IvaTime &) cp).getMinute(),((IvaTime &) cp).getSecond());
  now = cp.now;

};

void IvaTime::init(int year, int month, int day, int hour, int minute, int second)
{
  year_ = year;
  month_ = month;
  day_ = day;
  hour_ = hour;
  minute_ = minute;
  second_ = second;

};

IvaTime::IvaTime(int year, int month, int day, int hour, int minute, int second)
{

  struct tm curr;

  curr.tm_year = year-1900;
  curr.tm_mon = month -1;
  curr.tm_mday = day;
  curr.tm_hour = hour;
  curr.tm_min = minute;
  curr.tm_sec = second;

  now = mktime(&curr);

  init(year,month,day,hour,minute,second);
 
};

int IvaTime::getYear()
{
  return year_;
};

int IvaTime::getMonth()
{
  return month_;
};

int IvaTime::getDay()
{
  return day_;
};

int IvaTime::getHour()
{
  return hour_;
};

int IvaTime::getMinute()
{
  return minute_;
};

int IvaTime::getSecond()
{
  return second_;
};

const int IvaTime::operator-(const IvaTime &right) const
{

  return (int) difftime(now,right.now);

};

ostream & operator<<(ostream & out, const IvaTime & toPrint)
{

  out << "[";
  out.fill('0');
  out.width(4);
  out << ((IvaTime &) toPrint).getYear();
  out << "/";
  out.fill('0');
  out.width(2);
  out << ((IvaTime &) toPrint).getMonth();
  out << "/";
  out.fill('0');
  out.width(2);
  out << ((IvaTime &) toPrint).getDay();
  out << "] ";

  out << "[";
  out.fill('0');
  out.width(2);
  out << ((IvaTime &) toPrint).getHour();
  out << ":";
  out.fill('0');
  out.width(2);
  out << ((IvaTime &) toPrint).getMinute();
  out << ":";
  out.fill('0');
  out.width(2);
  out << ((IvaTime &) toPrint).getSecond();
  out << "]";

  return out;

};

