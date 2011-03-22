#include "LogData.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class LogDataTest :
    public ::testing::Test
  {

  protected:

    LogDataTest()
    {



    }

    virtual ~LogDataTest() {



    }

    virtual void SetUp() {


    }

    virtual void TearDown() {



    }

  };

};

TEST_F(LogDataTest, CreateAndPrint) {

  LogData newFi;

  newFi << "Teste" << "Teste" << "Teste" << endl;

  newFi << "Teste" << "Teste" << "Teste" << endl;

  newFi << "Teste" << "Teste" << "Teste" << endl;

  newFi << "Teste" << "Teste" << "Teste" << endl;

  newFi.push();

  LogData newFi2;

  newFi2 << "Teste" << "Teste" << "Teste" << endl;

  newFi2 << "Teste" << "Teste" << "Teste" << endl;

  newFi2 << "Teste" << "Teste" << "Teste" << endl;

  newFi2 << "Teste" << "Teste" << "Teste" << endl;

  newFi2.push();

};


