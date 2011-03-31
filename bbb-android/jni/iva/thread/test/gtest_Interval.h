#ifndef _GTEST_INTERVAL_H_
#define _GTEST_INTERVAL_H_

#include <Interval.h>
#include "gtest_main.h"
#include <gtest/gtest.h>
using namespace std;

namespace {

  class IntervalTest :
    public ::testing::Test
  {

  protected:
    
    IntervalTest();
    virtual ~IntervalTest();
    virtual void SetUp();
    virtual void TearDown();

  };

};

#endif

