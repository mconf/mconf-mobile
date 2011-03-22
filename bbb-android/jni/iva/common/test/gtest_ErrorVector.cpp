#include "ErrorVector.h"
#include "errorDefs.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class ErrorVectorTest :
    public ::testing::Test
  {

  protected:
    
    ErrorVectorTest()
    {

      
      
    }

    virtual ~ErrorVectorTest() {
      
      

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }
    
  };
    
};

TEST_F(ErrorVectorTest, CreateCopyAndGetData) {

  ErrorVector teste1;
  
  int val = (ERROR_LIBS_COMMON * 10000) + ERROR_COMMON_UNKNOWN_ERROR;

  IvaString res;
  res = teste1.codeToMessage(val);
  
  cout << res << endl;
  
  EXPECT_TRUE(res == "Erro inesperado");
  
};

TEST_F(ErrorVectorTest, CreateCopyAndGetDataOnlyMacro) {

  ErrorVector teste1;
  
  int val = E_COMMON_UNKNOWN_ERROR;

  IvaString res;
  res = teste1[val];
  
  cout << res << endl;
  
  EXPECT_TRUE(res == "Erro inesperado");
  
};

