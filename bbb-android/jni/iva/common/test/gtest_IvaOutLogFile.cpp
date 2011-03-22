#include "IvaOutLogFile.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class IvaOutLogFileTest :
    public ::testing::Test
  {

  protected:
    
    IvaOutLogFileTest()
    {

      
      
    }

    virtual ~IvaOutLogFileTest() {
      
      

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }
  };
    
};

TEST_F(IvaOutLogFileTest, CreateAndPrint) {

  IvaOutLogFile newFi;
  IvaString file("newFileTest");
  IvaString teste("Testando o IvaOutLogFile\nE Testando de novo\n");

  newFi = file;

  newFi.print(teste);
  newFi.print(teste);
  newFi.print(teste);
  newFi.print(teste);

};

