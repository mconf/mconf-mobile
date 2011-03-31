#ifndef _GTEST_SECOND_H_
#define _GTEST_SECOND_H_

#include <Seconds.h>
#include "gtest_main.h"
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
using namespace std;

namespace {

  class SecondsTest :
    public ::testing::Test
  {

  protected:
    SecondsTest();

    virtual ~SecondsTest();
    virtual void SetUp();
    virtual void TearDown();
    void _CreateSecondsAndSleep();

  };

};

#endif
