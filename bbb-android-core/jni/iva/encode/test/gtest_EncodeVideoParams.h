#ifndef _GTEST_ENCODE_VIDEO_PARAMS_H_
#define _GTEST_ENCODE_VIDEO_PARAMS_H_

#include <gtest/gtest.h>
#include <common.h>
#include <EncodeVideoParams.h>

class EncodeVideoParamsTest : public ::testing::Test
{
protected:
    EncodeVideoParams * _params;

    virtual void SetUp()
    {
        _params = new EncodeVideoParams();
    }

    virtual void TearDown()
    {
        delete _params;
    }

};

#endif
