#ifndef _GTEST_AV_CONFIGS_H_
#define _GTEST_AV_CONFIGS_H_

#include <common.h>
#include <gtest/gtest.h>
#include <AVConfigs.h>
#include <list>
using namespace std;

class AVConfigsTest : public ::testing::Test
{
protected:
    AVConfigs _configs;

    virtual void SetUp()
    {
    }

    virtual void TearDown()
    {
    }

};

#endif
