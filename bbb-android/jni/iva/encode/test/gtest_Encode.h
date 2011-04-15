#ifndef _GTEST_ENCODE_H_
#define _GTEST_ENCODE_H_

#include <gtest/gtest.h>
#include <Encode.h>

/// \todo Encode é uma classe abstrata. Como instanciar ela pra testar alguns métodos?
class EncodeTest : public ::testing::Test
{
protected:
    //Encode * _encode;

    virtual void SetUp()
    {
        //_encode = new Encode();
    }

    virtual void TearDown()
    {
        //delete _encode;
    }

};

#endif
