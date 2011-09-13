#ifndef _GTEST_DECODE_H_
#define _GTEST_DECODE_H_

#include <gtest/gtest.h>
#include <Decode.h>

/// \todo Decode é uma classe abstrata. Como instanciar ela pra testar alguns métodos?
class DecodeTest : public ::testing::Test
{
protected:
    //Decode * _decode;

    virtual void SetUp()
    {
        //_decode = new Decode();
    }

    virtual void TearDown()
    {
        //delete _decode;
    }

};

#endif
