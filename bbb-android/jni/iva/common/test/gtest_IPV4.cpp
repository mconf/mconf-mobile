#include "IPV4.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class IPV4Test :
    public ::testing::Test
  {

  protected:
    
    IPV4Test()
    {

      
      
    }

    virtual ~IPV4Test() {
      
      

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }
    
  };
    
};

TEST_F(IPV4Test, ThrowingConstructor) {
    ASSERT_ANY_THROW(IPv4("256.0.0.0"));
    ASSERT_ANY_THROW(IPv4("0.256.0.0"));
    ASSERT_ANY_THROW(IPv4("0.0.256.0"));
    ASSERT_ANY_THROW(IPv4("0.0.0.256"));

    ASSERT_ANY_THROW(IPv4("a.0.0.0"));
    ASSERT_ANY_THROW(IPv4("0.a.0.0"));
    ASSERT_ANY_THROW(IPv4("0.0.a.0"));
    ASSERT_ANY_THROW(IPv4("0.0.0.a"));

    ASSERT_ANY_THROW(IPv4(".0.0.0"));
    ASSERT_ANY_THROW(IPv4("0..0.0"));
    ASSERT_ANY_THROW(IPv4("0.0..0"));
    ASSERT_ANY_THROW(IPv4("0.0.0."));

    ASSERT_ANY_THROW(IPv4("..0.0"));
    ASSERT_ANY_THROW(IPv4("0...0"));
    ASSERT_ANY_THROW(IPv4("0.0.."));

    ASSERT_NO_THROW(IPv4("0.0.0.0"));
    ASSERT_NO_THROW(IPv4("255.255.255.255"));
}

TEST_F(IPV4Test, CreateAndGetBytes) {

  IPv4 teste1(143,54,132,210);

  int a,b,c,d;

  teste1.getBytes(&a,&b,&c,&d);

  cout << a << "." << b << "." << c << "." << d << endl;

  EXPECT_TRUE(a == 143) << "Valor diferente do que deveria: " << a;
  EXPECT_TRUE(b == 54) << "Valor diferente do que deveria: " << b;
  EXPECT_TRUE(c == 132) << "Valor diferente do que deveria: " << c;
  EXPECT_TRUE(d == 210) << "Valor diferente do que deveria: " << d;

}

TEST_F(IPV4Test, CreateAndGetString) {

  IPv4 teste1(143,54,132,210);

  IvaString a;

  teste1.getString(a);

  cout << a << endl;

  EXPECT_TRUE(a == "143.54.132.210") << "Valor diferente do que deveria: " << a;

}

TEST_F(IPV4Test, CreateAndGetInt) {

  IPv4 teste1(143,54,132,210);

  unsigned int a;

  a = teste1.getInteger();

  cout << a << endl;

  EXPECT_TRUE(a == 2402714834) << "Valor diferente do que deveria: " << a;

}

TEST_F(IPV4Test, CreateIntGet) {

  IPv4 teste1(2402714834);

  int a,b,c,d;

  teste1.getBytes(&a,&b,&c,&d);

  cout << a << "." << b << "." << c << "." << d << endl;

  EXPECT_TRUE(a == 143) << "Valor diferente do que deveria: " << a;
  EXPECT_TRUE(b == 54) << "Valor diferente do que deveria: " << b;
  EXPECT_TRUE(c == 132) << "Valor diferente do que deveria: " << c;
  EXPECT_TRUE(d == 210) << "Valor diferente do que deveria: " << d;

};

TEST_F(IPV4Test, CreateStringGet) {

  IvaString newS("143.54.132.210");
  IPv4 teste1(newS);

  int a,b,c,d;

  teste1.getBytes(&a,&b,&c,&d);

  cout << a << "." << b << "." << c << "." << d << endl;

  EXPECT_TRUE(a == 143) << "Valor diferente do que deveria: " << a;
  EXPECT_TRUE(b == 54) << "Valor diferente do que deveria: " << b;
  EXPECT_TRUE(c == 132) << "Valor diferente do que deveria: " << c;
  EXPECT_TRUE(d == 210) << "Valor diferente do que deveria: " << d;

};

