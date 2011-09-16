#ifndef _GTEST_MILLISSECOND_H_
#define _GTEST_MILLISSECOND_H_

#include <Milliseconds.h>
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
using namespace std;

namespace {

  class MillisecondsTest :
    public ::testing::Test
  {

  protected:
    
    MillisecondsTest();

    virtual ~MillisecondsTest();
    
    virtual void SetUp();

    virtual void TearDown();

  };

};

#endif

