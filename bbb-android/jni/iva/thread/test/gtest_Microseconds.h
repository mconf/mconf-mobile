#ifndef _GTEST_MICROSSECOND_H_
#define _GTEST_MICROSSECOND_H_

#include <Microseconds.h>
#include "gtest_main.h"
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
using namespace std;

namespace {

  class MicrosecondsTest :
    public ::testing::Test
  {

  protected:
    
    MicrosecondsTest();

    virtual ~MicrosecondsTest();
    
    virtual void SetUp();

    virtual void TearDown();

  };

};

#endif

