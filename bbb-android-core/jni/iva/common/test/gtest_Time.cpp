#include "IvaTime.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class TimeTest :
    public ::testing::Test
  {

  protected:
    
    TimeTest()
    {

      
      
    }

    virtual ~TimeTest() {
      
      

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }

  public:
    
  };
    
};

TEST_F(TimeTest, CreateNow) {

  IvaTime teste1;

  cout << teste1 << endl;

  EXPECT_TRUE((teste1.getYear() >= 1900) && (teste1.getYear() <= 2100));
  EXPECT_TRUE((teste1.getMonth() >= 1) && (teste1.getMonth() <= 12));
  EXPECT_TRUE((teste1.getDay() >= 1) && (teste1.getDay() <= 31)); 
  EXPECT_TRUE((teste1.getHour() >= 0) && (teste1.getHour() <= 23));
  EXPECT_TRUE((teste1.getMinute() >= 0) && (teste1.getMinute() <= 59));
  EXPECT_TRUE((teste1.getSecond() >= 0) && (teste1.getSecond() <= 59));

}

TEST_F(TimeTest, CreateCopy) {

  IvaTime teste2;
  IvaTime teste1 = teste2;

  cout << teste1 << endl;

  EXPECT_TRUE((teste1.getYear() >= 1900) && (teste1.getYear() <= 2100));
  EXPECT_TRUE((teste1.getMonth() >= 1) && (teste1.getMonth() <= 12));
  EXPECT_TRUE((teste1.getDay() >= 1) && (teste1.getDay() <= 31)); 
  EXPECT_TRUE((teste1.getHour() >= 0) && (teste1.getHour() <= 23));
  EXPECT_TRUE((teste1.getMinute() >= 0) && (teste1.getMinute() <= 59));
  EXPECT_TRUE((teste1.getSecond() >= 0) && (teste1.getSecond() <= 59));

}

TEST_F(TimeTest, CreateFromData) {

  IvaTime teste1(2010,02,10,23,04,10);

  stringstream a;
  
  a << teste1;

  cout << a.str() << endl;

  EXPECT_TRUE(a.str() == "[2010/02/10] [23:04:10]");
  EXPECT_TRUE(teste1.getYear() == 2010);
  EXPECT_TRUE(teste1.getMonth() == 02);
  EXPECT_TRUE(teste1.getDay() == 10); 
  EXPECT_TRUE(teste1.getHour() == 23);
  EXPECT_TRUE(teste1.getMinute() == 04);
  EXPECT_TRUE(teste1.getSecond() == 10);

}

TEST_F(TimeTest, DiffTimes) {
  
  IvaTime teste1(2010,02,10,23,04,10);
  IvaTime teste2(2010,02,10,23,05,30);

  cout << teste2-teste1 << endl;
  
  EXPECT_TRUE((teste2-teste1)==80);

}

