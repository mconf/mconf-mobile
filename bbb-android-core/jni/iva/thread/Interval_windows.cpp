#include <CommonLeaks.h>
#include "Interval.h"
#include <Windows.h>

#include <iostream>
using namespace std;

#include <CommonLeaksCpp.h>

void Interval::setTimestamp()
{
    LARGE_INTEGER ts,freq;
    unsigned int timestamp = 0;

    // Query for the timestamp
    QueryPerformanceCounter(&ts);
    // se o hardware instalado suporta "high-resolution performance counter"
    // o valor de freq é diferente de zero
    QueryPerformanceFrequency(&freq);
    timestamp = (unsigned int) ((((double) ts.QuadPart) * 1000000.0) / ((double) freq.QuadPart));

    sec_ = timestamp / 1000000;
    usec_ = timestamp % 1000000;
}

Interval::Interval()
{
//  blocked_ = false;

  setTimestamp();

};
