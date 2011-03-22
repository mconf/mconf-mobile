#include <CommonLeaks.h>
#include "Interval.h"
#include <iostream>
using namespace std;

#ifdef _MSC_VER

#include <winsock2.h>

#else

#include <sys/socket.h>
#include <sys/stat.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <errno.h>
#include <unistd.h>

#define SOCKET int

#define closesocket close

#endif

#include <CommonLeaksCpp.h>

Interval::Interval(const Interval & val)
{

  sec_ = val.sec_;
  usec_ = val.usec_;
  blocked_ = false;

};

Interval::Interval(int sec, int usec)
{

  sec_ = sec;
  usec_ = usec;
  blocked_ = false;

};

Interval::~Interval()
{

  running_mutex_.lock();
  blocked_ = true;
  running_mutex_.unlock();

};

int Interval::getSeconds() const {
  return sec_;
};

int Interval::getMicroseconds() const {
  return usec_;
};

void Interval::setInterval(int sec, int usec)
{
  running_mutex_.lock();
  sec_ = sec;
  usec_ = usec;
  running_mutex_.unlock();
}

void Interval::fullSleep()
{

  struct timeval tv;
  fd_set readfds;
  SOCKET s=0;

  running_mutex_.lock();
  if (!blocked_) {
  
    if (((usec_ == 0) && (sec_ == 0)) || (usec_ < 0) || (sec_ < 0)) {
      running_mutex_.unlock();
      return;
    };
    
    tv.tv_sec = (long) sec_;
    tv.tv_usec = (long) usec_;

	//cout << tv.tv_sec << "," << tv.tv_usec << endl;

#ifdef _MSC_VER
    WORD wVersionRequested;
    WSADATA wsaData;
    wVersionRequested = MAKEWORD(2,2);
    WSAStartup(wVersionRequested, &wsaData);
#else

    if ((tv.tv_sec > 0) || (tv.tv_usec > 100000)) {
      if (tv.tv_usec < 1000) {
	if (tv.tv_sec > 0) {
	  tv.tv_sec--;
	  tv.tv_usec+=1000000;
	};
      };
      if (tv.tv_usec >= 1000) {
	tv.tv_usec -= 1000;
      };
    };

#endif

    s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    FD_ZERO(&readfds);
    FD_SET(s,&readfds);
    select((int)s, &readfds, NULL, NULL, &tv);
    closesocket(s);
  };
  running_mutex_.unlock();
}

void Interval::sleep()
{
    if (sec_ == 0 && usec_ < 5000) {
        fullSleep();
    } else {
        Interval step(0, 10000);
        sleep(step);
    }
}

Interval Interval::sleep(Interval &step)
{
    bool flagRun = true;
    return sleep(step, flagRun);
}

Interval Interval::sleep(Interval &step, bool &run)
{
    // se o passo é >= o tempo setado, dorme apenas o tempo setado
    if (step >= *this) {
        this->fullSleep();
        return *this;
    }

    Interval now, start;
    Interval final;

    final = start + *this; // tempo alvo (final), quando deve parar a execução

    while (now < final-step) { // para evitar dormir mais que o necessário
        step.fullSleep();
        now.setTimestamp();
        if (!run) return (now - start);
    }

    now.setTimestamp();
    final -= now;
    // se é menos de 0,5ms não dorme
    if (final.getSeconds() > 0 || final.getMicroseconds() > 500) {
        final.fullSleep();
    }

    return Interval() - start;
}

Interval Interval::operator-(const Interval & operand) const
{
    Interval toRet(*this);
    toRet -= operand;
    return toRet;
}

void Interval::operator-=(const Interval & operand)
{
    int sec, usec;

    if (usec_ < operand.usec_) {
        usec = usec_ - operand.usec_ + 1000000;
        sec = sec_-1;
    } else {
        usec = usec_ - operand.usec_;
        sec = sec_;
    }

    sec -= operand.sec_;

    if ((sec < 0) || (usec < 0)) {
        sec = 0;
        usec = 0;
    }

    sec_ = sec;
    usec_ = usec;
}

Interval Interval::operator+(const Interval & operand) const
{
    Interval toRet(*this);
    toRet += operand;
    return toRet;
}

void Interval::operator+=(const Interval & operand)
{
    int sec, usec;

    sec = sec_ + operand.sec_;
    usec = usec_ + operand.usec_;

    if (usec >= 1000000) {
        usec -= 1000000;
        sec += 1;
    }

    sec_ = sec;
    usec_ = usec;
}

bool Interval::operator>=(const Interval& operand) const
{
    return (operator>(operand) || operator==(operand));
}

bool Interval::operator>(const Interval& operand) const
{
    return (sec_ > operand.sec_ || (sec_ == operand.sec_ && usec_ > operand.usec_));
}

bool Interval::operator==(const Interval& operand) const
{
    return (sec_ == operand.sec_ && usec_ == operand.usec_);
}

bool Interval::operator<(const Interval& operand) const
{
    return (sec_ < operand.sec_ || (sec_ == operand.sec_ && usec_ < operand.usec_));
}

bool Interval::operator<=(const Interval& operand) const
{
    return (operator<(operand) || operator==(operand));
}
