#include "IvaOutController.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class IvaOutControllerTest :
    public ::testing::Test
  {

  protected:
    
    IvaOutControllerTest()
    {
      
      
      
    }

    virtual ~IvaOutControllerTest() 
    {
      
      
      
    }

    virtual void SetUp() 
    {
      
      
    }

    virtual void TearDown() 
    {
      
      

    }
    
  };
    
};

TEST_F(IvaOutControllerTest, CreateAndGetData) {

  IvaString teste("TESTANDO");

  ivaOutControllerCtx.print(teste);

}

TEST_F(IvaOutControllerTest, CreateSpecificFile) {

  IvaString teste("TESTANDO2");
  IvaString testeFile("newFileTest");

  ivaOutControllerCtx.print(testeFile,teste);

}

TEST_F(IvaOutControllerTest, lockUnlock) {
	ivaOutControllerCtx.lock();
	ivaOutControllerCtx.unlock();
}

