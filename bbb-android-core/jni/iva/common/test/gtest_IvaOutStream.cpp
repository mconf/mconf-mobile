#include "IvaOutStream.h"
#include "IvaOutController.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include "LogData.h"
using namespace std;

namespace {

  class IvaOutStreamTest :
    public ::testing::Test
  {

  protected:
    
    IvaOutStreamTest()
    {
    }

    virtual ~IvaOutStreamTest() 
    {
      
      ivaOutControllerCtx.close();
      
    }

    virtual void SetUp() 
    {
      
      
    }

    virtual void TearDown() 
    {
      
      

    }
    
  };
    
};

TEST_F(IvaOutStreamTest, CreateAndGetData2) {
    
  IvaOutStream Log;

  Log.start();

  Log << "testando esta bagaca: " << 121 << endl;
  Log << "testando esta bagaca: " << 122 << endl;
  Log << "testando esta bagaca: " << 123 << endl;
  Log << "testando esta bagaca: " << 124 << endl;
  Log << "testando esta bagaca: " << 125 << endl;

  Log.stop();

}

TEST_F(IvaOutStreamTest, CreateAndGetData) {

  IvaOutStream Teste;

  Teste.start();

  Teste << "testando esta bagaca: " << 126 << endl;
  Teste << "testando esta bagaca: " << 127 << endl;
  Teste << "testando esta bagaca: " << 128 << endl;
  Teste << "testando esta bagaca: " << 129 << endl;
  Teste << "testando esta bagaca: " << 130 << endl;
  
  Teste.stop();

}

TEST_F(IvaOutStreamTest, CreateAndGetDataFile) {

  IvaString newFile("newFileTest");
  IvaOutStream Teste(newFile);

  Teste << "testando esta bagaca: " << 120 << endl;
  Teste << "testando esta bagaca: " << 120 << endl;
  Teste << "testando esta bagaca: " << 120 << endl;

}

TEST_F(IvaOutStreamTest, LogDataTest) {

	LogData a;

	a << "testando esta bagaca: " << 120 << endl;

	IvaOutStream Teste;

	Teste << a;
	Teste.sync();

}

