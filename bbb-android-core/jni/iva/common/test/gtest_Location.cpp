#include "Location.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class LocationTest :
    public ::testing::Test
  {

  protected:
    
    LocationTest()
    {

      
      
    }

    virtual ~LocationTest() {
      
      

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }
    
  };
    
};

TEST_F(LocationTest, CreateAndGetData) {

  Location teste1("testando/a/classe/location.tst","testando()",123);
  
  IvaString a = *(teste1.getFile());
  cout << a << endl;

  EXPECT_TRUE(a == "classe/location.tst")  << "Valor diferente do que deveria: " << a;

  a = *(teste1.getFunction());
  cout << a << endl;

  EXPECT_TRUE(a == "testando()")  << "Valor diferente do que deveria: " << a;

  int b = teste1.getLine();

  cout << b << endl;

  EXPECT_TRUE(b == 123)  << "Valor diferente do que deveria: " << b;

  a = *(teste1.getDebugLine());

  cout << a << endl;
  cout << teste1 << endl;

  stringstream ss;
  ss << teste1;

  EXPECT_TRUE(a == "classe/location.tst:testando():123") << "Valor diferente do que deveria: " << a;
  EXPECT_TRUE(ss.str() == "classe/location.tst:testando():123") << "Valor diferente do que deveria: " 
								<< ss.str();

  a = *(teste1.getLib());

  cout << a << endl;

  EXPECT_TRUE(a == "classe") << "Valor diferente do que deveria: " << a;

}

TEST_F(LocationTest, CreateCopyAndGetData) {

  Location teste2("testando/a/classe/location.tst","testando()",123);
  Location teste1;
  
  teste1 = teste2;
  
  IvaString a = *(teste1.getFile());
  cout << a << endl;

  EXPECT_TRUE(a == "classe/location.tst")  << "Valor diferente do que deveria: " << a;

  a = *(teste1.getFunction());
  cout << a << endl;

  EXPECT_TRUE(a == "testando()")  << "Valor diferente do que deveria: " << a;

  int b = teste1.getLine();

  cout << b << endl;

  EXPECT_TRUE(b == 123)  << "Valor diferente do que deveria: " << b;

  a = *(teste1.getDebugLine());

  cout << a << endl;

  EXPECT_TRUE(a == "classe/location.tst:testando():123") << "Valor diferente do que deveria: " << a;
  
  a = *(teste1.getLib());

  cout << a << endl;

  EXPECT_TRUE(a == "classe") << "Valor diferente do que deveria: " << a;

}

TEST_F(LocationTest, EmptyAndGetData) {

  Location teste1;
  
  IvaString a = *(teste1.getFile());
  cout << a << endl;

  EXPECT_TRUE(a == "")  << "Valor diferente do que deveria: " << a;

  a = *(teste1.getFunction());
  cout << a << endl;

  EXPECT_TRUE(a == "")  << "Valor diferente do que deveria: " << a;

  int b = teste1.getLine();

  cout << b << endl;

  EXPECT_TRUE(b == 0)  << "Valor diferente do que deveria: " << b;

  a = *(teste1.getDebugLine());

  cout << a << endl;

  EXPECT_TRUE(a == "::0") << "Valor diferente do que deveria: " << a;

  a = *(teste1.getLib());

  cout << a << endl;

  EXPECT_TRUE(a == "") << "Valor diferente do que deveria: " << a;

}


TEST_F(LocationTest, CreateByAt) {

  IvaString s;
  Location teste1(AT);
  int L = __LINE__ - 1;
  
  IvaString a = *(teste1.getFile());
  cout << a << endl;

  s = "test/gtest_Location.cpp";
  EXPECT_STRCASEEQ(a.c_str(), s.c_str());

  a = *(teste1.getFunction());
  cout << a << endl;

#ifdef _MSC_VER
  s = "LocationTest_CreateByAt_Test::TestBody";
#else
  s = "TestBody";
#endif
  EXPECT_STRCASEEQ(a.c_str(), s.c_str());

  int b = teste1.getLine();

  cout << b << endl;

  EXPECT_TRUE(b == L)  << "Valor diferente do que deveria: " << a;

  IvaString c = *(teste1.getDebugLine());

  cout << "C--> [" << c << "]" << endl;

#ifdef _MSC_VER
  s = "test/gtest_Location.cpp:LocationTest_CreateByAt_Test::TestBody:";
#else
  s = "test/gtest_Location.cpp:TestBody:";
#endif
  s += IvaString(L);
  EXPECT_STRCASEEQ(c.c_str(), s.c_str());

  a = *(teste1.getLib());

  cout << a << endl;

  EXPECT_TRUE(a == "test") << "Valor diferente do que deveria: " << a;

}

