#include "ErrorData.h"
#include "ErrorController.h"
#include "Location.h"
#include "IvaOutStream.h"
#include "IvaOutController.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class ErrorTest :
    public ::testing::Test
  {

  protected:
    
    ErrorTest()
    {

      errorContext.clean();
      
    }

    virtual ~ErrorTest() {
      
      ivaOutControllerCtx.close();

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }
    
  };
    
};

TEST_F(ErrorTest, CreateAndGetData) {

  ErrorData newError(1,Location(AT),"Testando esta bagaca: 134 Teste");
  IvaString line = IvaString(__LINE__ - 1);

  IvaString x = newError.getMsg();

  cout << x << endl;

  EXPECT_TRUE(x == "Testando esta bagaca: 134 Teste");

  int a = newError.getCode();

  cout << a << endl;

  EXPECT_TRUE(a == 1);

  x = *(newError.getLocation().getDebugLine());

  cout << x << endl;

#ifdef _MSC_VER
  IvaString s("test/gtest_Error.cpp:ErrorTest_CreateAndGetData_Test::TestBody:");
#else
  IvaString s("test/gtest_Error.cpp:TestBody:");
#endif
  s += line;
  s.trim();
  x.trim();
  EXPECT_STRCASEEQ(x.c_str(), s.c_str());

}

TEST_F(ErrorTest, PushAndBackWarning) {

  NEW_WARNING(2,"Testando esta bagaca: 134 Teste");
  NEW_WARNING(1,"Testando esta bagaca: 132 Teste");
  IvaString line(__LINE__-1);

  ErrorData newError;

  newError.getLast();

  IvaString x = newError.getMsg();

  cout << x << endl;

  EXPECT_TRUE(x == "Testando esta bagaca: 132 Teste");

  int a = newError.getType();

  cout << a << endl;

  EXPECT_TRUE(a == ERROR_TYPE_WARNING);

  a = newError.getCode();
  cout << a << endl;

  EXPECT_TRUE(a == 1);

  x = *((newError.getLocation()).getDebugLine());

  cout << x << endl;

#ifdef _MSC_VER
  IvaString s("test/gtest_Error.cpp:ErrorTest_PushAndBackWarning_Test::TestBody:");
#else
  IvaString s("test/gtest_Error.cpp:TestBody:");
#endif
  s += line;
  s.trim();
  x.trim();
  EXPECT_STRCASEEQ(x.c_str(), s.c_str());

}

TEST_F(ErrorTest, PushAndBackError) {

  ErrorData newErrora(1,Location(AT));
  ErrorData newErrorb(2,Location(AT));
  IvaString line = IvaString(__LINE__ - 1
		  );

  newErrora << "Testando esta bagaca: " << 132 << " Teste";
  newErrorb << "Testando esta bagaca: " << 134 << " Teste";

  newErrora.pushError();
  newErrorb.pushError();

  ErrorData newError;

  newError.getLast();

  IvaString x = newError.getMsg();

  cout << x << endl;

  EXPECT_TRUE(x == "Testando esta bagaca: 134 Teste");

  int a = newError.getType();

  cout << a << endl;

  EXPECT_TRUE(a == ERROR_TYPE_ERROR);

  a = newError.getCode();
  cout << a << endl;

  EXPECT_TRUE(a == 2);

  x = *((newError.getLocation()).getDebugLine());

  cout << x << endl;

#ifdef _MSC_VER
  IvaString s("test/gtest_Error.cpp:ErrorTest_PushAndBackError_Test::TestBody:");
#else
  IvaString s("test/gtest_Error.cpp:TestBody:");
#endif
  s += line;
  s.trim();
  x.trim();
  EXPECT_STRCASEEQ(x.c_str(), s.c_str());

}

TEST_F(ErrorTest, PrintQueue) {

  ErrorData newErrora(1,Location(AT));
  ErrorData newErrorb(2,Location(AT));
  ErrorData newErrorc(3,Location(AT));
  ErrorData newErrord(4,Location(AT));
  ErrorData newErrore(5,Location(AT));

  newErrora << "Testando essa bagaca: " << 1;
  newErrorb << "Testando essa bagaca: " << 2;
  newErrorc << "Testando essa bagaca: " << 3;
  newErrord << "Testando essa bagaca: " << 4;
  newErrore << "Testando essa bagaca: " << 5;

  newErrora.pushError();
  newErrorb.pushWarning();
  newErrorc.pushError();
  newErrord.pushError();
  newErrore.pushWarning();

  cout << ((ErrorStack) errorContext) << endl;

}

