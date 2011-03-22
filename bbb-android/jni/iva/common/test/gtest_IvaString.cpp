#include "IvaString.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class IvaStringTest :
    public ::testing::Test
  {

  protected:
    
    IvaStringTest()
    {

      
      
    }

    virtual ~IvaStringTest() {
      
      

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }
    
  };
    
};

TEST_F(IvaStringTest, CreateNoParameters) {

  IvaString teste1;

  cout << teste1 << endl;

  EXPECT_TRUE(teste1 == "") << "String diferente do que deveria: " << teste1;

}

TEST_F(IvaStringTest, CreateCString) {

  IvaString teste1("Testando");

  cout << teste1 << endl;

  EXPECT_TRUE(teste1 == "Testando")  << "String diferente do que deveria: " << teste1;

}

TEST_F(IvaStringTest, CreateCPPString) {

  string teste1("Testando");
  IvaString teste2(teste1);

  cout << teste2 << endl;

  EXPECT_TRUE(teste2 == "Testando") << "String diferente do que deveria: " << teste2;

}

TEST_F(IvaStringTest, CreateNumCharsString) {

  IvaString teste1(5,'t');

  cout << teste1 << endl;

  EXPECT_TRUE(teste1 == "ttttt")  << "String diferente do que deveria: " << teste1;

}

TEST_F(IvaStringTest, CreateInteger) {

  IvaString teste1(1345);

  cout << teste1 << endl;

  EXPECT_TRUE(teste1 == "1345")  << "String diferente do que deveria: " << teste1;
  
}

TEST_F(IvaStringTest, lTrim) {

  IvaString teste1("    Testando       ");

  teste1.lTrim();

  cout << teste1 << endl;

  EXPECT_TRUE(teste1 == "Testando       ")  << "String diferente do que deveria: " << teste1;
  
}

TEST_F(IvaStringTest, rTrim) {

  IvaString teste1("    Testando       ");

  teste1.rTrim();

  cout << teste1 << endl;

  EXPECT_TRUE(teste1 == "    Testando")  << "String diferente do que deveria: " << teste1;
  
}

TEST_F(IvaStringTest, trim) {

  IvaString teste1("    Testando       ");

  teste1.trim();

  cout << teste1 << endl;

  EXPECT_TRUE(teste1 == "Testando")  << "String diferente do que deveria: " << teste1;
  
}

TEST_F(IvaStringTest, TestWChar) {

  IvaString teste1("Testando");

  cout << teste1 << endl;

  wchar_t * arr;
  arr = teste1.toWchar();
  
  for (int i = 0; arr[i] != 0; i++) {

    cout << arr[i] << " ";

  };

  cout << endl;

  //  cout << sizeof(wchar_t) << endl;

  EXPECT_TRUE(teste1 == "Testando")  << "String diferente do que deveria: " << teste1;
  
}

TEST_F(IvaStringTest, TestSplit) {

  IvaString teste1("13.14.15.16");
  vector<IvaString> teste2;
  
  teste1.split('.',teste2);

  cout << teste2[0] << ",";
  cout << teste2[1] << ",";
  cout << teste2[2] << ",";
  cout << teste2[3] << endl;
  

  EXPECT_TRUE(teste2[0] == "13")  << "String diferente do que deveria: " << teste1;
  EXPECT_TRUE(teste2[1] == "14")  << "String diferente do que deveria: " << teste1;
  EXPECT_TRUE(teste2[2] == "15")  << "String diferente do que deveria: " << teste1;
  EXPECT_TRUE(teste2[3] == "16")  << "String diferente do que deveria: " << teste1;
  
}

TEST_F(IvaStringTest, TestSplitChars) {

  IvaString teste1("13_14.15_16.17");
  vector<IvaString> teste2;
  
  teste1.split((char *) "._",teste2);

  cout << teste2[0] << ",";
  cout << teste2[1] << ",";
  cout << teste2[2] << ",";
  cout << teste2[3] << ",";
  cout << teste2[4] << endl;
  

  EXPECT_TRUE(teste2[0] == "13")  << "String diferente do que deveria: " << teste1;
  EXPECT_TRUE(teste2[1] == "14")  << "String diferente do que deveria: " << teste1;
  EXPECT_TRUE(teste2[2] == "15")  << "String diferente do que deveria: " << teste1;
  EXPECT_TRUE(teste2[3] == "16")  << "String diferente do que deveria: " << teste1;
  EXPECT_TRUE(teste2[4] == "17")  << "String diferente do que deveria: " << teste1;
  
}

TEST_F(IvaStringTest, TestGetInt) {

  IvaString teste1("13");
  
  int a = teste1.getInt();
  
  cout << a << endl;

  //EXPECT_TRUE(teste1 == "Testando")  << "String diferente do que deveria: " << teste1;
  
}

